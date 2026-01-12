package com.sentinelguard.domain.model

import java.util.UUID

/**
 * DOMAIN MODEL: User Account
 * 
 * Represents the single user account for this security app.
 * Clean POKO with no Room/framework annotations.
 */
data class User(
    val id: String = UUID.randomUUID().toString(),
    val email: String,
    val biometricEnabled: Boolean = false,
    val failedLoginAttempts: Int = 0,
    val lockedUntil: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long? = null
)

/**
 * DOMAIN MODEL: Security Signal
 * 
 * A single security-relevant event detected by the app.
 * Used for behavioral analysis and risk scoring.
 */
data class SecuritySignal(
    val id: String = UUID.randomUUID().toString(),
    val type: SignalType,
    val value: String? = null,
    val metadata: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val processed: Boolean = false
)

/**
 * Types of security signals the app can detect.
 * Each type has a specific purpose in threat detection.
 */
enum class SignalType {
    // App lifecycle
    APP_OPEN,           // User opened the app
    APP_CLOSE,          // App went to background
    APP_SESSION,        // Complete session (open→close)
    
    // Authentication - Detailed
    LOGIN_SUCCESS,      // Successful login
    LOGIN_FAILURE,      // Failed login attempt (generic)
    BIOMETRIC_SUCCESS,  // Biometric auth successful
    BIOMETRIC_FAILED,   // Biometric auth failed (fingerprint/face)
    PIN_FAILED,         // Wrong PIN entered
    
    // Device state
    DEVICE_BOOT,        // Device was rebooted
    SCREEN_ON,          // Screen turned on
    SCREEN_OFF,         // Screen turned off
    
    // Network
    NETWORK_WIFI,       // Connected to WiFi
    NETWORK_MOBILE,     // Connected to mobile data
    NETWORK_NONE,       // No network
    NETWORK_CHANGE,     // Network type changed (WiFi↔Mobile)
    NETWORK_SIM_SWITCH, // Switched between SIM1/SIM2
    
    // Cell Tower
    CELL_TOWER_CHANGE,  // Connected to different cell tower
    
    // SIM
    SIM_PRESENT,        // SIM is present
    SIM_REMOVED,        // SIM was removed
    SIM_CHANGED,        // Different SIM inserted
    
    // Location (on-demand only, never background)
    LOCATION_UPDATE,    // Location captured at app open
    LOCATION_ANOMALY,   // Location outside known clusters
    
    // Environment
    TIMEZONE_CHANGE,    // Timezone changed
    LOCALE_CHANGE,      // Device locale changed
    
    // Security threats
    ROOT_DETECTED,      // Device appears rooted
    EMULATOR_DETECTED,  // Running on emulator
    DEBUGGER_DETECTED,  // Debugger attached
    SCREEN_RECORDING_DETECTED,  // Screen recording active
    
    // Security scans
    SECURITY_SCAN_COMPLETE, // Scan finished - no threats
    SECURITY_SCAN_THREAT    // Scan finished - threats found
}

/**
 * DOMAIN MODEL: Behavioral Baseline
 * 
 * Stores learned patterns of legitimate user behavior.
 * Used to detect anomalies.
 */
data class BehavioralBaseline(
    val id: String = UUID.randomUUID().toString(),
    val metricType: BaselineMetricType,
    val value: String,          // JSON-encoded baseline data
    val variance: Double? = null,
    val confidence: Double = 0.0,
    val sampleCount: Int = 0,
    val learningComplete: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

enum class BaselineMetricType {
    USAGE_HOUR_HISTOGRAM,   // Which hours user typically opens app
    SESSIONS_PER_DAY,       // Average sessions per day
    SESSION_DURATION,       // Average session length
    LOCATION_CLUSTERS,      // Known location clusters
    NETWORK_PATTERN,        // Typical network types
    LEARNING_DAYS           // Days of learning completed
}

/**
 * DOMAIN MODEL: Risk Score
 * 
 * A point-in-time risk assessment.
 */
data class RiskScore(
    val id: String = UUID.randomUUID().toString(),
    val totalScore: Int,
    val level: RiskLevel,
    val contributions: Map<SignalType, Int>,  // Which signals contributed
    val triggerReason: String,
    val decayed: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

enum class RiskLevel {
    NORMAL,     // Score 0-39
    WARNING,    // Score 40-69
    HIGH,       // Score 70-89
    CRITICAL    // Score 90+
}

/**
 * DOMAIN MODEL: Security Incident
 * 
 * A logged security event for the forensic timeline.
 */
data class Incident(
    val id: String = UUID.randomUUID().toString(),
    val severity: IncidentSeverity,
    val riskScore: Int,
    val triggers: List<SignalType>,
    val actionsTaken: List<ResponseAction>,
    val summary: String,
    val location: LocationData? = null,
    val resolved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

enum class IncidentSeverity {
    INFO,
    WARNING,
    HIGH,
    CRITICAL
}

enum class ResponseAction {
    NONE,
    UI_WARNING,
    APP_LOCKED,
    BIOMETRIC_REQUIRED,
    SESSION_WIPED,
    COOLDOWN_STARTED,
    RESTRICTED_UI,
    ALERT_QUEUED
}

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float
)

/**
 * DOMAIN MODEL: Alert Queue Item
 * 
 * An email alert pending delivery.
 */
data class AlertQueueItem(
    val id: String = UUID.randomUUID().toString(),
    val recipientEmail: String,
    val subject: String,
    val body: String,
    val status: AlertStatus = AlertStatus.QUEUED,
    val incidentId: String? = null,
    val retryCount: Int = 0,
    val lastError: String? = null,
    val nextRetryAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val sentAt: Long? = null
)

enum class AlertStatus {
    QUEUED,
    SENDING,
    SENT,
    FAILED,
    FAILED_PERMANENT
}
