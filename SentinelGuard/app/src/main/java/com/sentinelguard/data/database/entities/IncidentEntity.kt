package com.sentinelguard.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Severity level for incidents.
 */
enum class IncidentSeverity {
    INFO,
    WARNING,
    HIGH,
    CRITICAL
}

/**
 * Types of response actions taken.
 */
enum class ResponseAction {
    APP_LOCKED,
    BIOMETRIC_REQUIRED,
    SESSION_WIPED,
    COOLDOWN_STARTED,
    RESTRICTED_UI,
    ALERT_QUEUED,
    ALERT_SENT
}

/**
 * Security incident entity.
 * 
 * Records security-relevant events for the forensic timeline.
 * Each incident may have multiple associated actions.
 */
@Entity(tableName = "incidents")
data class IncidentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /** Severity of the incident */
    val severity: IncidentSeverity,
    
    /** Risk score at time of incident */
    val riskScore: Int,
    
    /** What triggered this incident (JSON list of signals) */
    val triggeredBy: String,
    
    /** Actions taken in response (JSON list of ResponseAction) */
    val actionsTaken: String,
    
    /** Human-readable summary */
    val summary: String,
    
    /** Location at time of incident (lat,lng) or null */
    val location: String? = null,
    
    /** Device state JSON (network, sim, etc.) */
    val deviceState: String? = null,
    
    /** When incident occurred */
    val timestamp: Long = System.currentTimeMillis(),
    
    /** Whether incident was resolved (user verified identity) */
    val resolved: Boolean = false,
    
    /** When incident was resolved */
    val resolvedAt: Long? = null
)
