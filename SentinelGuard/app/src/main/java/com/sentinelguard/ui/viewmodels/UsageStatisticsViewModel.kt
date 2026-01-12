package com.sentinelguard.ui.viewmodels

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinelguard.permission.PermissionManager
import com.sentinelguard.security.collector.AppUsageTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * App usage info with icon for display
 */
data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val usageTimeMs: Long,
    val icon: Drawable? = null
) {
    val formattedDuration: String
        get() {
            val hours = usageTimeMs / (1000 * 60 * 60)
            val minutes = (usageTimeMs % (1000 * 60 * 60)) / (1000 * 60)
            return when {
                hours > 0 -> "${hours}h ${minutes}m"
                minutes > 0 -> "${minutes}m"
                else -> "<1m"
            }
        }
    
    val usagePercent: Float
        get() = (usageTimeMs.toFloat() / (24 * 60 * 60 * 1000)).coerceIn(0f, 1f)
}

/**
 * Hourly usage data for timeline chart
 */
data class HourlyUsage(
    val hour: Int, // 0-23
    val usageMs: Long
) {
    val percentage: Float
        get() = (usageMs.toFloat() / (60 * 60 * 1000)).coerceIn(0f, 1f) // Max 1 hour per hour
}

/**
 * UI State for Usage Statistics Screen
 */
data class UsageStatisticsUiState(
    val isLoading: Boolean = true,
    val hasPermission: Boolean = false,
    val error: String? = null,
    
    // Header stats
    val totalScreenTimeToday: Long = 0,
    val appsUsedToday: Int = 0,
    val peakHour: Int? = null, // 0-23
    
    // Top apps
    val topApps: List<AppUsageInfo> = emptyList(),
    
    // Hourly breakdown
    val hourlyUsage: List<HourlyUsage> = emptyList(),
    
    // Weekly comparison
    val weeklyAverageMs: Long = 0,
    val todayVsAveragePercent: Int = 0 // Positive or negative %
) {
    val formattedScreenTime: String
        get() {
            val hours = totalScreenTimeToday / (1000 * 60 * 60)
            val minutes = (totalScreenTimeToday % (1000 * 60 * 60)) / (1000 * 60)
            return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        }
    
    val formattedWeeklyAverage: String
        get() {
            val hours = weeklyAverageMs / (1000 * 60 * 60)
            val minutes = (weeklyAverageMs % (1000 * 60 * 60)) / (1000 * 60)
            return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        }
    
    val peakHourFormatted: String
        get() = peakHour?.let {
            when {
                it == 0 -> "12 AM"
                it < 12 -> "$it AM"
                it == 12 -> "12 PM"
                else -> "${it - 12} PM"
            }
        } ?: "--"
}

@HiltViewModel
class UsageStatisticsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appUsageTracker: AppUsageTracker,
    private val permissionManager: PermissionManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(UsageStatisticsUiState())
    val uiState: StateFlow<UsageStatisticsUiState> = _uiState.asStateFlow()
    
    init {
        loadUsageStatistics()
    }
    
    fun loadUsageStatistics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Check permission
            if (!permissionManager.hasUsageStatsPermission()) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        hasPermission = false,
                        error = "Usage access permission required"
                    ) 
                }
                return@launch
            }
            
            try {
                val now = System.currentTimeMillis()
                val calendar = Calendar.getInstance()
                
                // Start of today
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfToday = calendar.timeInMillis
                
                // Get today's usage
                val todayUsage = appUsageTracker.getUsageSummary(startOfToday, now)
                val totalToday = todayUsage.values.sum()
                val appsUsed = todayUsage.filter { it.value > 60000 }.size // Apps used >1 min
                
                // Get top apps with icons
                val topApps = todayUsage
                    .filter { it.value > 30000 } // >30 seconds
                    .entries
                    .sortedByDescending { it.value }
                    .take(10)
                    .map { (pkg, duration) ->
                        val appName = getAppName(pkg)
                        val icon = getAppIcon(pkg)
                        AppUsageInfo(pkg, appName, duration, icon)
                    }
                
                // Get hourly breakdown
                val hourlyUsage = calculateHourlyUsage(startOfToday, now)
                val peakHour = hourlyUsage.maxByOrNull { it.usageMs }?.hour
                
                // Get weekly average
                val weekStart = startOfToday - (7 * 24 * 60 * 60 * 1000)
                val weeklyUsage = appUsageTracker.getUsageSummary(weekStart, startOfToday)
                val weeklyTotal = weeklyUsage.values.sum()
                val weeklyAverage = weeklyTotal / 7
                
                // Calculate comparison
                val comparisonPercent = if (weeklyAverage > 0) {
                    ((totalToday - weeklyAverage) * 100 / weeklyAverage).toInt()
                } else 0
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hasPermission = true,
                        totalScreenTimeToday = totalToday,
                        appsUsedToday = appsUsed,
                        peakHour = peakHour,
                        topApps = topApps,
                        hourlyUsage = hourlyUsage,
                        weeklyAverageMs = weeklyAverage,
                        todayVsAveragePercent = comparisonPercent
                    )
                }
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to load usage data: ${e.message}")
                }
            }
        }
    }
    
    private suspend fun calculateHourlyUsage(startOfDay: Long, endTime: Long): List<HourlyUsage> {
        val hourlyData = mutableListOf<HourlyUsage>()
        val sessions = appUsageTracker.getAppSessions(startTime = startOfDay, endTime = endTime)
        
        // Initialize all hours
        val hourUsage = LongArray(24) { 0L }
        
        // Distribute session time to hours
        for (session in sessions) {
            val startCal = Calendar.getInstance().apply { timeInMillis = session.startTime }
            val endCal = Calendar.getInstance().apply { timeInMillis = session.endTime }
            
            val startHour = startCal.get(Calendar.HOUR_OF_DAY)
            val endHour = endCal.get(Calendar.HOUR_OF_DAY)
            
            if (startHour == endHour) {
                hourUsage[startHour] += session.duration
            } else {
                // Session spans multiple hours - approximate
                val avgDuration = session.duration / (endHour - startHour + 1).coerceAtLeast(1)
                for (h in startHour..endHour.coerceAtMost(23)) {
                    hourUsage[h] += avgDuration
                }
            }
        }
        
        for (hour in 0..23) {
            hourlyData.add(HourlyUsage(hour, hourUsage[hour]))
        }
        
        return hourlyData
    }
    
    private fun getAppName(packageName: String): String {
        return try {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName.substringAfterLast('.')
        }
    }
    
    private fun getAppIcon(packageName: String): Drawable? {
        return try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            null
        }
    }
}
