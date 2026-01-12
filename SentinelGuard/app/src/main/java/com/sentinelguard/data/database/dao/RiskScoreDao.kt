package com.sentinelguard.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sentinelguard.data.database.entities.RiskLevel
import com.sentinelguard.data.database.entities.RiskScoreEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for RiskScore operations.
 */
@Dao
interface RiskScoreDao {

    @Insert
    suspend fun insert(riskScore: RiskScoreEntity): Long

    @Update
    suspend fun update(riskScore: RiskScoreEntity)

    @Query("SELECT * FROM risk_scores WHERE id = :id")
    suspend fun getById(id: Long): RiskScoreEntity?

    @Query("SELECT * FROM risk_scores ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(): RiskScoreEntity?

    @Query("SELECT * FROM risk_scores ORDER BY timestamp DESC LIMIT 1")
    fun observeLatest(): Flow<RiskScoreEntity?>

    @Query("SELECT * FROM risk_scores ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 50): List<RiskScoreEntity>

    @Query("SELECT * FROM risk_scores ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int = 20): Flow<List<RiskScoreEntity>>

    @Query("SELECT * FROM risk_scores WHERE riskLevel = :level ORDER BY timestamp DESC")
    suspend fun getByLevel(level: RiskLevel): List<RiskScoreEntity>

    @Query("SELECT * FROM risk_scores WHERE totalScore >= :minScore ORDER BY timestamp DESC")
    suspend fun getAboveScore(minScore: Int): List<RiskScoreEntity>

    @Query("SELECT * FROM risk_scores WHERE triggeredAction = 1 ORDER BY timestamp DESC")
    suspend fun getTriggeredActions(): List<RiskScoreEntity>

    @Query("SELECT AVG(totalScore) FROM risk_scores WHERE timestamp >= :since")
    suspend fun getAverageScoreSince(since: Long): Double?

    @Query("SELECT MAX(totalScore) FROM risk_scores WHERE timestamp >= :since")
    suspend fun getMaxScoreSince(since: Long): Int?

    @Query("UPDATE risk_scores SET currentScore = :decayedScore, decayed = 1 WHERE id = :id")
    suspend fun updateDecayedScore(id: Long, decayedScore: Int)

    @Query("DELETE FROM risk_scores WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)

    @Query("DELETE FROM risk_scores")
    suspend fun deleteAll()
}
