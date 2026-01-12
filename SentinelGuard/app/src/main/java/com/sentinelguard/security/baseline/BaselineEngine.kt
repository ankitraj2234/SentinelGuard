package com.sentinelguard.security.baseline

import com.sentinelguard.data.local.preferences.SecurePreferences
import com.sentinelguard.domain.model.BaselineMetricType
import com.sentinelguard.domain.model.BehavioralBaseline
import com.sentinelguard.domain.repository.BaselineRepository
import com.sentinelguard.domain.repository.SecuritySignalRepository
import com.sentinelguard.security.baseline.metric.LocationMetric
import com.sentinelguard.security.baseline.metric.SessionMetric
import com.sentinelguard.security.baseline.metric.UsageHourMetric
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * BaselineEngine: Orchestrator for Behavioral Learning
 * 
 * WHY THIS EXISTS:
 * Coordinates all metrics to build and maintain behavioral baselines.
 * Called on app open to update baselines and check for anomalies.
 * 
 * LEARNING PHASES:
 * 1. Initial (0-7 days): Collecting data, no anomaly detection
 * 2. Learning (7-14 days): Building confidence, soft anomaly detection
 * 3. Active (14+ days): Full anomaly detection
 */
class BaselineEngine(
    private val baselineRepository: BaselineRepository,
    private val signalRepository: SecuritySignalRepository,
    private val securePreferences: SecurePreferences
) {
    companion object {
        const val MIN_LEARNING_DAYS = 7
        const val FULL_LEARNING_DAYS = 14
        const val ROLLING_WINDOW_DAYS = 30
    }

    // Metrics
    val usageHourMetric = UsageHourMetric()
    val sessionMetric = SessionMetric()
    val locationMetric = LocationMetric()

    private var initialized = false

    /**
     * Initializes the engine by loading stored baselines.
     */
    suspend fun initialize() = withContext(Dispatchers.IO) {
        if (initialized) return@withContext

        // Load stored baselines
        baselineRepository.getByType(BaselineMetricType.USAGE_HOUR_HISTOGRAM)?.let {
            usageHourMetric.loadFromBaseline(it)
        }
        baselineRepository.getByType(BaselineMetricType.SESSIONS_PER_DAY)?.let {
            sessionMetric.loadFromBaseline(it)
        }
        baselineRepository.getByType(BaselineMetricType.LOCATION_CLUSTERS)?.let {
            locationMetric.loadFromBaseline(it)
        }

        initialized = true
    }

    /**
     * Updates all baselines from recent signals.
     * Called on app open.
     */
    suspend fun updateBaselines() = withContext(Dispatchers.IO) {
        if (!initialized) initialize()

        // Get signals from rolling window
        val windowStart = System.currentTimeMillis() - (ROLLING_WINDOW_DAYS * 24 * 60 * 60 * 1000L)
        val signals = signalRepository.getInRange(windowStart, System.currentTimeMillis())

        // Update each metric
        val hourBaseline = usageHourMetric.updateFromSignals(signals)
        val sessionBaseline = sessionMetric.updateFromSignals(signals)
        val locationBaseline = locationMetric.updateFromSignals(signals)

        // Save updated baselines
        baselineRepository.upsert(hourBaseline)
        baselineRepository.upsert(sessionBaseline)
        baselineRepository.upsert(locationBaseline)
    }

    /**
     * Checks if overall learning is complete.
     */
    suspend fun isLearningComplete(): Boolean = withContext(Dispatchers.IO) {
        val baselines = baselineRepository.getAllLearningComplete()
        return@withContext baselines.any { it.metricType == BaselineMetricType.USAGE_HOUR_HISTOGRAM } &&
                baselines.any { it.metricType == BaselineMetricType.SESSIONS_PER_DAY }
    }

    /**
     * Gets learning progress as a percentage (0.0 to 1.0).
     */
    suspend fun getLearningProgress(): Float = withContext(Dispatchers.IO) {
        val avgConfidence = baselineRepository.getAverageConfidence()
        return@withContext avgConfidence.toFloat().coerceIn(0f, 1f)
    }

    // ============ Anomaly Detection ============

    fun isCurrentHourAnomaly(): Boolean = usageHourMetric.isCurrentHourAnomaly()
    fun isHourAnomaly(hour: Int): Boolean = usageHourMetric.isHourAnomaly(hour)
    fun isTodaySessionCountAnomaly(): Boolean = sessionMetric.isTodaySessionCountAnomaly()
    fun isSessionDurationAnomaly(durationMs: Long): Boolean = sessionMetric.isSessionDurationAnomaly(durationMs)
    fun isLocationAnomaly(latitude: Double, longitude: Double): Boolean = locationMetric.isLocationAnomaly(latitude, longitude)
    fun isLocationKnown(latitude: Double, longitude: Double): Boolean = locationMetric.isLocationKnown(latitude, longitude)

    /**
     * Gets all current anomaly signals.
     */
    fun getCurrentAnomalies(): List<AnomalyResult> {
        val anomalies = mutableListOf<AnomalyResult>()
        if (isCurrentHourAnomaly()) {
            anomalies.add(AnomalyResult(AnomalyType.UNUSUAL_HOUR, "App opened at unusual hour"))
        }
        if (isTodaySessionCountAnomaly()) {
            anomalies.add(AnomalyResult(AnomalyType.UNUSUAL_SESSION_COUNT, "Unusual number of sessions today"))
        }
        return anomalies
    }

    fun checkLocationAnomaly(latitude: Double, longitude: Double): AnomalyResult? {
        return if (isLocationAnomaly(latitude, longitude)) {
            AnomalyResult(AnomalyType.UNKNOWN_LOCATION, "App opened from unknown location")
        } else null
    }

    fun getPeakUsageHours(): List<Int> = usageHourMetric.getPeakHours()
    fun getLocationClusters(): List<LocationMetric.LocationCluster> = locationMetric.getClusters()
    fun getAverageSessionsPerDay(): Double = sessionMetric.getAverageSessionsPerDay()
}

enum class AnomalyType { UNUSUAL_HOUR, UNUSUAL_SESSION_COUNT, UNUSUAL_SESSION_DURATION, UNKNOWN_LOCATION }
data class AnomalyResult(val type: AnomalyType, val description: String)
