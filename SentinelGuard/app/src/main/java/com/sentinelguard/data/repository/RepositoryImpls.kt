package com.sentinelguard.data.repository

import com.sentinelguard.data.database.dao.*
import com.sentinelguard.data.database.entities.*
import com.sentinelguard.data.local.preferences.SecurePreferences
import com.sentinelguard.domain.model.AlertQueueItem
import com.sentinelguard.domain.model.BehavioralBaseline
import com.sentinelguard.domain.model.Incident
import com.sentinelguard.domain.model.LocationData
import com.sentinelguard.domain.model.RiskScore
import com.sentinelguard.domain.model.SecuritySignal
import com.sentinelguard.domain.model.User
import com.sentinelguard.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.sentinelguard.domain.model.AlertStatus as DomainAlertStatus
import com.sentinelguard.domain.model.BaselineMetricType as DomainBaselineMetricType
import com.sentinelguard.domain.model.IncidentSeverity as DomainIncidentSeverity
import com.sentinelguard.domain.model.ResponseAction as DomainResponseAction
import com.sentinelguard.domain.model.RiskLevel as DomainRiskLevel
import com.sentinelguard.domain.model.SignalType as DomainSignalType

/**
 * Repository Implementations
 */

// ============ User Repository ============

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val securePreferences: SecurePreferences
) : UserRepository {

    override suspend fun createUser(email: String, passwordHash: String): Result<User> {
        return try {
            val entity = UserEntity(
                email = email,
                passwordHash = passwordHash,
                createdAt = System.currentTimeMillis()
            )
            val id = userDao.insert(entity)
            securePreferences.isSetupComplete = true
            Result.success(User(
                id = id.toString(),
                email = email,
                biometricEnabled = false,
                failedLoginAttempts = 0,
                lockedUntil = null,
                createdAt = entity.createdAt,
                lastLoginAt = null
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(): User? {
        return userDao.getFirstUser()?.toUser()
    }

    override suspend fun getUserByEmail(email: String): User? {
        return userDao.getByEmail(email)?.toUser()
    }

    override suspend fun updateUser(user: User) {
        val existing = userDao.getById(user.id.toLong()) ?: return
        userDao.update(existing.copy(biometricEnabled = user.biometricEnabled))
    }

    override suspend fun incrementFailedAttempts(userId: String) {
        val existing = userDao.getById(userId.toLong()) ?: return
        userDao.updateLoginAttempts(userId.toLong(), existing.failedLoginAttempts + 1, existing.lockoutUntil)
    }

    override suspend fun resetFailedAttempts(userId: String) {
        userDao.updateLoginAttempts(userId.toLong(), 0, null)
    }

    override suspend fun setLockout(userId: String, until: Long) {
        val existing = userDao.getById(userId.toLong()) ?: return
        userDao.updateLoginAttempts(userId.toLong(), existing.failedLoginAttempts, until)
    }

    override suspend fun clearLockout(userId: String) {
        val existing = userDao.getById(userId.toLong()) ?: return
        userDao.updateLoginAttempts(userId.toLong(), existing.failedLoginAttempts, null)
    }

    override suspend fun enableBiometric(userId: String, enabled: Boolean) {
        userDao.updateBiometricEnabled(userId.toLong(), enabled)
    }

    override suspend fun updateLastLogin(userId: String) {
        userDao.updateSuccessfulLogin(userId.toLong(), System.currentTimeMillis())
    }

    override suspend fun deleteUser(userId: String) {
        userDao.deleteById(userId.toLong())
        securePreferences.clearAll()
    }

    override suspend fun hasUser(): Boolean {
        return userDao.getUserCount() > 0
    }

    private fun UserEntity.toUser() = User(
        id = id.toString(),
        email = email,
        biometricEnabled = biometricEnabled,
        failedLoginAttempts = failedLoginAttempts,
        lockedUntil = lockoutUntil,
        createdAt = createdAt,
        lastLoginAt = lastLoginAt
    )
}

// ============ Security Signal Repository ============

class SecuritySignalRepositoryImpl(
    private val dao: SecuritySignalDao
) : SecuritySignalRepository {

    override suspend fun insert(signal: SecuritySignal) {
        dao.insert(SecuritySignalEntity(
            signalType = signal.type.toEntity(),
            value = signal.value,
            metadata = signal.metadata,
            timestamp = signal.timestamp
        ))
    }

    override suspend fun insertAll(signals: List<SecuritySignal>) {
        dao.insertAll(signals.map { signal ->
            SecuritySignalEntity(
                signalType = signal.type.toEntity(),
                value = signal.value,
                metadata = signal.metadata,
                timestamp = signal.timestamp
            )
        })
    }

    override fun observeRecent(limit: Int): Flow<List<SecuritySignal>> {
        return dao.observeRecent(limit).map { entities -> entities.map { it.toSignal() } }
    }

    override suspend fun getRecent(limit: Int): List<SecuritySignal> {
        return dao.getRecent(limit).map { it.toSignal() }
    }

    override suspend fun getByType(type: DomainSignalType, limit: Int): List<SecuritySignal> {
        return dao.getByType(type.toEntity(), limit).map { it.toSignal() }
    }

    override suspend fun getByTypeSince(type: DomainSignalType, since: Long): List<SecuritySignal> {
        return dao.getByTypeSince(type.toEntity(), since).map { it.toSignal() }
    }

    override suspend fun getInRange(startTime: Long, endTime: Long): List<SecuritySignal> {
        return dao.getInRange(startTime, endTime).map { it.toSignal() }
    }

    override suspend fun getUnprocessed(): List<SecuritySignal> {
        return dao.getUnprocessed().map { it.toSignal() }
    }

    override suspend fun markProcessed(id: String) {
        val longId = id.toLongOrNull() ?: return
        dao.markProcessed(listOf(longId))
    }

    override suspend fun deleteOlderThan(timestamp: Long): Int {
        dao.deleteOlderThan(timestamp)
        return 0
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }

    private fun SecuritySignalEntity.toSignal() = SecuritySignal(
        id = id.toString(),
        type = signalType.toDomain(),
        value = value,
        metadata = metadata,
        timestamp = timestamp
    )

    private fun DomainSignalType.toEntity(): SignalType = SignalType.valueOf(name)
    private fun SignalType.toDomain(): DomainSignalType = DomainSignalType.valueOf(name)
}

// ============ Baseline Repository ============

class BaselineRepositoryImpl(
    private val dao: BehavioralBaselineDao
) : BaselineRepository {

    override suspend fun upsert(baseline: BehavioralBaseline) {
        dao.upsert(BehavioralBaselineEntity(
            metricType = baseline.metricType.toEntity(),
            baselineValue = baseline.value,
            variance = baseline.variance,
            confidence = baseline.confidence,
            sampleCount = baseline.sampleCount,
            learningComplete = baseline.learningComplete,
            updatedAt = System.currentTimeMillis()
        ))
    }

    override suspend fun getByType(type: DomainBaselineMetricType): BehavioralBaseline? {
        return dao.getByType(type.toEntity())?.toBaseline()
    }

    override suspend fun getAllLearningComplete(): List<BehavioralBaseline> {
        return dao.getCompletedBaselines().map { it.toBaseline() }
    }

    override suspend fun getAverageConfidence(): Double {
        return dao.getAverageConfidence() ?: 0.0
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }

    private fun BehavioralBaselineEntity.toBaseline() = BehavioralBaseline(
        id = id.toString(),
        metricType = metricType.toDomain(),
        value = baselineValue,
        variance = variance,
        confidence = confidence,
        sampleCount = sampleCount,
        learningComplete = learningComplete,
        updatedAt = updatedAt
    )

    private fun DomainBaselineMetricType.toEntity(): BaselineMetricType = BaselineMetricType.valueOf(name)
    private fun BaselineMetricType.toDomain(): DomainBaselineMetricType = DomainBaselineMetricType.valueOf(name)
}

// ============ Risk Score Repository ============

class RiskScoreRepositoryImpl(
    private val dao: RiskScoreDao
) : RiskScoreRepository {

    override suspend fun insert(score: RiskScore) {
        dao.insert(RiskScoreEntity(
            totalScore = score.totalScore,
            riskLevel = score.level.toEntity(),
            signalContributions = score.triggerReason,
            timestamp = score.timestamp
        ))
    }

    override suspend fun getLatest(): RiskScore? {
        return dao.getLatest()?.toRiskScore()
    }

    override fun observeLatest(): Flow<RiskScore?> {
        return dao.observeLatest().map { it?.toRiskScore() }
    }

    override suspend fun getRecent(limit: Int): List<RiskScore> {
        return dao.getRecent(limit).map { it.toRiskScore() }
    }

    override suspend fun getByLevel(level: DomainRiskLevel, limit: Int): List<RiskScore> {
        return dao.getByLevel(level.toEntity()).map { it.toRiskScore() }
    }

    override suspend fun updateDecayedScore(id: String, newScore: Int) {
        dao.updateDecayedScore(id.toLong(), newScore)
    }

    override suspend fun deleteOlderThan(timestamp: Long): Int {
        dao.deleteOlderThan(timestamp)
        return 0
    }

    private fun RiskScoreEntity.toRiskScore() = RiskScore(
        id = id.toString(),
        totalScore = totalScore,
        level = riskLevel.toDomain(),
        contributions = emptyMap(),
        triggerReason = signalContributions,
        timestamp = timestamp
    )

    private fun DomainRiskLevel.toEntity(): RiskLevel = RiskLevel.valueOf(name)
    private fun RiskLevel.toDomain(): DomainRiskLevel = DomainRiskLevel.valueOf(name)
}

// ============ Incident Repository ============

class IncidentRepositoryImpl(
    private val dao: IncidentDao
) : IncidentRepository {

    override suspend fun insert(incident: Incident): String {
        val id = dao.insert(IncidentEntity(
            severity = incident.severity.toEntity(),
            riskScore = incident.riskScore,
            triggeredBy = incident.triggers.joinToString(",") { it.name },
            actionsTaken = incident.actionsTaken.joinToString(",") { it.name },
            summary = incident.summary,
            location = incident.location?.let { "${it.latitude},${it.longitude}" },
            resolved = incident.resolved,
            timestamp = incident.timestamp
        ))
        return id.toString()
    }

    override suspend fun getById(id: String): Incident? {
        return dao.getById(id.toLong())?.toIncident()
    }

    override fun observeAll(): Flow<List<Incident>> {
        return dao.observeAll().map { entities -> entities.map { it.toIncident() } }
    }

    override fun observeRecent(limit: Int): Flow<List<Incident>> {
        return dao.observeRecent(limit).map { entities -> entities.map { it.toIncident() } }
    }

    override suspend fun getUnresolved(): List<Incident> {
        return dao.getUnresolved().map { it.toIncident() }
    }

    override suspend fun getBySeverity(severity: DomainIncidentSeverity, limit: Int): List<Incident> {
        return dao.getBySeverity(severity.toEntity()).map { it.toIncident() }
    }

    override suspend fun getInRange(startTime: Long, endTime: Long): List<Incident> {
        return dao.getInRange(startTime, endTime).map { it.toIncident() }
    }

    override suspend fun markResolved(id: String) {
        dao.markResolved(id.toLong())
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }

    private fun IncidentEntity.toIncident(): Incident {
        val coords = location?.split(",")
        val lat = coords?.getOrNull(0)?.toDoubleOrNull()
        val lng = coords?.getOrNull(1)?.toDoubleOrNull()
        return Incident(
            id = id.toString(),
            severity = severity.toDomain(),
            riskScore = riskScore,
            triggers = triggeredBy.split(",").filter { it.isNotEmpty() }.map { DomainSignalType.valueOf(it) },
            actionsTaken = actionsTaken.split(",").filter { it.isNotEmpty() }.map { DomainResponseAction.valueOf(it) },
            summary = summary,
            location = if (lat != null && lng != null) LocationData(lat, lng, 0f) else null,
            resolved = resolved,
            timestamp = timestamp
        )
    }

    private fun DomainIncidentSeverity.toEntity(): IncidentSeverity = IncidentSeverity.valueOf(name)
    private fun IncidentSeverity.toDomain(): DomainIncidentSeverity = DomainIncidentSeverity.valueOf(name)
}

// ============ Alert Queue Repository ============

class AlertQueueRepositoryImpl(
    private val dao: AlertQueueDao
) : AlertQueueRepository {

    override suspend fun insert(alert: AlertQueueItem): String {
        val id = dao.insert(AlertQueueEntity(
            recipientEmail = alert.recipientEmail,
            subject = alert.subject,
            body = alert.body,
            status = alert.status.name,
            incidentId = alert.incidentId?.toLongOrNull(),
            retryCount = alert.retryCount,
            createdAt = alert.createdAt
        ))
        return id.toString()
    }

    override suspend fun getPending(): List<AlertQueueItem> {
        return dao.getPending().map { it.toAlertItem() }
    }

    override suspend fun getByStatus(status: DomainAlertStatus): List<AlertQueueItem> {
        return dao.getByStatus(status.name).map { it.toAlertItem() }
    }

    override suspend fun getByIncidentId(incidentId: String): List<AlertQueueItem> {
        return dao.getByIncidentId(incidentId.toLong()).map { it.toAlertItem() }
    }

    override suspend fun updateStatus(id: String, status: DomainAlertStatus, error: String?) {
        dao.updateStatus(id.toLong(), status.name, error)
    }

    override suspend fun incrementRetry(id: String, nextRetryAt: Long) {
        dao.incrementRetry(id.toLong(), nextRetryAt)
    }

    override suspend fun markSent(id: String) {
        dao.markSent(id.toLong())
    }

    override suspend fun deleteOlderThan(timestamp: Long): Int {
        return dao.deleteOlderThan(timestamp)
    }

    private fun AlertQueueEntity.toAlertItem() = AlertQueueItem(
        id = id.toString(),
        recipientEmail = recipientEmail,
        subject = subject,
        body = body,
        status = DomainAlertStatus.valueOf(status),
        incidentId = incidentId?.toString(),
        retryCount = retryCount,
        lastError = lastError,
        nextRetryAt = nextRetryAt,
        createdAt = createdAt,
        sentAt = sentAt
    )
}
