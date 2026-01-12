package com.sentinelguard.domain.repository

import com.sentinelguard.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for User operations.
 * 
 * Defined in domain layer - implemented in data layer.
 * This separation allows testing domain logic without database.
 */
interface UserRepository {
    suspend fun createUser(email: String, passwordHash: String): Result<User>
    suspend fun getUser(): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun updateUser(user: User)
    suspend fun incrementFailedAttempts(userId: String)
    suspend fun resetFailedAttempts(userId: String)
    suspend fun setLockout(userId: String, until: Long)
    suspend fun clearLockout(userId: String)
    suspend fun enableBiometric(userId: String, enabled: Boolean)
    suspend fun updateLastLogin(userId: String)
    suspend fun deleteUser(userId: String)
    suspend fun hasUser(): Boolean
}

/**
 * Repository interface for Security Signals.
 */
interface SecuritySignalRepository {
    suspend fun insert(signal: SecuritySignal)
    suspend fun insertAll(signals: List<SecuritySignal>)
    fun observeRecent(limit: Int): Flow<List<SecuritySignal>>
    suspend fun getRecent(limit: Int): List<SecuritySignal>
    suspend fun getByType(type: SignalType, limit: Int): List<SecuritySignal>
    suspend fun getByTypeSince(type: SignalType, since: Long): List<SecuritySignal>
    suspend fun getInRange(startTime: Long, endTime: Long): List<SecuritySignal>
    suspend fun getUnprocessed(): List<SecuritySignal>
    suspend fun markProcessed(id: String)
    suspend fun deleteOlderThan(timestamp: Long): Int
    suspend fun deleteAll()
}

/**
 * Repository interface for Behavioral Baselines.
 */
interface BaselineRepository {
    suspend fun upsert(baseline: BehavioralBaseline)
    suspend fun getByType(type: BaselineMetricType): BehavioralBaseline?
    suspend fun getAllLearningComplete(): List<BehavioralBaseline>
    suspend fun getAverageConfidence(): Double
    suspend fun deleteAll()
}

/**
 * Repository interface for Risk Scores.
 */
interface RiskScoreRepository {
    suspend fun insert(score: RiskScore)
    suspend fun getLatest(): RiskScore?
    fun observeLatest(): Flow<RiskScore?>
    suspend fun getRecent(limit: Int): List<RiskScore>
    suspend fun getByLevel(level: RiskLevel, limit: Int): List<RiskScore>
    suspend fun updateDecayedScore(id: String, newScore: Int)
    suspend fun deleteOlderThan(timestamp: Long): Int
}

/**
 * Repository interface for Security Incidents.
 */
interface IncidentRepository {
    suspend fun insert(incident: Incident): String
    suspend fun getById(id: String): Incident?
    fun observeAll(): Flow<List<Incident>>
    fun observeRecent(limit: Int): Flow<List<Incident>>
    suspend fun getUnresolved(): List<Incident>
    suspend fun getBySeverity(severity: IncidentSeverity, limit: Int): List<Incident>
    suspend fun getInRange(startTime: Long, endTime: Long): List<Incident>
    suspend fun markResolved(id: String)
    suspend fun deleteAll()
}

/**
 * Repository interface for Alert Queue.
 */
interface AlertQueueRepository {
    suspend fun insert(alert: AlertQueueItem): String
    suspend fun getPending(): List<AlertQueueItem>
    suspend fun getByStatus(status: AlertStatus): List<AlertQueueItem>
    suspend fun getByIncidentId(incidentId: String): List<AlertQueueItem>
    suspend fun updateStatus(id: String, status: AlertStatus, error: String? = null)
    suspend fun incrementRetry(id: String, nextRetryAt: Long)
    suspend fun markSent(id: String)
    suspend fun deleteOlderThan(timestamp: Long): Int
}
