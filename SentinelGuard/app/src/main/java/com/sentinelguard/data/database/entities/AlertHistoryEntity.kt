package com.sentinelguard.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Email alert delivery status.
 */
enum class AlertStatus {
    /** Waiting to be sent (offline or pending) */
    QUEUED,
    
    /** Currently attempting to send */
    SENDING,
    
    /** Successfully delivered */
    SENT,
    
    /** Delivery failed (will retry) */
    FAILED,
    
    /** Permanently failed after max retries */
    FAILED_PERMANENT
}

/**
 * Email alert history entity.
 * 
 * Tracks all email alerts sent or queued, with retry information.
 */
@Entity(tableName = "alert_history")
data class AlertHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /** Recipient email address */
    val recipientEmail: String,
    
    /** Email subject */
    val subject: String,
    
    /** Email body content */
    val body: String,
    
    /** Current delivery status */
    val status: AlertStatus = AlertStatus.QUEUED,
    
    /** Related incident ID */
    val incidentId: Long? = null,
    
    /** When alert was created/queued */
    val createdAt: Long = System.currentTimeMillis(),
    
    /** When alert was successfully sent */
    val sentAt: Long? = null,
    
    /** Number of delivery attempts */
    val retryCount: Int = 0,
    
    /** Last error message if failed */
    val lastError: String? = null,
    
    /** Next retry attempt time */
    val nextRetryAt: Long? = null
)
