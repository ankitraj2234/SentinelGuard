package com.sentinelguard.data.local.database.dao

import androidx.room.*
import com.sentinelguard.data.local.database.entity.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO: User Operations
 * 
 * Handles all user account database operations.
 * Only one user account exists at a time.
 */
@Dao
interface UserDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)
    
    @Update
    suspend fun update(user: UserEntity)
    
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUser(): UserEntity?
    
    @Query("SELECT * FROM users LIMIT 1")
    fun observeUser(): Flow<UserEntity?>
    
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?
    
    @Query("SELECT COUNT(*) > 0 FROM users")
    suspend fun hasUser(): Boolean
    
    @Query("UPDATE users SET failedLoginAttempts = failedLoginAttempts + 1 WHERE id = :userId")
    suspend fun incrementFailedAttempts(userId: String)
    
    @Query("UPDATE users SET failedLoginAttempts = 0 WHERE id = :userId")
    suspend fun resetFailedAttempts(userId: String)
    
    @Query("UPDATE users SET lockedUntil = :until WHERE id = :userId")
    suspend fun setLockout(userId: String, until: Long?)
    
    @Query("UPDATE users SET biometricEnabled = :enabled WHERE id = :userId")
    suspend fun setBiometricEnabled(userId: String, enabled: Boolean)
    
    @Query("UPDATE users SET lastLoginAt = :timestamp WHERE id = :userId")
    suspend fun updateLastLogin(userId: String, timestamp: Long)
    
    @Query("UPDATE users SET passwordHash = :hash WHERE id = :userId")
    suspend fun updatePasswordHash(userId: String, hash: String)
    
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun delete(userId: String)
    
    @Query("DELETE FROM users")
    suspend fun deleteAll()
}

/**
 * DAO: Security Signal Operations
 * 
 * Handles storage and retrieval of security events.
 * Optimized for time-range queries and type filtering.
 */
@Dao
interface SecuritySignalDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(signal: SecuritySignalEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(signals: List<SecuritySignalEntity>)
    
    @Query("SELECT * FROM security_signals ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<SecuritySignalEntity>
    
    @Query("SELECT * FROM security_signals ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<SecuritySignalEntity>>
    
    @Query("SELECT * FROM security_signals WHERE signalType = :type ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getByType(type: String, limit: Int): List<SecuritySignalEntity>
    
    @Query("SELECT * FROM security_signals WHERE signalType = :type AND timestamp >= :since ORDER BY timestamp DESC")
    suspend fun getByTypeSince(type: String, since: Long): List<SecuritySignalEntity>
    
    @Query("SELECT * FROM security_signals WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    suspend fun getInRange(startTime: Long, endTime: Long): List<SecuritySignalEntity>
    
    @Query("SELECT * FROM security_signals WHERE processed = 0 ORDER BY timestamp ASC")
    suspend fun getUnprocessed(): List<SecuritySignalEntity>
    
    @Query("UPDATE security_signals SET processed = 1 WHERE id = :id")
    suspend fun markProcessed(id: String)
    
    @Query("DELETE FROM security_signals WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long): Int
    
    @Query("SELECT COUNT(*) FROM security_signals")
    suspend fun count(): Int
    
    @Query("DELETE FROM security_signals")
    suspend fun deleteAll()
}

/**
 * DAO: Behavioral Baseline Operations
 * 
 * Handles learned behavior patterns.
 * Uses UPSERT for updating existing baselines.
 */
@Dao
interface BehavioralBaselineDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(baseline: BehavioralBaselineEntity)
    
    @Query("SELECT * FROM behavioral_baselines WHERE metricType = :type LIMIT 1")
    suspend fun getByType(type: String): BehavioralBaselineEntity?
    
    @Query("SELECT * FROM behavioral_baselines")
    suspend fun getAll(): List<BehavioralBaselineEntity>
    
    @Query("SELECT * FROM behavioral_baselines")
    fun observeAll(): Flow<List<BehavioralBaselineEntity>>
    
    @Query("SELECT * FROM behavioral_baselines WHERE learningComplete = 1")
    suspend fun getAllLearningComplete(): List<BehavioralBaselineEntity>
    
    @Query("SELECT AVG(confidence) FROM behavioral_baselines")
    suspend fun getAverageConfidence(): Double?
    
    @Query("DELETE FROM behavioral_baselines")
    suspend fun deleteAll()
}

/**
 * DAO: Risk Score Operations
 * 
 * Handles risk assessment records.
 * Supports decay updates and trend queries.
 */
@Dao
interface RiskScoreDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(score: RiskScoreEntity)
    
    @Query("SELECT * FROM risk_scores ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(): RiskScoreEntity?
    
    @Query("SELECT * FROM risk_scores ORDER BY timestamp DESC LIMIT 1")
    fun observeLatest(): Flow<RiskScoreEntity?>
    
    @Query("SELECT * FROM risk_scores ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<RiskScoreEntity>
    
    @Query("SELECT * FROM risk_scores ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<RiskScoreEntity>>
    
    @Query("SELECT * FROM risk_scores WHERE riskLevel = :level ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getByLevel(level: String, limit: Int): List<RiskScoreEntity>
    
    @Query("UPDATE risk_scores SET currentScore = :newScore, decayed = 1 WHERE id = :id")
    suspend fun updateDecayedScore(id: String, newScore: Int)
    
    @Query("DELETE FROM risk_scores WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long): Int
    
    @Query("DELETE FROM risk_scores")
    suspend fun deleteAll()
}

/**
 * DAO: Incident Operations
 * 
 * Handles forensic timeline records.
 * Incidents are permanent unless explicitly deleted by user.
 */
@Dao
interface IncidentDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(incident: IncidentEntity): Long
    
    @Query("SELECT * FROM incidents WHERE id = :id")
    suspend fun getById(id: String): IncidentEntity?
    
    @Query("SELECT * FROM incidents ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<IncidentEntity>>
    
    @Query("SELECT * FROM incidents ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<IncidentEntity>>
    
    @Query("SELECT * FROM incidents WHERE resolved = 0 ORDER BY timestamp DESC")
    suspend fun getUnresolved(): List<IncidentEntity>
    
    @Query("SELECT * FROM incidents WHERE severity = :severity ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getBySeverity(severity: String, limit: Int): List<IncidentEntity>
    
    @Query("SELECT * FROM incidents WHERE severity IN (:severities) ORDER BY timestamp DESC")
    fun observeBySeverities(severities: List<String>): Flow<List<IncidentEntity>>
    
    @Query("SELECT * FROM incidents WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    suspend fun getInRange(startTime: Long, endTime: Long): List<IncidentEntity>
    
    @Query("UPDATE incidents SET resolved = 1 WHERE id = :id")
    suspend fun markResolved(id: String)
    
    @Query("DELETE FROM incidents")
    suspend fun deleteAll()
}

/**
 * DAO: Alert Queue Operations
 * 
 * Handles email alert queue with retry logic.
 */
@Dao
interface AlertQueueDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: AlertQueueEntity): Long
    
    @Query("SELECT * FROM alert_queue WHERE status IN ('QUEUED', 'FAILED') AND (nextRetryAt IS NULL OR nextRetryAt <= :now) ORDER BY createdAt ASC")
    suspend fun getPendingSend(now: Long = System.currentTimeMillis()): List<AlertQueueEntity>
    
    @Query("SELECT * FROM alert_queue WHERE status = :status ORDER BY createdAt DESC")
    suspend fun getByStatus(status: String): List<AlertQueueEntity>
    
    @Query("SELECT * FROM alert_queue WHERE incidentId = :incidentId")
    suspend fun getByIncidentId(incidentId: String): List<AlertQueueEntity>
    
    @Query("UPDATE alert_queue SET status = :status, lastError = :error WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, error: String? = null)
    
    @Query("UPDATE alert_queue SET retryCount = retryCount + 1, nextRetryAt = :nextRetryAt, status = 'FAILED' WHERE id = :id")
    suspend fun incrementRetry(id: String, nextRetryAt: Long)
    
    @Query("UPDATE alert_queue SET status = 'SENT', sentAt = :sentAt WHERE id = :id")
    suspend fun markSent(id: String, sentAt: Long = System.currentTimeMillis())
    
    @Query("DELETE FROM alert_queue WHERE createdAt < :timestamp AND status = 'SENT'")
    suspend fun deleteOlderThan(timestamp: Long): Int
    
    @Query("DELETE FROM alert_queue")
    suspend fun deleteAll()
}
