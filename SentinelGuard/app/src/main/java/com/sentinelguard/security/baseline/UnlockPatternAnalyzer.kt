package com.sentinelguard.security.baseline

import android.app.KeyguardManager
import android.content.Context
import com.sentinelguard.data.database.dao.BehavioralAnomalyDao
import com.sentinelguard.data.database.dao.UnlockPatternDao
import com.sentinelguard.data.database.entities.BehavioralAnomalyEntity
import com.sentinelguard.data.database.entities.UnlockPatternEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Analyzes device unlock patterns for anomaly detection.
 * 
 * Features:
 * - Tracks unlock frequency by hour
 * - Detects unusual unlock patterns
 * - Monitors failed unlock attempts
 */
@Singleton
class UnlockPatternAnalyzer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val unlockPatternDao: UnlockPatternDao,
    private val anomalyDao: BehavioralAnomalyDao
) {
    
    companion object {
        // Threshold for unusual unlock frequency (times the average)
        private const val UNUSUAL_FREQUENCY_MULTIPLIER = 3.0f
        
        // Minimum data points before detecting anomalies
        private const val MIN_DATA_POINTS = 7
        
        // Risk points for anomalies
        private const val HIGH_FREQUENCY_RISK = 10
        private const val FAILED_ATTEMPTS_RISK = 15
    }
    
    /**
     * Record a device unlock event and check for anomalies.
     * Returns risk points if anomaly detected.
     */
    suspend fun recordUnlock(): Int {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val timestamp = System.currentTimeMillis()
        
        var riskPoints = 0
        
        // Get or create pattern for this hour/day
        val existingPattern = unlockPatternDao.getPattern(hour, dayOfWeek)
        
        if (existingPattern != null) {
            // Check for unusual frequency
            val avgUnlocks = unlockPatternDao.getAverageUnlocksForHour(hour) ?: 0f
            
            if (avgUnlocks > 0 && existingPattern.unlockCount >= MIN_DATA_POINTS) {
                // We have enough data - check if current count is unusual
                val todayUnlocks = existingPattern.unlockCount + 1
                
                if (todayUnlocks > avgUnlocks * UNUSUAL_FREQUENCY_MULTIPLIER) {
                    riskPoints = HIGH_FREQUENCY_RISK
                    logAnomaly(
                        type = "UNLOCK",
                        description = "Unusual unlock frequency: $todayUnlocks times (avg: ${avgUnlocks.toInt()})",
                        severity = 5,
                        riskPoints = riskPoints
                    )
                }
            }
            
            // Update existing pattern
            unlockPatternDao.incrementUnlock(hour, dayOfWeek, timestamp)
        } else {
            // Create new pattern
            unlockPatternDao.insert(UnlockPatternEntity(
                hourOfDay = hour,
                dayOfWeek = dayOfWeek,
                unlockCount = 1,
                failedAttempts = 0,
                avgSessionLengthMs = 0,
                lastUpdated = timestamp
            ))
        }
        
        return riskPoints
    }
    
    /**
     * Record a failed unlock attempt.
     * Returns risk points if multiple failures detected.
     */
    suspend fun recordFailedAttempt(): Int {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        
        var riskPoints = 0
        
        val existingPattern = unlockPatternDao.getPattern(hour, dayOfWeek)
        
        if (existingPattern != null) {
            val newFailedCount = existingPattern.failedAttempts + 1
            
            // Multiple failed attempts is suspicious
            if (newFailedCount >= 3) {
                riskPoints = FAILED_ATTEMPTS_RISK
                logAnomaly(
                    type = "UNLOCK",
                    description = "Multiple failed unlock attempts: $newFailedCount failures",
                    severity = 7,
                    riskPoints = riskPoints
                )
            }
            
            unlockPatternDao.incrementFailedAttempt(hour, dayOfWeek)
        } else {
            // Create pattern with failed attempt
            unlockPatternDao.insert(UnlockPatternEntity(
                hourOfDay = hour,
                dayOfWeek = dayOfWeek,
                unlockCount = 0,
                failedAttempts = 1
            ))
        }
        
        return riskPoints
    }
    
    /**
     * Check if device is currently locked.
     */
    fun isDeviceLocked(): Boolean {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return keyguardManager.isKeyguardLocked
    }
    
    /**
     * Get typical unlock count for current hour.
     */
    suspend fun getTypicalUnlocksForCurrentHour(): Float {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return unlockPatternDao.getAverageUnlocksForHour(hour) ?: 0f
    }
    
    /**
     * Calculate learning progress for unlock patterns.
     */
    suspend fun getLearningProgress(): Float {
        val totalUnlocks = unlockPatternDao.getTotalUnlocks() ?: 0
        
        // Consider "learned" after ~50 unlocks (about a week of normal usage)
        return (totalUnlocks / 50f).coerceIn(0f, 1f)
    }
    
    private suspend fun logAnomaly(
        type: String,
        description: String,
        severity: Int,
        riskPoints: Int
    ) {
        anomalyDao.insert(BehavioralAnomalyEntity(
            anomalyType = type,
            description = description,
            severity = severity,
            riskPoints = riskPoints
        ))
    }
}
