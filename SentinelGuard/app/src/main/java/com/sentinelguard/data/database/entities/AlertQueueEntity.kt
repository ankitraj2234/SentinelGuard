package com.sentinelguard.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for queued email alerts.
 */
@Entity(tableName = "alert_queue")
data class AlertQueueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipientEmail: String,
    val subject: String,
    val body: String,
    val status: String,
    val incidentId: Long? = null,
    val retryCount: Int = 0,
    val lastError: String? = null,
    val nextRetryAt: Long? = null,
    val createdAt: Long,
    val sentAt: Long? = null
)
