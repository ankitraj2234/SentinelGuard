package com.sentinelguard.security.baseline

import android.content.Context
import com.sentinelguard.data.database.dao.AppUsagePatternDao
import com.sentinelguard.data.database.dao.BehavioralAnomalyDao
import com.sentinelguard.data.database.entities.AppUsagePatternEntity
import com.sentinelguard.data.database.entities.BehavioralAnomalyEntity
import com.sentinelguard.security.collector.AppUsageTracker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Analyzes app usage patterns to detect anomalies.
 * 
 * Learns:
 * - Which apps are typically used at each hour
 * - Average session durations
 * - Weekday vs weekend patterns
 * 
 * Detects:
 * - Unusual app usage times (banking app at 3 AM)
 * - New apps being used
 * - Abnormal usage patterns
 */
@Singleton
class AppUsagePatternAnalyzer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appUsageTracker: AppUsageTracker,
    private val appUsagePatternDao: AppUsagePatternDao,
    private val anomalyDao: BehavioralAnomalyDao
) {
    
    companion object {
        // Sensitive app categories that warrant higher scrutiny
        private val SENSITIVE_APP_KEYWORDS = listOf(
            "bank", "pay", "wallet", "money", "finance", "credit",
            "password", "authenticator", "security", "vault"
        )
        
        // Hours considered unusual for sensitive apps (2 AM - 5 AM)
        private val UNUSUAL_HOURS = listOf(2, 3, 4, 5)
        
        // Minimum uses before considering it "normal"
        private const val MIN_USAGE_FOR_BASELINE = 3
    }
    
    /**
     * Record app usage and check for anomalies.
     * Returns risk points if anomaly detected.
     */
    suspend fun recordAppUsage(packageName: String, appName: String): Int {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val timestamp = System.currentTimeMillis()
        
        // Get existing pattern
        val existingPattern = appUsagePatternDao.getPattern(packageName, hour)
        
        var riskPoints = 0
        
        if (existingPattern != null) {
            // Update existing pattern
            appUsagePatternDao.incrementUsage(packageName, hour, timestamp)
        } else {
            // New pattern - check if this is unusual
            val isTypicalApp = isAppTypicalForHour(packageName, hour)
            val isSensitiveApp = isSensitiveApp(appName)
            val isUnusualHour = hour in UNUSUAL_HOURS
            
            // Detect anomalies
            if (isSensitiveApp && isUnusualHour && !isTypicalApp) {
                riskPoints = 15 // High risk - sensitive app at unusual time
                logAnomaly(
                    type = "APP_USAGE",
                    description = "Sensitive app '$appName' used at unusual hour ($hour:00)",
                    severity = 7,
                    riskPoints = riskPoints
                )
            } else if (!isTypicalApp && hasEstablishedBaseline()) {
                riskPoints = 5 // Low risk - new usage pattern
            }
            
            // Create new pattern entry
            appUsagePatternDao.insert(AppUsagePatternEntity(
                packageName = packageName,
                hourOfDay = hour,
                dayOfWeek = dayOfWeek,
                usageCount = 1,
                avgDurationMs = 0,
                lastUsed = timestamp
            ))
        }
        
        return riskPoints
    }
    
    /**
     * Check if app is typically used at this hour.
     */
    private suspend fun isAppTypicalForHour(packageName: String, hour: Int): Boolean {
        val patterns = appUsagePatternDao.getPatternsByApp(packageName)
        val patternForHour = patterns.find { it.hourOfDay == hour }
        return patternForHour != null && patternForHour.usageCount >= MIN_USAGE_FOR_BASELINE
    }
    
    /**
     * Check if app name suggests sensitive functionality.
     */
    private fun isSensitiveApp(appName: String): Boolean {
        val lowerName = appName.lowercase()
        return SENSITIVE_APP_KEYWORDS.any { lowerName.contains(it) }
    }
    
    /**
     * Check if we have enough data to establish baseline.
     */
    private suspend fun hasEstablishedBaseline(): Boolean {
        val typicalApps = appUsagePatternDao.getTypicalAppsForHour(12, MIN_USAGE_FOR_BASELINE)
        return typicalApps.size >= 5 // At least 5 apps with established patterns
    }
    
    /**
     * Get apps typically used at current hour.
     */
    suspend fun getTypicalAppsForCurrentHour(): List<String> {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return appUsagePatternDao.getTypicalAppsForHour(hour, MIN_USAGE_FOR_BASELINE)
    }
    
    /**
     * Analyze current app usage and detect anomalies.
     * Returns total risk points from app usage anomalies.
     */
    suspend fun analyzeCurrentUsage(): Int {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        
        // Get recent app usage from tracker
        val last15Min = System.currentTimeMillis() - (15 * 60 * 1000)
        val recentEvents = appUsageTracker.getUsageEvents(last15Min)
        
        var totalRiskPoints = 0
        
        for (event in recentEvents.filter { it.isAppOpened }) {
            val appName = event.appName ?: event.packageName
            val riskPoints = recordAppUsage(event.packageName, appName)
            totalRiskPoints += riskPoints
        }
        
        return totalRiskPoints.coerceAtMost(30) // Cap at 30 points
    }
    
    /**
     * Log a behavioral anomaly.
     */
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
    
    /**
     * Get learning progress for app usage patterns.
     */
    suspend fun getLearningProgress(): Float {
        val hoursWithPatterns = (0..23).count { hour ->
            val patterns = appUsagePatternDao.getPatternsByHour(hour)
            patterns.any { it.usageCount >= MIN_USAGE_FOR_BASELINE }
        }
        return (hoursWithPatterns / 24f).coerceIn(0f, 1f)
    }
}
