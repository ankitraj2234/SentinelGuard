package com.sentinelguard.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sentinelguard.data.database.entities.AlertQueueEntity

/**
 * Data Access Object for alert queue operations.
 */
@Dao
interface AlertQueueDao {

    @Insert
    suspend fun insert(alert: AlertQueueEntity): Long

    @Query("SELECT * FROM alert_queue WHERE status = 'PENDING' ORDER BY createdAt ASC")
    suspend fun getPending(): List<AlertQueueEntity>

    @Query("SELECT * FROM alert_queue WHERE status = :status ORDER BY createdAt ASC")
    suspend fun getByStatus(status: String): List<AlertQueueEntity>

    @Query("SELECT * FROM alert_queue WHERE incidentId = :incidentId ORDER BY createdAt DESC")
    suspend fun getByIncidentId(incidentId: Long): List<AlertQueueEntity>

    @Query("UPDATE alert_queue SET status = :status, lastError = :error WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, error: String?)

    @Query("UPDATE alert_queue SET retryCount = retryCount + 1, nextRetryAt = :nextRetryAt WHERE id = :id")
    suspend fun incrementRetry(id: Long, nextRetryAt: Long)

    @Query("UPDATE alert_queue SET status = 'SENT', sentAt = :now WHERE id = :id")
    suspend fun markSent(id: Long, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM alert_queue WHERE createdAt < :before")
    suspend fun deleteOlderThan(before: Long): Int

    @Query("DELETE FROM alert_queue")
    suspend fun deleteAll()
}
