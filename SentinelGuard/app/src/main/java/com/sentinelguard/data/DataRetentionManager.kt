package com.sentinelguard.data

import com.sentinelguard.data.local.database.dao.AlertQueueDao
import com.sentinelguard.data.local.database.dao.SecuritySignalDao
import com.sentinelguard.data.local.preferences.SecurePreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * DataRetentionManager: Automatic Cleanup of Old Data
 * 
 * WHY THIS EXISTS:
 * Security signals accumulate quickly. To prevent database bloat and
 * maintain performance, old data is automatically purged based on
 * user-configurable retention period (default: 30 days).
 * 
 * WHAT GETS DELETED:
 * - Security signals older than retention period
 * - Sent alerts older than retention period
 * 
 * WHAT IS PRESERVED:
 * - Incidents (forensic timeline) - kept until user manually deletes
 * - Behavioral baselines - always kept for anomaly detection
 */
class DataRetentionManager(
    private val signalDao: SecuritySignalDao,
    private val alertQueueDao: AlertQueueDao,
    private val securePreferences: SecurePreferences
) {

    companion object {
        /** Default retention period in days */
        const val DEFAULT_RETENTION_DAYS = 30
        
        /** Minimum allowed retention period */
        const val MIN_RETENTION_DAYS = 7
        
        /** Maximum allowed retention period */
        const val MAX_RETENTION_DAYS = 365
    }

    /**
     * Runs data cleanup based on retention settings.
     * 
     * Should be called:
     * - On app startup
     * - Daily via WorkManager (if implemented)
     * 
     * @return Number of records deleted
     */
    suspend fun performCleanup(): CleanupResult = withContext(Dispatchers.IO) {
        val retentionDays = securePreferences.dataRetentionDays
            .coerceIn(MIN_RETENTION_DAYS, MAX_RETENTION_DAYS)
        
        val cutoffTime = System.currentTimeMillis() - (retentionDays * 24 * 60 * 60 * 1000L)
        
        val signalsDeleted = signalDao.deleteOlderThan(cutoffTime)
        val alertsDeleted = alertQueueDao.deleteOlderThan(cutoffTime)
        
        CleanupResult(
            signalsDeleted = signalsDeleted,
            alertsDeleted = alertsDeleted,
            cutoffTimestamp = cutoffTime,
            retentionDays = retentionDays
        )
    }

    /**
     * Gets current retention period in days.
     */
    fun getRetentionDays(): Int {
        return securePreferences.dataRetentionDays
    }

    /**
     * Sets retention period.
     * 
     * @param days Retention period (clamped to MIN-MAX range)
     */
    fun setRetentionDays(days: Int) {
        securePreferences.dataRetentionDays = days.coerceIn(MIN_RETENTION_DAYS, MAX_RETENTION_DAYS)
    }

    /**
     * Calculates approximate storage used by signals.
     */
    suspend fun getSignalCount(): Int = withContext(Dispatchers.IO) {
        signalDao.count()
    }
}

/**
 * Result of a cleanup operation.
 */
data class CleanupResult(
    val signalsDeleted: Int,
    val alertsDeleted: Int,
    val cutoffTimestamp: Long,
    val retentionDays: Int
) {
    val totalDeleted: Int get() = signalsDeleted + alertsDeleted
}
