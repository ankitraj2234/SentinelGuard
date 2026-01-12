package com.sentinelguard.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sentinelguard.data.database.entities.IncidentEntity
import com.sentinelguard.data.database.entities.IncidentSeverity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Incident operations (forensic timeline).
 */
@Dao
interface IncidentDao {

    @Insert
    suspend fun insert(incident: IncidentEntity): Long

    @Update
    suspend fun update(incident: IncidentEntity)

    @Query("SELECT * FROM incidents WHERE id = :id")
    suspend fun getById(id: Long): IncidentEntity?

    @Query("SELECT * FROM incidents ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 100): List<IncidentEntity>

    @Query("SELECT * FROM incidents ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM incidents ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int = 50): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM incidents WHERE severity = :severity ORDER BY timestamp DESC")
    suspend fun getBySeverity(severity: IncidentSeverity): List<IncidentEntity>

    @Query("SELECT * FROM incidents WHERE severity IN (:severities) ORDER BY timestamp DESC")
    fun observeBySeverities(severities: List<IncidentSeverity>): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM incidents WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp ASC")
    suspend fun getInRange(start: Long, end: Long): List<IncidentEntity>

    @Query("SELECT * FROM incidents WHERE resolved = 0 ORDER BY timestamp DESC")
    suspend fun getUnresolved(): List<IncidentEntity>

    @Query("UPDATE incidents SET resolved = 1, resolvedAt = :timestamp WHERE id = :id")
    suspend fun markResolved(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM incidents WHERE severity = :severity AND timestamp >= :since")
    suspend fun countBySeveritySince(severity: IncidentSeverity, since: Long): Int

    @Query("SELECT * FROM incidents WHERE timestamp >= :since ORDER BY timestamp ASC")
    suspend fun getSince(since: Long): List<IncidentEntity>

    @Query("DELETE FROM incidents WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)

    @Query("DELETE FROM incidents")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM incidents")
    suspend fun count(): Int
}
