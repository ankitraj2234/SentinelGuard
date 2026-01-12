package com.sentinelguard.security.baseline

import android.content.Context
import android.location.Location
import com.sentinelguard.data.database.dao.BehavioralAnomalyDao
import com.sentinelguard.data.database.dao.LocationClusterDao
import com.sentinelguard.data.database.entities.BehavioralAnomalyEntity
import com.sentinelguard.data.database.entities.LocationClusterEntity
import com.sentinelguard.permission.PermissionManager
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Manages location clusters (safe zones) for behavioral analysis.
 * 
 * Features:
 * - Auto-clusters frequently visited locations
 * - Detects when device is in unknown location
 * - Tracks time spent in each zone
 * - Labels clusters (Home, Work, etc.)
 */
@Singleton
class LocationClusterManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationClusterDao: LocationClusterDao,
    private val anomalyDao: BehavioralAnomalyDao,
    private val permissionManager: PermissionManager,
    private val fusedLocationClient: FusedLocationProviderClient
) {
    
    companion object {
        // Minimum distance (meters) to consider a new cluster
        private const val CLUSTER_RADIUS_METERS = 100f
        
        // Maximum distance to match existing cluster
        private const val MAX_CLUSTER_DISTANCE = 150f
        
        // Minimum visits to consider a cluster "established"
        private const val MIN_VISITS_FOR_TRUSTED = 5
        
        // Risk points for unknown location
        private const val UNKNOWN_LOCATION_RISK = 30
    }
    
    private var currentClusterId: Long? = null
    private var clusterEntryTime: Long? = null
    
    /**
     * Record current location and check for anomalies.
     * Returns risk points if in unknown location.
     */
    suspend fun recordLocation(): Int = withContext(Dispatchers.IO) {
        if (!permissionManager.hasLocationPermission()) {
            return@withContext 0
        }
        
        val location = try {
            getLastLocation()
        } catch (e: Exception) {
            null
        } ?: return@withContext 0
        
        val lat = location.latitude
        val lon = location.longitude
        val timestamp = System.currentTimeMillis()
        
        // Find matching cluster
        val clusters = locationClusterDao.getAllClusters()
        val matchingCluster = clusters.find { cluster ->
            distanceMeters(lat, lon, cluster.centerLatitude, cluster.centerLongitude) <= MAX_CLUSTER_DISTANCE
        }
        
        var riskPoints = 0
        
        if (matchingCluster != null) {
            // Update existing cluster
            locationClusterDao.incrementVisit(matchingCluster.id, timestamp)
            
            // Track time in cluster
            if (currentClusterId != matchingCluster.id) {
                // Left previous cluster, entered new one
                if (currentClusterId != null && clusterEntryTime != null) {
                    val duration = timestamp - clusterEntryTime!!
                    locationClusterDao.addTimeSpent(currentClusterId!!, duration)
                }
                currentClusterId = matchingCluster.id
                clusterEntryTime = timestamp
            }
        } else {
            // New location - not in any cluster
            val hasEstablishedClusters = clusters.any { it.visitCount >= MIN_VISITS_FOR_TRUSTED }
            
            if (hasEstablishedClusters) {
                // We have baselines but this location is unknown
                riskPoints = UNKNOWN_LOCATION_RISK
                logAnomaly(
                    type = "LOCATION",
                    description = "Device in unknown location (not in any established zone)",
                    severity = 8,
                    riskPoints = riskPoints
                )
            }
            
            // Create new cluster for this location
            val newCluster = LocationClusterEntity(
                centerLatitude = lat,
                centerLongitude = lon,
                radiusMeters = CLUSTER_RADIUS_METERS,
                visitCount = 1,
                lastVisited = timestamp,
                isTrusted = false // Not trusted until established
            )
            val newId = locationClusterDao.insert(newCluster)
            currentClusterId = newId
            clusterEntryTime = timestamp
        }
        
        riskPoints
    }
    
    /**
     * Get current location cluster info.
     */
    suspend fun getCurrentCluster(): LocationClusterEntity? {
        return currentClusterId?.let { locationClusterDao.getClusterById(it) }
    }
    
    /**
     * Check if device is in a trusted zone.
     */
    suspend fun isInTrustedZone(): Boolean {
        val cluster = getCurrentCluster() ?: return false
        return cluster.isTrusted && cluster.visitCount >= MIN_VISITS_FOR_TRUSTED
    }
    
    /**
     * Get all established clusters (safe zones).
     */
    suspend fun getSafeZones(): List<LocationClusterEntity> {
        return locationClusterDao.getTrustedClusters().filter { 
            it.visitCount >= MIN_VISITS_FOR_TRUSTED 
        }
    }
    
    /**
     * Label a cluster (e.g., "Home", "Work").
     */
    suspend fun labelCluster(clusterId: Long, label: String) {
        locationClusterDao.setLabel(clusterId, label)
    }
    
    /**
     * Calculate learning progress for location clusters.
     */
    suspend fun getLearningProgress(): Float {
        val clusters = locationClusterDao.getAllClusters()
        val establishedClusters = clusters.count { it.visitCount >= MIN_VISITS_FOR_TRUSTED }
        
        // Consider "learned" when we have at least 2 established clusters (e.g., home + work)
        return (establishedClusters / 2f).coerceIn(0f, 1f)
    }
    
    /**
     * Calculate distance between two coordinates in meters.
     */
    private fun distanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
    
    /**
     * Log a behavioral anomaly.
     */
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
    
    /**
     * Get last known location using suspendCancellableCoroutine.
     */
    @Suppress("MissingPermission")
    private suspend fun getLastLocation(): Location? = suspendCancellableCoroutine { cont ->
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                cont.resume(location)
            }
            .addOnFailureListener { exception ->
                cont.resumeWithException(exception)
            }
    }
}
