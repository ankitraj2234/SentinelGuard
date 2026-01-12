package com.sentinelguard.security.signal.detector

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.sentinelguard.data.local.preferences.SecurePreferences
import com.sentinelguard.domain.model.SecuritySignal
import com.sentinelguard.domain.model.SignalType
import com.sentinelguard.domain.util.SecureIdGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

/**
 * NetworkDetector: Monitors Network State and Changes
 * 
 * WHY THIS EXISTS:
 * Network changes can indicate:
 * - Device moved to new location (WiFi → Mobile)
 * - SIM swapped and connected to new network
 * - Airplane mode toggled (possible theft cover-up)
 * - VPN enabled (may indicate privacy-conscious use or tunneling)
 * 
 * SIGNALS PRODUCED:
 * - NETWORK_WIFI: Connected to WiFi
 * - NETWORK_MOBILE: Connected to mobile data
 * - NETWORK_NONE: No network connectivity
 * - NETWORK_CHANGE: Network type changed since last check
 */
class NetworkDetector(
    private val context: Context,
    private val securePreferences: SecurePreferences
) : Detector {

    override val name: String = "Network"

    private val connectivityManager = 
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _currentNetworkType = MutableStateFlow(NetworkType.NONE)
    val currentNetworkType: StateFlow<NetworkType> = _currentNetworkType.asStateFlow()

    private val _isVpnActive = MutableStateFlow(false)
    val isVpnActive: StateFlow<Boolean> = _isVpnActive.asStateFlow()

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    enum class NetworkType {
        WIFI, MOBILE, NONE, VPN, OTHER
    }

    init {
        registerNetworkCallback()
    }

    override suspend fun detect(): List<SecuritySignal> {
        val signals = mutableListOf<SecuritySignal>()
        val now = System.currentTimeMillis()

        // Detect current network state
        val currentType = getCurrentNetworkType()
        _currentNetworkType.value = currentType

        // Check for VPN
        val vpnActive = isVpnConnected()
        _isVpnActive.value = vpnActive

        // Create signal for current state
        val signalType = when (currentType) {
            NetworkType.WIFI -> SignalType.NETWORK_WIFI
            NetworkType.MOBILE -> SignalType.NETWORK_MOBILE
            NetworkType.NONE -> SignalType.NETWORK_NONE
            else -> SignalType.NETWORK_CHANGE
        }

        signals.add(
            SecuritySignal(
                id = SecureIdGenerator.generateId(),
                type = signalType,
                value = currentType.name,
                metadata = buildMetadata(currentType, vpnActive),
                timestamp = now
            )
        )

        // Check if network changed since last time
        val lastNetworkType = securePreferences.lastNetworkType
        if (lastNetworkType != null && lastNetworkType != currentType.name) {
            signals.add(
                SecuritySignal(
                    id = SecureIdGenerator.generateId(),
                    type = SignalType.NETWORK_CHANGE,
                    value = "${lastNetworkType} -> ${currentType.name}",
                    metadata = JSONObject().apply {
                        put("from", lastNetworkType)
                        put("to", currentType.name)
                    }.toString(),
                    timestamp = now
                )
            )
        }

        // Save current state
        securePreferences.lastNetworkType = currentType.name

        return signals
    }

    private fun getCurrentNetworkType(): NetworkType {
        val network = connectivityManager.activeNetwork ?: return NetworkType.NONE
        val capabilities = connectivityManager.getNetworkCapabilities(network) 
            ?: return NetworkType.NONE

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> NetworkType.VPN
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.MOBILE
            else -> NetworkType.OTHER
        }
    }

    private fun isVpnConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    }

    private fun registerNetworkCallback() {
        if (networkCallback != null) return

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _currentNetworkType.value = getCurrentNetworkType()
            }

            override fun onLost(network: Network) {
                _currentNetworkType.value = NetworkType.NONE
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                _currentNetworkType.value = getCurrentNetworkType()
                _isVpnActive.value = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
            }
        }

        try {
            connectivityManager.registerNetworkCallback(request, networkCallback!!)
        } catch (e: Exception) {
            // May fail on some devices
        }
    }

    fun unregister() {
        networkCallback?.let {
            try {
                connectivityManager.unregisterNetworkCallback(it)
            } catch (e: Exception) {
                // Ignore
            }
            networkCallback = null
        }
    }

    private fun buildMetadata(type: NetworkType, vpnActive: Boolean): String {
        return JSONObject().apply {
            put("networkType", type.name)
            put("vpnActive", vpnActive)
            put("isMetered", isMetered())
        }.toString()
    }

    private fun isMetered(): Boolean {
        return connectivityManager.isActiveNetworkMetered
    }

    override fun getDebugInfo(): Map<String, Any> {
        return mapOf(
            "currentType" to _currentNetworkType.value.name,
            "vpnActive" to _isVpnActive.value,
            "isMetered" to isMetered()
        )
    }
}
