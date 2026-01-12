package com.sentinelguard.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Cached cell tower location data from API lookups
 */
@Entity(
    tableName = "cell_tower_cache",
    indices = [Index(value = ["cellId", "lac", "mcc", "mnc"], unique = true)]
)
data class CellTowerCacheEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cellId: String,
    val lac: String,
    val mcc: String,
    val mnc: String,
    val radioType: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Int,
    val range: Int,
    val samples: Int,
    val areaName: String?,
    val towerType: String,       // MACRO, MICRO, PICO, FEMTO
    val securityStatus: String,  // VERIFIED, UNVERIFIED, UNKNOWN, SUSPICIOUS
    val cachedAt: Long,          // When this was cached
    val expiresAt: Long          // When cache expires (24 hours typically)
)

/**
 * Cell tower connection history for tracking movement
 */
@Entity(
    tableName = "cell_tower_history",
    indices = [Index(value = ["connectedAt"])]
)
data class CellTowerHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cellId: String,
    val lac: String,
    val mcc: String?,
    val mnc: String?,
    val latitude: Double?,
    val longitude: Double?,
    val areaName: String?,
    val carrierName: String?,
    val networkType: String?,        // 4G, 5G, etc.
    val signalStrength: Int?,        // dBm
    val connectedAt: Long,           // When connected
    val disconnectedAt: Long?,       // When disconnected (null if still connected)
    val securityStatus: String,      // VERIFIED, UNKNOWN, SUSPICIOUS
    val wasAlertSent: Boolean = false
)

/**
 * Security incidents related to cell towers
 */
@Entity(tableName = "cell_tower_incidents")
data class CellTowerIncidentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cellId: String,
    val lac: String?,
    val incidentType: String,        // FAKE_TOWER, UNUSUAL_CHANGE, DOWNGRADE_ATTACK
    val riskLevel: String,           // LOW, MEDIUM, HIGH, CRITICAL
    val description: String,
    val indicators: String,          // JSON array of indicators
    val latitude: Double?,
    val longitude: Double?,
    val areaName: String?,
    val occurredAt: Long,
    val wasEmailSent: Boolean = false,
    val wasResolved: Boolean = false,
    val resolvedAt: Long? = null
)
