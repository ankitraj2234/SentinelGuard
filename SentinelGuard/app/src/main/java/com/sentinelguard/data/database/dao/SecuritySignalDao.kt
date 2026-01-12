package com.sentinelguard.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sentinelguard.data.database.entities.SecuritySignalEntity
import com.sentinelguard.data.database.entities.SignalType
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for SecuritySignal operations.
 */
@Dao
interface SecuritySignalDao {

    @Insert
    suspend fun insert(signal: SecuritySignalEntity): Long

    @Insert
    suspend fun insertAll(signals: List<SecuritySignalEntity>)

    @Update
    suspend fun update(signal: SecuritySignalEntity)

    @Query("SELECT * FROM security_signals WHERE id = :id")
    suspend fun getById(id: Long): SecuritySignalEntity?

    @Query("SELECT * FROM security_signals ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 100): List<SecuritySignalEntity>

    @Query("SELECT * FROM security_signals ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int = 50): Flow<List<SecuritySignalEntity>>

    @Query("SELECT * FROM security_signals WHERE signalType = :type ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getByType(type: SignalType, limit: Int = 100): List<SecuritySignalEntity>

    @Query("SELECT * FROM security_signals WHERE timestamp >= :since ORDER BY timestamp ASC")
    suspend fun getSince(since: Long): List<SecuritySignalEntity>

    @Query("SELECT * FROM security_signals WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp ASC")
    suspend fun getInRange(start: Long, end: Long): List<SecuritySignalEntity>

    @Query("SELECT * FROM security_signals WHERE signalType = :type AND timestamp >= :since ORDER BY timestamp ASC")
    suspend fun getByTypeSince(type: SignalType, since: Long): List<SecuritySignalEntity>

    @Query("SELECT * FROM security_signals WHERE processed = 0 ORDER BY timestamp ASC")
    suspend fun getUnprocessed(): List<SecuritySignalEntity>

    @Query("UPDATE security_signals SET processed = 1 WHERE id IN (:ids)")
    suspend fun markProcessed(ids: List<Long>)

    @Query("SELECT COUNT(*) FROM security_signals WHERE signalType = :type AND timestamp >= :since")
    suspend fun countByTypeSince(type: SignalType, since: Long): Int

    @Query("SELECT * FROM security_signals WHERE signalType IN (:types) AND timestamp >= :since ORDER BY timestamp DESC")
    suspend fun getByTypesSince(types: List<SignalType>, since: Long): List<SecuritySignalEntity>

    @Query("DELETE FROM security_signals WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)

    @Query("DELETE FROM security_signals")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM security_signals")
    suspend fun count(): Int
}
