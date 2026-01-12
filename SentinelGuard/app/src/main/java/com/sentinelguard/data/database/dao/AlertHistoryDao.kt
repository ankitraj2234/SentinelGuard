package com.sentinelguard.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sentinelguard.data.database.entities.AlertHistoryEntity
import com.sentinelguard.data.database.entities.AlertStatus
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for AlertHistory operations.
 */
@Dao
interface AlertHistoryDao {

    @Insert
    suspend fun insert(alert: AlertHistoryEntity): Long

    @Update
    suspend fun update(alert: AlertHistoryEntity)

    @Query("SELECT * FROM alert_history WHERE id = :id")
    suspend fun getById(id: Long): AlertHistoryEntity?

    @Query("SELECT * FROM alert_history ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 50): List<AlertHistoryEntity>

    @Query("SELECT * FROM alert_history ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<AlertHistoryEntity>>

    @Query("SELECT * FROM alert_history ORDER BY createdAt DESC LIMIT :limit")
    fun observeRecent(limit: Int = 20): Flow<List<AlertHistoryEntity>>

    @Query("SELECT * FROM alert_history WHERE status = :status ORDER BY createdAt ASC")
    suspend fun getByStatus(status: AlertStatus): List<AlertHistoryEntity>

    @Query("SELECT * FROM alert_history WHERE status = :status ORDER BY createdAt ASC")
    fun observeByStatus(status: AlertStatus): Flow<List<AlertHistoryEntity>>

    @Query("SELECT * FROM alert_history WHERE status IN (:statuses) ORDER BY createdAt ASC")
    suspend fun getByStatuses(statuses: List<AlertStatus>): List<AlertHistoryEntity>

    @Query("SELECT * FROM alert_history WHERE status = 'QUEUED' AND (nextRetryAt IS NULL OR nextRetryAt <= :now) ORDER BY createdAt ASC")
    suspend fun getPendingSend(now: Long = System.currentTimeMillis()): List<AlertHistoryEntity>

    @Query("UPDATE alert_history SET status = :status, sentAt = :sentAt, retryCount = retryCount + 1 WHERE id = :id")
    suspend fun updateStatusSent(id: Long, status: AlertStatus, sentAt: Long)

    @Query("UPDATE alert_history SET status = :status, lastError = :error, retryCount = retryCount + 1, nextRetryAt = :nextRetry WHERE id = :id")
    suspend fun updateStatusFailed(id: Long, status: AlertStatus, error: String?, nextRetry: Long?)

    @Query("SELECT COUNT(*) FROM alert_history WHERE status = :status")
    suspend fun countByStatus(status: AlertStatus): Int

    @Query("SELECT * FROM alert_history WHERE incidentId = :incidentId ORDER BY createdAt DESC")
    suspend fun getByIncidentId(incidentId: Long): List<AlertHistoryEntity>

    @Query("DELETE FROM alert_history WHERE createdAt < :before")
    suspend fun deleteOlderThan(before: Long)

    @Query("DELETE FROM alert_history")
    suspend fun deleteAll()
}
