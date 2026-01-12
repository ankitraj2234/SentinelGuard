package com.sentinelguard.data.mapper

import com.sentinelguard.data.local.database.entity.*
import com.sentinelguard.domain.model.*
import com.sentinelguard.domain.util.SecureIdGenerator

/**
 * Entity-Domain Mappers
 * 
 * WHY MAPPERS:
 * Clean architecture requires separation between data layer (Room entities)
 * and domain layer (business models). Mappers convert between these layers.
 * 
 * BENEFITS:
 * - Domain models have no Room/framework dependencies
 * - Database schema can change without affecting domain logic
 * - Easier unit testing of domain logic
 */

// ============ User Mapper ============

fun UserEntity.toDomain(): User = User(
    id = id,
    email = email,
    biometricEnabled = biometricEnabled,
    failedLoginAttempts = failedLoginAttempts,
    lockedUntil = lockedUntil,
    createdAt = createdAt,
    lastLoginAt = lastLoginAt
)

fun User.toEntity(passwordHash: String): UserEntity = UserEntity(
    id = id,
    email = email,
    passwordHash = passwordHash,
    biometricEnabled = biometricEnabled,
    failedLoginAttempts = failedLoginAttempts,
    lockedUntil = lockedUntil,
    createdAt = createdAt,
    lastLoginAt = lastLoginAt
)

// ============ Security Signal Mapper ============

fun SecuritySignalEntity.toDomain(): SecuritySignal = SecuritySignal(
    id = id,
    type = SignalType.valueOf(signalType),
    value = value,
    metadata = metadata,
    timestamp = timestamp,
    processed = processed
)

fun SecuritySignal.toEntity(): SecuritySignalEntity = SecuritySignalEntity(
    id = id,
    signalType = type.name,
    value = value,
    metadata = metadata,
    timestamp = timestamp,
    processed = processed
)

// ============ Behavioral Baseline Mapper ============

fun BehavioralBaselineEntity.toDomain(): BehavioralBaseline = BehavioralBaseline(
    id = id,
    metricType = BaselineMetricType.valueOf(metricType),
    value = value,
    variance = variance,
    confidence = confidence,
    sampleCount = sampleCount,
    learningComplete = learningComplete,
    updatedAt = updatedAt
)

fun BehavioralBaseline.toEntity(): BehavioralBaselineEntity = BehavioralBaselineEntity(
    id = id,
    metricType = metricType.name,
    value = value,
    variance = variance,
    confidence = confidence,
    sampleCount = sampleCount,
    learningComplete = learningComplete,
    updatedAt = updatedAt
)

// ============ Risk Score Mapper ============

fun RiskScoreEntity.toDomain(): RiskScore {
    val contributionsMap = parseContributions(signalContributions)
    return RiskScore(
        id = id,
        totalScore = totalScore,
        level = RiskLevel.valueOf(riskLevel),
        contributions = contributionsMap,
        triggerReason = triggerReason,
        decayed = decayed,
        timestamp = timestamp
    )
}

fun RiskScore.toEntity(): RiskScoreEntity = RiskScoreEntity(
    id = id,
    totalScore = totalScore,
    currentScore = totalScore,
    riskLevel = level.name,
    signalContributions = formatContributions(contributions),
    triggerReason = triggerReason,
    decayed = decayed,
    timestamp = timestamp
)

private fun parseContributions(json: String): Map<SignalType, Int> {
    return try {
        val obj = org.json.JSONObject(json)
        val result = mutableMapOf<SignalType, Int>()
        obj.keys().forEach { key ->
            result[SignalType.valueOf(key)] = obj.getInt(key)
        }
        result
    } catch (e: Exception) {
        emptyMap()
    }
}

private fun formatContributions(map: Map<SignalType, Int>): String {
    val obj = org.json.JSONObject()
    map.forEach { (type, score) -> obj.put(type.name, score) }
    return obj.toString()
}

// ============ Incident Mapper ============

fun IncidentEntity.toDomain(): Incident {
    val triggers = parseTriggers(triggeredBy)
    val actions = parseActions(actionsTaken)
    val loc = location?.let { parseLocation(it) }
    
    return Incident(
        id = id,
        severity = IncidentSeverity.valueOf(severity),
        riskScore = riskScore,
        triggers = triggers,
        actionsTaken = actions,
        summary = summary,
        location = loc,
        resolved = resolved,
        timestamp = timestamp
    )
}

fun Incident.toEntity(): IncidentEntity = IncidentEntity(
    id = id,
    severity = severity.name,
    riskScore = riskScore,
    triggeredBy = formatTriggers(triggers),
    actionsTaken = formatActions(actionsTaken),
    summary = summary,
    location = location?.let { formatLocation(it) },
    resolved = resolved,
    timestamp = timestamp
)

private fun parseTriggers(json: String): List<SignalType> {
    return try {
        val array = org.json.JSONArray(json)
        (0 until array.length()).map { SignalType.valueOf(array.getString(it)) }
    } catch (e: Exception) {
        emptyList()
    }
}

private fun formatTriggers(list: List<SignalType>): String {
    return org.json.JSONArray(list.map { it.name }).toString()
}

private fun parseActions(json: String): List<ResponseAction> {
    return try {
        val array = org.json.JSONArray(json)
        (0 until array.length()).map { ResponseAction.valueOf(array.getString(it)) }
    } catch (e: Exception) {
        emptyList()
    }
}

private fun formatActions(list: List<ResponseAction>): String {
    return org.json.JSONArray(list.map { it.name }).toString()
}

private fun parseLocation(json: String): LocationData? {
    return try {
        val obj = org.json.JSONObject(json)
        LocationData(
            latitude = obj.getDouble("lat"),
            longitude = obj.getDouble("lng"),
            accuracy = obj.getDouble("accuracy").toFloat()
        )
    } catch (e: Exception) {
        null
    }
}

private fun formatLocation(loc: LocationData): String {
    return org.json.JSONObject().apply {
        put("lat", loc.latitude)
        put("lng", loc.longitude)
        put("accuracy", loc.accuracy)
    }.toString()
}

// ============ Alert Queue Mapper ============

fun AlertQueueEntity.toDomain(): AlertQueueItem = AlertQueueItem(
    id = id,
    recipientEmail = recipientEmail,
    subject = subject,
    body = body,
    status = AlertStatus.valueOf(status),
    incidentId = incidentId,
    retryCount = retryCount,
    lastError = lastError,
    nextRetryAt = nextRetryAt,
    createdAt = createdAt,
    sentAt = sentAt
)

fun AlertQueueItem.toEntity(): AlertQueueEntity = AlertQueueEntity(
    id = id,
    recipientEmail = recipientEmail,
    subject = subject,
    body = body,
    status = status.name,
    incidentId = incidentId,
    retryCount = retryCount,
    lastError = lastError,
    nextRetryAt = nextRetryAt,
    createdAt = createdAt,
    sentAt = sentAt
)
