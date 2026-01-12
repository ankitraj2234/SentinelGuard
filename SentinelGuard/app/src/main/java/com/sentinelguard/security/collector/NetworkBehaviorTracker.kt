package com.sentinelguard.security.collector

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import com.sentinelguard.data.database.dao.BehavioralAnomalyDao
import com.sentinelguard.data.database.dao.KnownNetworkDao
import com.sentinelguard.data.database.entities.BehavioralAnomalyEntity
import com.sentinelguard.data.database.entities.KnownNetworkEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tracks network behavior for anomaly detection.
 * 
 * Features:
 * - Tracks connected WiFi networks
 * - Builds trusted network list
 * - Detects new/unknown networks
 * - Flags open (unsecured) networks
 */
@Singleton
class NetworkBehaviorTracker @Inject constructor(
    @ApplicationContext private val context: Context,
    private val knownNetworkDao: KnownNetworkDao,
    private val anomalyDao: BehavioralAnomalyDao
) {
    
    companion object {
        private const val NEW_NETWORK_RISK = 10
        private const val OPEN_NETWORK_RISK = 5
        private const val MIN_CONNECTIONS_FOR_TRUSTED = 3
    }
    
    /**
     * Record current network connection and check for anomalies.
     * Returns risk points if suspicious network detected.
     */
    suspend fun recordNetworkConnection(): Int = withContext(Dispatchers.IO) {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            ?: return@withContext 0
            
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        // Check if connected to WiFi
        val network = connectivityManager.activeNetwork ?: return@withContext 0
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return@withContext 0
        
        if (!capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            return@withContext 0 // Not on WiFi
        }
        
        @Suppress("DEPRECATION")
        val wifiInfo = wifiManager.connectionInfo
        val ssid = wifiInfo.ssid?.removeSurrounding("\"") ?: return@withContext 0
        
        if (ssid == "<unknown ssid>") {
            return@withContext 0 // WiFi info not available
        }
        
        val bssid = wifiInfo.bssid
        val timestamp = System.currentTimeMillis()
        
        var riskPoints = 0
        
        // Check if network is known
        val existingNetwork = knownNetworkDao.getNetwork(ssid)
        
        if (existingNetwork != null) {
            // Update existing network
            knownNetworkDao.incrementConnection(ssid, timestamp)
        } else {
            // New network - check for anomalies
            val hasEstablishedNetworks = knownNetworkDao.getTrustedNetworks().isNotEmpty()
            
            if (hasEstablishedNetworks) {
                riskPoints += NEW_NETWORK_RISK
                logAnomaly(
                    type = "NETWORK",
                    description = "Connected to new WiFi network: $ssid",
                    severity = 5,
                    riskPoints = NEW_NETWORK_RISK
                )
            }
            
            // Check if open network (we can't reliably detect this without more permissions)
            // For now, assume networks are secure unless proven otherwise
            
            // Add new network
            knownNetworkDao.insert(KnownNetworkEntity(
                ssid = ssid,
                bssid = bssid,
                isSecure = true, // Assume secure
                isTrusted = false, // Not trusted yet
                connectionCount = 1,
                lastConnected = timestamp
            ))
        }
        
        riskPoints
    }
    
    /**
     * Get current network SSID.
     */
    fun getCurrentNetwork(): String? {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            ?: return null
            
        @Suppress("DEPRECATION")
        val ssid = wifiManager.connectionInfo?.ssid?.removeSurrounding("\"")
        
        return if (ssid == "<unknown ssid>") null else ssid
    }
    
    /**
     * Check if current network is trusted.
     */
    suspend fun isOnTrustedNetwork(): Boolean {
        val ssid = getCurrentNetwork() ?: return false
        val network = knownNetworkDao.getNetwork(ssid) ?: return false
        return network.isTrusted && network.connectionCount >= MIN_CONNECTIONS_FOR_TRUSTED
    }
    
    /**
     * Get all known networks.
     */
    suspend fun getKnownNetworks(): List<KnownNetworkEntity> {
        return knownNetworkDao.getAllNetworks()
    }
    
    /**
     * Mark a network as trusted/untrusted.
     */
    suspend fun setNetworkTrusted(ssid: String, trusted: Boolean) {
        knownNetworkDao.setTrusted(ssid, trusted)
    }
    
    /**
     * Calculate learning progress for network tracking.
     */
    suspend fun getLearningProgress(): Float {
        val trustedNetworks = knownNetworkDao.getTrustedNetworks()
            .count { it.connectionCount >= MIN_CONNECTIONS_FOR_TRUSTED }
        
        // Consider "learned" when we have at least 2 trusted networks
        return (trustedNetworks / 2f).coerceIn(0f, 1f)
    }
    
    private suspend fun logAnomaly(
        type: String,
        description: String,
        severity: Int,
        riskPoints: Int
    ) {
        anomalyDao.insert(BehavioralAnomalyEntity(
            anomalyType = type,
            description = description,
            severity = severity,
            riskPoints = riskPoints
        ))
    }
}
