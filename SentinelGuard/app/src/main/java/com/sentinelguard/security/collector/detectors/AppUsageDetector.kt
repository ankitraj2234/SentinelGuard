package com.sentinelguard.security.collector.detectors

/**
 * Tracks app usage patterns.
 * 
 * Collected metrics:
 * - Session start/end times
 * - Session duration
 * - Time-of-day patterns
 */
class AppUsageDetector {

    private var sessionStartTime: Long? = null

    /**
     * Marks the start of an app session.
     */
    fun startSession(): Long {
        val now = System.currentTimeMillis()
        sessionStartTime = now
        return now
    }

    /**
     * Marks the end of an app session and returns duration.
     */
    fun endSession(): Long? {
        val startTime = sessionStartTime ?: return null
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        sessionStartTime = null
        return duration
    }

    /**
     * Gets the current session duration.
     */
    fun getCurrentSessionDuration(): Long? {
        val startTime = sessionStartTime ?: return null
        return System.currentTimeMillis() - startTime
    }

    /**
     * Returns true if a session is currently active.
     */
    fun isSessionActive(): Boolean {
        return sessionStartTime != null
    }

    /**
     * Gets the hour of day (0-23) from a timestamp.
     */
    fun getHourOfDay(timestamp: Long): Int {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return calendar.get(java.util.Calendar.HOUR_OF_DAY)
    }

    /**
     * Gets the day of week (1-7, Sunday=1) from a timestamp.
     */
    fun getDayOfWeek(timestamp: Long): Int {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return calendar.get(java.util.Calendar.DAY_OF_WEEK)
    }
}
