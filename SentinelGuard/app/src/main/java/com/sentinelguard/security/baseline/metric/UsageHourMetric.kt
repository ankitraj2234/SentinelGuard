package com.sentinelguard.security.baseline.metric

import com.sentinelguard.domain.model.BaselineMetricType
import com.sentinelguard.domain.model.BehavioralBaseline
import com.sentinelguard.domain.model.SecuritySignal
import com.sentinelguard.domain.model.SignalType
import com.sentinelguard.domain.util.SecureIdGenerator
import com.sentinelguard.security.baseline.util.Statistics
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

/**
 * UsageHourMetric: Tracks Hour-of-Day Usage Patterns
 * 
 * WHY THIS EXISTS:
 * Users have predictable daily patterns. Someone opening the app at 3 AM
 * when they normally only use it 8 AM - 10 PM is suspicious.
 * 
 * DATA STRUCTURE:
 * - Histogram with 24 buckets (one per hour)
 * - Each bucket counts app opens during that hour
 * 
 * ANOMALY DETECTION:
 * - Calculate mean and stddev of hour counts
 * - Current hour is anomaly if its count < mean - 2σ AND current hour has 0 opens
 */
class UsageHourMetric {

    companion object {
        const val ANOMALY_THRESHOLD_SD = 2.0
        const val MIN_SAMPLES = 20
    }

    private var histogram = IntArray(24) { 0 }
    private var totalSamples = 0

    /**
     * Updates histogram from a list of signals.
     */
    fun updateFromSignals(signals: List<SecuritySignal>): BehavioralBaseline {
        // Filter to APP_OPEN signals only
        val appOpens = signals.filter { it.type == SignalType.APP_OPEN }

        // Parse hour from each signal and increment histogram
        for (signal in appOpens) {
            val hour = extractHour(signal)
            if (hour in 0..23) {
                histogram[hour]++
                totalSamples++
            }
        }

        return buildBaseline()
    }

    /**
     * Checks if current hour is anomalous.
     */
    fun isCurrentHourAnomaly(): Boolean {
        if (totalSamples < MIN_SAMPLES) return false
        
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return isHourAnomaly(currentHour)
    }

    /**
     * Checks if a specific hour is anomalous based on learned patterns.
     */
    fun isHourAnomaly(hour: Int): Boolean {
        if (totalSamples < MIN_SAMPLES) return false
        if (hour !in 0..23) return false

        val hourValues = histogram.map { it.toDouble() }
        val mean = Statistics.mean(hourValues)
        val stdDev = Statistics.standardDeviation(hourValues)

        // An hour is anomalous if:
        // 1. User has never opened app at this hour (count = 0)
        // 2. AND this hour's count is significantly below average
        val hourCount = histogram[hour]
        
        if (hourCount == 0 && mean > 0) {
            // Never used at this hour - check how unusual that is
            return stdDev > 0 && mean > stdDev // Means there's variance in usage
        }

        return false
    }

    /**
     * Gets the histogram as a map for debugging.
     */
    fun getHistogram(): Map<Int, Int> {
        return histogram.mapIndexed { hour, count -> hour to count }.toMap()
    }

    /**
     * Gets peak usage hours (top 3).
     */
    fun getPeakHours(): List<Int> {
        return histogram
            .mapIndexed { hour, count -> hour to count }
            .sortedByDescending { it.second }
            .take(3)
            .map { it.first }
    }

    private fun extractHour(signal: SecuritySignal): Int {
        // Try to extract from metadata first
        signal.metadata?.let {
            try {
                val json = JSONObject(it)
                if (json.has("hour")) {
                    return json.getInt("hour")
                }
            } catch (e: Exception) {
                // Fall through to timestamp calculation
            }
        }

        // Calculate from timestamp
        val calendar = Calendar.getInstance().apply { timeInMillis = signal.timestamp }
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    private fun buildBaseline(): BehavioralBaseline {
        val histogramJson = JSONArray(histogram.toTypedArray())
        val hourValues = histogram.map { it.toDouble() }

        return BehavioralBaseline(
            id = SecureIdGenerator.generateId(),
            metricType = BaselineMetricType.USAGE_HOUR_HISTOGRAM,
            value = histogramJson.toString(),
            variance = Statistics.variance(hourValues),
            confidence = Statistics.confidence(totalSamples, MIN_SAMPLES),
            sampleCount = totalSamples,
            learningComplete = totalSamples >= MIN_SAMPLES,
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Loads baseline from stored data.
     */
    fun loadFromBaseline(baseline: BehavioralBaseline) {
        try {
            val json = JSONArray(baseline.value)
            for (i in 0 until minOf(json.length(), 24)) {
                histogram[i] = json.getInt(i)
            }
            totalSamples = baseline.sampleCount
        } catch (e: Exception) {
            // Reset on error
            histogram = IntArray(24) { 0 }
            totalSamples = 0
        }
    }
}
