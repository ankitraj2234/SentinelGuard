package com.sentinelguard.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * ROOM ENTITY: User Account
 * 
 * WHY THIS TABLE EXISTS:
 * Stores the single user account for this security app. Contains the bcrypt
 * password hash (never plain text), biometric enrollment status, and lockout
 * tracking for brute-force protection.
 * 
 * SECURITY NOTES:
 * - Password is stored as bcrypt hash with cost factor 12
 * - Email indexed for login lookup
 * - Lockout fields implement progressive delays
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey
    val id: String,
    
    val email: String,
    
    /** bcrypt hash of user password, NEVER plain text */
    val passwordHash: String,
    
    /** Whether biometric login is enabled */
    val biometricEnabled: Boolean = false,
    
    /** Consecutive failed login attempts */
    val failedLoginAttempts: Int = 0,
    
    /** Timestamp until which login is blocked (null = not locked) */
    val lockedUntil: Long? = null,
    
    /** Account creation timestamp */
    val createdAt: Long,
    
    /** Last successful login timestamp */
    val lastLoginAt: Long? = null
)

/**
 * ROOM ENTITY: Security Signal
 * 
 * WHY THIS TABLE EXISTS:
 * Records every security-relevant event detected by the app. This raw data
 * feeds into the behavioral baseline engine and risk scoring. Signals are
 * immutable once written.
 * 
 * EXAMPLES:
 * - App opened at 2:30 AM (unusual hour)
 * - SIM card removed
 * - Network changed from WiFi to Mobile
 * - Failed login attempt
 * 
 * RETENTION: Signals older than retention period are auto-deleted.
 */
@Entity(
    tableName = "security_signals",
    indices = [
        Index(value = ["signalType"]),
        Index(value = ["timestamp"]),
        Index(value = ["processed"])
    ]
)
data class SecuritySignalEntity(
    @PrimaryKey
    val id: String,
    
    /** Type of security signal (enum stored as string) */
    val signalType: String,
    
    /** Signal-specific value (optional) */
    val value: String? = null,
    
    /** Additional JSON metadata */
    val metadata: String? = null,
    
    /** When the signal was captured */
    val timestamp: Long,
    
    /** Whether this signal has been processed by baseline/risk engines */
    val processed: Boolean = false
)

/**
 * ROOM ENTITY: Behavioral Baseline
 * 
 * WHY THIS TABLE EXISTS:
 * Stores learned patterns of legitimate user behavior. After a 7-14 day
 * learning period, deviations from these baselines trigger risk signals.
 * 
 * EXAMPLES:
 * - User typically opens app between 8 AM and 10 PM
 * - User has 3 location clusters (home, work, gym)
 * - User averages 5 sessions per day
 * 
 * STATISTICS: Uses mean, variance, standard deviation - NO ML.
 */
@Entity(
    tableName = "behavioral_baselines",
    indices = [Index(value = ["metricType"], unique = true)]
)
data class BehavioralBaselineEntity(
    @PrimaryKey
    val id: String,
    
    /** Type of baseline metric (enum stored as string) */
    val metricType: String,
    
    /** JSON-encoded baseline value (histogram, cluster list, etc.) */
    val value: String,
    
    /** Statistical variance where applicable */
    val variance: Double? = null,
    
    /** Confidence score 0.0 to 1.0 based on sample count */
    val confidence: Double = 0.0,
    
    /** Number of samples used to build this baseline */
    val sampleCount: Int = 0,
    
    /** Whether the learning period is complete for this metric */
    val learningComplete: Boolean = false,
    
    /** Last update timestamp */
    val updatedAt: Long
)

/**
 * ROOM ENTITY: Risk Score
 * 
 * WHY THIS TABLE EXISTS:
 * Records point-in-time risk assessments. Each entry represents the total
 * risk score calculated when the app opens or when a significant signal
 * is detected. Used for trend analysis and forensic timeline.
 * 
 * THRESHOLDS:
 * - 0-39: Normal
 * - 40-69: Warning (UI indication)
 * - 70-89: High (app lock)
 * - 90+: Critical (lock + email alert)
 */
@Entity(
    tableName = "risk_scores",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["riskLevel"])
    ]
)
data class RiskScoreEntity(
    @PrimaryKey
    val id: String,
    
    /** Total calculated risk score (0-100+) */
    val totalScore: Int,
    
    /** Current score after decay applied */
    val currentScore: Int,
    
    /** Risk level category (enum stored as string) */
    val riskLevel: String,
    
    /** JSON map of SignalType -> contribution points */
    val signalContributions: String,
    
    /** Human-readable reason for this score */
    val triggerReason: String,
    
    /** Whether decay has been applied */
    val decayed: Boolean = false,
    
    /** When this score was calculated */
    val timestamp: Long
)

/**
 * ROOM ENTITY: Security Incident
 * 
 * WHY THIS TABLE EXISTS:
 * Permanent forensic record of security events. Unlike signals which may be
 * pruned, incidents are kept indefinitely for legal/forensic purposes.
 * Each incident represents a moment when the app took protective action.
 * 
 * USE CASE: User suspects device theft, opens timeline to see exactly
 * what happened and what actions the app took.
 */
@Entity(
    tableName = "incidents",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["severity"]),
        Index(value = ["resolved"])
    ]
)
data class IncidentEntity(
    @PrimaryKey
    val id: String,
    
    /** Incident severity (enum stored as string) */
    val severity: String,
    
    /** Risk score at time of incident */
    val riskScore: Int,
    
    /** JSON array of SignalType that triggered this incident */
    val triggeredBy: String,
    
    /** JSON array of ResponseAction taken */
    val actionsTaken: String,
    
    /** Human-readable summary */
    val summary: String,
    
    /** JSON with lat/lng if location was captured */
    val location: String? = null,
    
    /** Whether user has acknowledged/resolved this incident */
    val resolved: Boolean = false,
    
    /** When incident occurred */
    val timestamp: Long
)

/**
 * ROOM ENTITY: Alert Queue
 * 
 * WHY THIS TABLE EXISTS:
 * Email alerts may fail if device is offline. This queue stores pending
 * alerts with retry logic. Alerts are retried with exponential backoff
 * until successful or max retries exceeded.
 * 
 * OFFLINE RESILIENCE: Alerts queue up and send when connectivity returns.
 */
@Entity(
    tableName = "alert_queue",
    indices = [
        Index(value = ["status"]),
        Index(value = ["incidentId"])
    ]
)
data class AlertQueueEntity(
    @PrimaryKey
    val id: String,
    
    /** Recipient email address */
    val recipientEmail: String,
    
    /** Email subject line */
    val subject: String,
    
    /** Email body content */
    val body: String,
    
    /** Current status (enum stored as string) */
    val status: String,
    
    /** Linked incident ID (nullable) */
    val incidentId: String? = null,
    
    /** Number of send attempts */
    val retryCount: Int = 0,
    
    /** Last error message if failed */
    val lastError: String? = null,
    
    /** Scheduled next retry timestamp */
    val nextRetryAt: Long? = null,
    
    /** When alert was created */
    val createdAt: Long,
    
    /** When alert was successfully sent */
    val sentAt: Long? = null
)
