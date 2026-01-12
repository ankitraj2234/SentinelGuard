package com.sentinelguard.security.baseline.metric

import com.sentinelguard.domain.model.BaselineMetricType
import com.sentinelguard.domain.model.BehavioralBaseline
import com.sentinelguard.domain.model.SecuritySignal
import com.sentinelguard.domain.model.SignalType
import com.sentinelguard.domain.util.SecureIdGenerator
import com.sentinelguard.security.baseline.util.Statistics
import org.json.JSONObject
import java.util.Calendar

/**
 * SessionMetric: Tracks Session Frequency and Duration
 * 
 * WHY THIS EXISTS:
 * Users have predictable session patterns:
 * - How many times they open the app per day
 * - How long each session lasts
 * 
 * Deviations from these patterns may indicate:
 * - Someone else using the device
 * - Automated access attempts
 * 
 * ANOMALY DETECTION:
 * - Sessions per day outside mean ± 2σ
 * - Session duration outside mean ± 2σ
 */
class SessionMetric {

    companion object {
        const val ANOMALY_THRESHOLD_SD = 2.0
        const val MIN_SAMPLES = 20
        const val ROLLING_WINDOW_DAYS = 30
    }

    private val dailySessionCounts = mutableListOf<Int>()
    private val sessionDurations = mutableListOf<Long>()
    private var lastSessionDate: String? = null
    private var todaySessionCount = 0

    /**
     * Updates metrics from signals.
     */
    fun updateFromSignals(signals: List<SecuritySignal>): BehavioralBaseline {
        // Group signals by date
        val signalsByDate = signals
            .filter { it.type == SignalType.APP_SESSION || it.type == SignalType.APP_OPEN }
            .groupBy { getDateString(it.timestamp) }

        // Count sessions per day
        for ((date, daySignals) in signalsByDate) {
            val sessionCount = daySignals.count { it.type == SignalType.APP_OPEN }
            
            // Only add if we don't already have this date
            if (dailySessionCounts.size < ROLLING_WINDOW_DAYS) {
                dailySessionCounts.add(sessionCount)
            }
        }

        // Extract session durations
        val sessions = signals.filter { it.type == SignalType.APP_SESSION }
        for (session in sessions) {
            session.value?.toLongOrNull()?.let { durationMs ->
                if (sessionDurations.size < 100) { // Cap stored durations
                    sessionDurations.add(durationMs)
                }
            }
        }

        return buildBaseline()
    }

    /**
     * Records a new session for today.
     */
    fun recordSession(durationMs: Long? = null) {
        val today = getDateString(System.currentTimeMillis())
        
        if (lastSessionDate != today) {
            // New day - save yesterday's count and reset
            if (lastSessionDate != null && todaySessionCount > 0) {
                dailySessionCounts.add(todaySessionCount)
                if (dailySessionCounts.size > ROLLING_WINDOW_DAYS) {
                    dailySessionCounts.removeAt(0)
                }
            }
            todaySessionCount = 0
            lastSessionDate = today
        }
        
        todaySessionCount++
        
        durationMs?.let {
            sessionDurations.add(it)
            if (sessionDurations.size > 100) {
                sessionDurations.removeAt(0)
            }
        }
    }

    /**
     * Checks if today's session count is anomalous.
     */
    fun isTodaySessionCountAnomaly(): Boolean {
        if (dailySessionCounts.size < MIN_SAMPLES) return false

        val values = dailySessionCounts.map { it.toDouble() }
        val mean = Statistics.mean(values)
        val stdDev = Statistics.standardDeviation(values)

        return Statistics.isAnomaly(todaySessionCount.toDouble(), mean, stdDev, ANOMALY_THRESHOLD_SD)
    }

    /**
     * Checks if a session duration is anomalous.
     */
    fun isSessionDurationAnomaly(durationMs: Long): Boolean {
        if (sessionDurations.size < MIN_SAMPLES) return false

        val values = sessionDurations.map { it.toDouble() }
        val mean = Statistics.mean(values)
        val stdDev = Statistics.standardDeviation(values)

        return Statistics.isAnomaly(durationMs.toDouble(), mean, stdDev, ANOMALY_THRESHOLD_SD)
    }

    /**
     * Gets average sessions per day.
     */
    fun getAverageSessionsPerDay(): Double {
        return Statistics.meanInt(dailySessionCounts)
    }

    /**
     * Gets average session duration in milliseconds.
     */
    fun getAverageSessionDurationMs(): Double {
        return Statistics.mean(sessionDurations.map { it.toDouble() })
    }

    private fun getDateString(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}-${cal.get(Calendar.DAY_OF_MONTH)}"
    }

    private fun buildBaseline(): BehavioralBaseline {
        val durationValues = sessionDurations.map { it.toDouble() }
        
        val json = JSONObject().apply {
            put("dailyMean", Statistics.meanInt(dailySessionCounts))
            put("dailyStdDev", Statistics.standardDeviation(dailySessionCounts.map { it.toDouble() }))
            put("durationMeanMs", Statistics.mean(durationValues))
            put("durationStdDevMs", Statistics.standardDeviation(durationValues))
            put("sampleDays", dailySessionCounts.size)
            put("sampleSessions", sessionDurations.size)
        }

        val totalSamples = dailySessionCounts.size + sessionDurations.size

        return BehavioralBaseline(
            id = SecureIdGenerator.generateId(),
            metricType = BaselineMetricType.SESSIONS_PER_DAY,
            value = json.toString(),
            variance = Statistics.variance(durationValues),
            confidence = Statistics.confidence(totalSamples, MIN_SAMPLES),
            sampleCount = totalSamples,
            learningComplete = dailySessionCounts.size >= 7,
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Loads from stored baseline.
     */
    fun loadFromBaseline(baseline: BehavioralBaseline) {
        // Note: We don't restore historical data, just use for reference
        // Real data comes from signals
    }
}
