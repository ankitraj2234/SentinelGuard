package com.sentinelguard.security.baseline.util

import kotlin.math.sqrt

/**
 * Statistics: Mathematical Utility Functions
 * 
 * WHY THIS EXISTS:
 * Provides statistical calculations for behavioral analysis.
 * All calculations are explainable and auditable - NO ML black boxes.
 */
object Statistics {

    /**
     * Calculates arithmetic mean of a list of values.
     */
    fun mean(values: List<Double>): Double {
        if (values.isEmpty()) return 0.0
        return values.sum() / values.size
    }

    /**
     * Calculates arithmetic mean of integers.
     */
    fun meanInt(values: List<Int>): Double {
        if (values.isEmpty()) return 0.0
        return values.sum().toDouble() / values.size
    }

    /**
     * Calculates population variance.
     * Variance = Σ(x - μ)² / n
     */
    fun variance(values: List<Double>): Double {
        if (values.size < 2) return 0.0
        val avg = mean(values)
        val sumSquaredDiff = values.sumOf { (it - avg) * (it - avg) }
        return sumSquaredDiff / values.size
    }

    /**
     * Calculates sample variance.
     * Sample Variance = Σ(x - μ)² / (n - 1)
     */
    fun sampleVariance(values: List<Double>): Double {
        if (values.size < 2) return 0.0
        val avg = mean(values)
        val sumSquaredDiff = values.sumOf { (it - avg) * (it - avg) }
        return sumSquaredDiff / (values.size - 1)
    }

    /**
     * Calculates population standard deviation.
     */
    fun standardDeviation(values: List<Double>): Double {
        return sqrt(variance(values))
    }

    /**
     * Calculates sample standard deviation.
     */
    fun sampleStandardDeviation(values: List<Double>): Double {
        return sqrt(sampleVariance(values))
    }

    /**
     * Calculates Z-score (how many standard deviations from mean).
     * Z = (x - μ) / σ
     */
    fun zScore(value: Double, mean: Double, stdDev: Double): Double {
        if (stdDev == 0.0) return 0.0
        return (value - mean) / stdDev
    }

    /**
     * Checks if value is an anomaly (outside threshold standard deviations).
     */
    fun isAnomaly(value: Double, mean: Double, stdDev: Double, threshold: Double = 2.0): Boolean {
        if (stdDev == 0.0) return false
        return kotlin.math.abs(zScore(value, mean, stdDev)) > threshold
    }

    /**
     * Calculates confidence score based on sample count.
     * More samples = higher confidence, saturates at 100 samples.
     */
    fun confidence(sampleCount: Int, minSamples: Int = 20, saturationSamples: Int = 100): Double {
        if (sampleCount < minSamples) return 0.0
        if (sampleCount >= saturationSamples) return 1.0
        return (sampleCount - minSamples).toDouble() / (saturationSamples - minSamples)
    }

    /**
     * Calculates distance between two lat/lng points in meters.
     * Uses Haversine formula.
     */
    fun haversineDistance(
        lat1: Double, lng1: Double,
        lat2: Double, lng2: Double
    ): Double {
        val R = 6371000.0 // Earth radius in meters

        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        val deltaLat = Math.toRadians(lat2 - lat1)
        val deltaLng = Math.toRadians(lng2 - lng1)

        val a = kotlin.math.sin(deltaLat / 2) * kotlin.math.sin(deltaLat / 2) +
                kotlin.math.cos(lat1Rad) * kotlin.math.cos(lat2Rad) *
                kotlin.math.sin(deltaLng / 2) * kotlin.math.sin(deltaLng / 2)
        
        val c = 2 * kotlin.math.atan2(sqrt(a), sqrt(1 - a))

        return R * c
    }

    /**
     * Calculates median of a list.
     */
    fun median(values: List<Double>): Double {
        if (values.isEmpty()) return 0.0
        val sorted = values.sorted()
        val mid = sorted.size / 2
        return if (sorted.size % 2 == 0) {
            (sorted[mid - 1] + sorted[mid]) / 2.0
        } else {
            sorted[mid]
        }
    }

    /**
     * Calculates percentile (0-100).
     */
    fun percentile(values: List<Double>, percentile: Double): Double {
        if (values.isEmpty()) return 0.0
        val sorted = values.sorted()
        val index = (percentile / 100.0 * (sorted.size - 1)).toInt()
        return sorted[index.coerceIn(0, sorted.lastIndex)]
    }
}
