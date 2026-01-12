package com.sentinelguard.security.baseline.metric

import com.sentinelguard.domain.model.BaselineMetricType
import com.sentinelguard.domain.model.BehavioralBaseline
import com.sentinelguard.domain.model.SecuritySignal
import com.sentinelguard.domain.model.SignalType
import com.sentinelguard.domain.util.SecureIdGenerator
import com.sentinelguard.security.baseline.util.Statistics
import org.json.JSONArray
import org.json.JSONObject

/**
 * LocationMetric: Clusters Known User Locations
 * 
 * WHY THIS EXISTS:
 * Users have predictable location patterns (home, work, gym, etc.).
 * Opening the app from an unknown location is suspicious.
 * 
 * ALGORITHM:
 * - Cluster locations within CLUSTER_RADIUS meters
 * - New location that's outside all clusters = anomaly
 * 
 * PRIVACY:
 * - Locations stored locally only
 * - Only captures at app open (no background tracking)
 */
class LocationMetric {

    companion object {
        const val CLUSTER_RADIUS_METERS = 500.0
        const val MIN_CLUSTER_HITS = 3  // Need 3 visits to form a cluster
        const val MAX_CLUSTERS = 20
    }

    data class LocationCluster(
        val latitude: Double,
        val longitude: Double,
        var hitCount: Int = 1
    )

    private val clusters = mutableListOf<LocationCluster>()
    private var totalSamples = 0

    /**
     * Updates clusters from location signals.
     */
    fun updateFromSignals(signals: List<SecuritySignal>): BehavioralBaseline {
        val locationSignals = signals.filter { it.type == SignalType.LOCATION_UPDATE }

        for (signal in locationSignals) {
            val location = extractLocation(signal) ?: continue
            addLocation(location.first, location.second)
        }

        return buildBaseline()
    }

    /**
     * Adds a location, either to existing cluster or creates new.
     */
    fun addLocation(latitude: Double, longitude: Double) {
        totalSamples++

        // Find nearest cluster
        var nearestCluster: LocationCluster? = null
        var nearestDistance = Double.MAX_VALUE

        for (cluster in clusters) {
            val distance = Statistics.haversineDistance(
                latitude, longitude,
                cluster.latitude, cluster.longitude
            )
            if (distance < nearestDistance) {
                nearestDistance = distance
                nearestCluster = cluster
            }
        }

        if (nearestCluster != null && nearestDistance <= CLUSTER_RADIUS_METERS) {
            // Within existing cluster - increment hit count
            nearestCluster.hitCount++
        } else {
            // New cluster
            if (clusters.size < MAX_CLUSTERS) {
                clusters.add(LocationCluster(latitude, longitude))
            }
        }
    }

    /**
     * Checks if a location is within any known cluster.
     */
    fun isLocationKnown(latitude: Double, longitude: Double): Boolean {
        // If no clusters yet, we're still learning
        if (clusters.isEmpty()) return true
        
        // Only consider clusters with enough hits
        val validClusters = clusters.filter { it.hitCount >= MIN_CLUSTER_HITS }
        if (validClusters.isEmpty()) return true

        for (cluster in validClusters) {
            val distance = Statistics.haversineDistance(
                latitude, longitude,
                cluster.latitude, cluster.longitude
            )
            if (distance <= CLUSTER_RADIUS_METERS) {
                return true
            }
        }
        return false
    }

    /**
     * Checks if a location is anomalous (unknown).
     */
    fun isLocationAnomaly(latitude: Double, longitude: Double): Boolean {
        // Need at least some clusters before detecting anomalies
        val validClusters = clusters.filter { it.hitCount >= MIN_CLUSTER_HITS }
        if (validClusters.size < 2) return false

        return !isLocationKnown(latitude, longitude)
    }

    /**
     * Gets all clusters for display/debugging.
     */
    fun getClusters(): List<LocationCluster> {
        return clusters.toList()
    }

    /**
     * Gets the number of established clusters.
     */
    fun getEstablishedClusterCount(): Int {
        return clusters.count { it.hitCount >= MIN_CLUSTER_HITS }
    }

    private fun extractLocation(signal: SecuritySignal): Pair<Double, Double>? {
        signal.metadata?.let {
            try {
                val json = JSONObject(it)
                if (json.has("lat") && json.has("lng")) {
                    return Pair(json.getDouble("lat"), json.getDouble("lng"))
                }
            } catch (e: Exception) {
                // Ignore parse errors
            }
        }
        return null
    }

    private fun buildBaseline(): BehavioralBaseline {
        val clustersJson = JSONArray()
        for (cluster in clusters) {
            clustersJson.put(JSONObject().apply {
                put("lat", cluster.latitude)
                put("lng", cluster.longitude)
                put("hits", cluster.hitCount)
            })
        }

        return BehavioralBaseline(
            id = SecureIdGenerator.generateId(),
            metricType = BaselineMetricType.LOCATION_CLUSTERS,
            value = clustersJson.toString(),
            variance = null,
            confidence = Statistics.confidence(totalSamples, 10),
            sampleCount = totalSamples,
            learningComplete = getEstablishedClusterCount() >= 2,
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Loads from stored baseline.
     */
    fun loadFromBaseline(baseline: BehavioralBaseline) {
        clusters.clear()
        try {
            val json = JSONArray(baseline.value)
            for (i in 0 until json.length()) {
                val obj = json.getJSONObject(i)
                clusters.add(LocationCluster(
                    latitude = obj.getDouble("lat"),
                    longitude = obj.getDouble("lng"),
                    hitCount = obj.optInt("hits", 1)
                ))
            }
            totalSamples = baseline.sampleCount
        } catch (e: Exception) {
            clusters.clear()
            totalSamples = 0
        }
    }
}
