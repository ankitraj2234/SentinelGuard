package com.sentinelguard.security.collector

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.sentinelguard.permission.PermissionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Represents a single app usage event
 */
data class AppUsageEvent(
    val packageName: String,
    val eventType: Int,
    val timestamp: Long,
    val appName: String? = null
) {
    companion object {
        const val TYPE_FOREGROUND = 1
        const val TYPE_BACKGROUND = 2
    }
    
    val isAppOpened: Boolean get() = eventType == TYPE_FOREGROUND
    val isAppClosed: Boolean get() = eventType == TYPE_BACKGROUND
}

/**
 * AppUsageTracker: Tracks app usage across the entire device.
 * 
 * Uses UsageStatsManager to detect:
 * - Which apps are opened
 * - When apps are opened/closed
 * - Duration of app usage
 * 
 * Requires PACKAGE_USAGE_STATS permission (granted via Settings).
 */
@Singleton
class AppUsageTracker @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionManager: PermissionManager
) {
    
    private val usageStatsManager: UsageStatsManager? by lazy {
        context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
    }
    
    /**
     * Get app usage events within a time range.
     */
    suspend fun getUsageEvents(
        startTime: Long,
        endTime: Long = System.currentTimeMillis()
    ): List<AppUsageEvent> = withContext(Dispatchers.IO) {
        if (!permissionManager.hasUsageStatsPermission()) {
            return@withContext emptyList()
        }
        
        val events = mutableListOf<AppUsageEvent>()
        val usageEvents = usageStatsManager?.queryEvents(startTime, endTime) ?: return@withContext emptyList()
        
        val event = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            
            // Only track foreground/background transitions
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                event.eventType == UsageEvents.Event.ACTIVITY_PAUSED) {
                
                events.add(AppUsageEvent(
                    packageName = event.packageName,
                    eventType = if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) 
                        AppUsageEvent.TYPE_FOREGROUND else AppUsageEvent.TYPE_BACKGROUND,
                    timestamp = event.timeStamp,
                    appName = getAppName(event.packageName)
                ))
            }
        }
        
        events
    }
    
    /**
     * Get the most recently used apps.
     */
    suspend fun getRecentApps(limit: Int = 10): List<String> = withContext(Dispatchers.IO) {
        if (!permissionManager.hasUsageStatsPermission()) {
            return@withContext emptyList()
        }
        
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (24 * 60 * 60 * 1000) // Last 24 hours
        
        val events = getUsageEvents(startTime, endTime)
        
        // Get unique packages, most recent first
        events
            .filter { it.isAppOpened }
            .sortedByDescending { it.timestamp }
            .map { it.packageName }
            .distinct()
            .take(limit)
    }
    
    /**
     * Get app usage summary for a time period.
     */
    suspend fun getUsageSummary(
        startTime: Long,
        endTime: Long = System.currentTimeMillis()
    ): Map<String, Long> = withContext(Dispatchers.IO) {
        if (!permissionManager.hasUsageStatsPermission()) {
            return@withContext emptyMap()
        }
        
        val usageStats = usageStatsManager?.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        ) ?: return@withContext emptyMap()
        
        usageStats
            .filter { it.totalTimeInForeground > 0 }
            .associate { it.packageName to it.totalTimeInForeground }
    }
    
    /**
     * Get app session durations.
     */
    suspend fun getAppSessions(
        packageName: String? = null,
        startTime: Long,
        endTime: Long = System.currentTimeMillis()
    ): List<AppSession> = withContext(Dispatchers.IO) {
        val events = getUsageEvents(startTime, endTime)
        val filteredEvents = if (packageName != null) {
            events.filter { it.packageName == packageName }
        } else events
        
        val sessions = mutableListOf<AppSession>()
        val openTimes = mutableMapOf<String, Long>()
        
        for (event in filteredEvents) {
            if (event.isAppOpened) {
                openTimes[event.packageName] = event.timestamp
            } else if (event.isAppClosed) {
                val openTime = openTimes.remove(event.packageName)
                if (openTime != null) {
                    sessions.add(AppSession(
                        packageName = event.packageName,
                        startTime = openTime,
                        endTime = event.timestamp,
                        duration = event.timestamp - openTime
                    ))
                }
            }
        }
        
        sessions.sortedByDescending { it.startTime }
    }
    
    /**
     * Check if an app is currently in foreground.
     */
    suspend fun isAppInForeground(packageName: String): Boolean = withContext(Dispatchers.IO) {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (60 * 1000) // Last minute
        
        val events = getUsageEvents(startTime, endTime)
            .filter { it.packageName == packageName }
            .sortedByDescending { it.timestamp }
        
        events.firstOrNull()?.isAppOpened == true
    }
    
    private fun getAppName(packageName: String): String? {
        return try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Represents a single app session (from open to close)
 */
data class AppSession(
    val packageName: String,
    val startTime: Long,
    val endTime: Long,
    val duration: Long
)
