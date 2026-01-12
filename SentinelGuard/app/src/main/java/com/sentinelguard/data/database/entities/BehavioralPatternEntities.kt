package com.sentinelguard.data.database.entities

import androidx.room.*

/**
 * Entity for storing app usage patterns by hour.
 * Tracks which apps are typically used at what times.
 */
@Entity(tableName = "app_usage_patterns")
data class AppUsagePatternEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val hourOfDay: Int,        // 0-23
    val dayOfWeek: Int,        // 1-7 (Sunday-Saturday)
    val usageCount: Int,       // How many times used at this hour
    val avgDurationMs: Long,   // Average session duration
    val lastUsed: Long,        // Timestamp of last usage
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entity for storing location clusters (safe zones).
 * Each cluster represents a learned location like home, work, etc.
 */
@Entity(tableName = "location_clusters")
data class LocationClusterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val centerLatitude: Double,
    val centerLongitude: Double,
    val radiusMeters: Float = 100f,  // Cluster radius
    val label: String? = null,        // User-provided name (Home, Work, etc.)
    val visitCount: Int = 1,          // How many times visited
    val totalTimeSpentMs: Long = 0,   // Total time spent in this zone
    val lastVisited: Long,
    val isTrusted: Boolean = true,    // Is this a trusted zone
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entity for storing known WiFi networks.
 */
@Entity(tableName = "known_networks")
data class KnownNetworkEntity(
    @PrimaryKey
    val ssid: String,
    val bssid: String? = null,        // MAC address for more accuracy
    val isSecure: Boolean = true,     // Is it password protected
    val isTrusted: Boolean = true,    // User-trusted network
    val connectionCount: Int = 1,
    val lastConnected: Long,
    val totalTimeConnectedMs: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entity for storing unlock patterns by hour.
 */
@Entity(tableName = "unlock_patterns")
data class UnlockPatternEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hourOfDay: Int,          // 0-23
    val dayOfWeek: Int,          // 1-7
    val unlockCount: Int = 0,    // Number of unlocks at this hour
    val failedAttempts: Int = 0, // Failed unlock attempts
    val avgSessionLengthMs: Long = 0, // How long device stays unlocked
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Entity for tracking behavioral anomalies.
 */
@Entity(tableName = "behavioral_anomalies")
data class BehavioralAnomalyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val anomalyType: String,     // LOCATION, APP_USAGE, NETWORK, UNLOCK
    val description: String,
    val severity: Int,           // 1-10
    val riskPoints: Int,         // Points added to risk score
    val resolved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
