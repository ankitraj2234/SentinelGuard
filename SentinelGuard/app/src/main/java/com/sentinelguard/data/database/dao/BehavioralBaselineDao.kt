package com.sentinelguard.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.sentinelguard.data.database.entities.BaselineMetricType
import com.sentinelguard.data.database.entities.BehavioralBaselineEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for BehavioralBaseline operations.
 */
@Dao
interface BehavioralBaselineDao {

    @Insert
    suspend fun insert(baseline: BehavioralBaselineEntity): Long

    @Update
    suspend fun update(baseline: BehavioralBaselineEntity)

    @Upsert
    suspend fun upsert(baseline: BehavioralBaselineEntity)

    @Query("SELECT * FROM behavioral_baselines WHERE id = :id")
    suspend fun getById(id: Long): BehavioralBaselineEntity?

    @Query("SELECT * FROM behavioral_baselines WHERE metricType = :type LIMIT 1")
    suspend fun getByType(type: BaselineMetricType): BehavioralBaselineEntity?

    @Query("SELECT * FROM behavioral_baselines")
    suspend fun getAll(): List<BehavioralBaselineEntity>

    @Query("SELECT * FROM behavioral_baselines")
    fun observeAll(): Flow<List<BehavioralBaselineEntity>>

    @Query("SELECT * FROM behavioral_baselines WHERE learningComplete = 1")
    suspend fun getCompletedBaselines(): List<BehavioralBaselineEntity>

    @Query("SELECT * FROM behavioral_baselines WHERE learningComplete = 0")
    suspend fun getIncompleteBaselines(): List<BehavioralBaselineEntity>

    @Query("SELECT AVG(confidence) FROM behavioral_baselines")
    suspend fun getAverageConfidence(): Double?

    @Query("SELECT COUNT(*) FROM behavioral_baselines WHERE learningComplete = 1")
    suspend fun getCompletedCount(): Int

    @Query("UPDATE behavioral_baselines SET learningComplete = 1, updatedAt = :timestamp WHERE metricType = :type")
    suspend fun markLearningComplete(type: BaselineMetricType, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM behavioral_baselines WHERE metricType = :type")
    suspend fun deleteByType(type: BaselineMetricType)

    @Query("DELETE FROM behavioral_baselines")
    suspend fun deleteAll()
}
