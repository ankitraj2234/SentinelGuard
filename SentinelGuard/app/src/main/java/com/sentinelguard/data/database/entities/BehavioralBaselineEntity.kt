package com.sentinelguard.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Types of behavioral metrics that can have baselines.
 */
enum class BaselineMetricType {
    /** 24-hour usage histogram (JSON array of 24 values) */
    USAGE_HOUR_HISTOGRAM,
    
    /** Average sessions per day */
    SESSIONS_PER_DAY,
    
    /** Average session duration in milliseconds */
    SESSION_DURATION,
    
    /** Frequent location clusters (JSON array of lat/lng/count) */
    LOCATION_CLUSTERS,
    
    /** Typical network state distribution */
    NETWORK_STATE,
    
    /** Screen unlock frequency per day */
    UNLOCK_FREQUENCY,
    
    /** Days since learning started */
    LEARNING_DAYS
}

/**
 * Behavioral baseline entity.
 * 
 * Stores learned user behavior patterns.
 * Uses statistical methods (mean, standard deviation) - no ML.
 */
@Entity(tableName = "behavioral_baselines")
data class BehavioralBaselineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /** Type of metric */
    val metricType: BaselineMetricType,
    
    /** Baseline value (JSON for complex types) */
    val baselineValue: String,
    
    /** Statistical variance/standard deviation where applicable */
    val variance: Double? = null,
    
    /** Confidence level 0.0 - 1.0 (increases over learning period) */
    val confidence: Double = 0.0,
    
    /** Number of data points used to calculate baseline */
    val sampleCount: Int = 0,
    
    /** When baseline was first created */
    val createdAt: Long = System.currentTimeMillis(),
    
    /** Last time baseline was updated */
    val updatedAt: Long = System.currentTimeMillis(),
    
    /** Whether learning period is complete (7-14 days) */
    val learningComplete: Boolean = false
)
