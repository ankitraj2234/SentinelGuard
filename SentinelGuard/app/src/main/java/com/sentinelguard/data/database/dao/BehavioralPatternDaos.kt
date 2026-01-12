package com.sentinelguard.data.database.dao

import androidx.room.*
import com.sentinelguard.data.database.entities.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO for app usage patterns
 */
@Dao
interface AppUsagePatternDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pattern: AppUsagePatternEntity)
    
    @Query("SELECT * FROM app_usage_patterns WHERE packageName = :packageName AND hourOfDay = :hour")
    suspend fun getPattern(packageName: String, hour: Int): AppUsagePatternEntity?
    
    @Query("SELECT * FROM app_usage_patterns WHERE hourOfDay = :hour ORDER BY usageCount DESC")
    suspend fun getPatternsByHour(hour: Int): List<AppUsagePatternEntity>
    
    @Query("SELECT * FROM app_usage_patterns WHERE packageName = :packageName ORDER BY hourOfDay")
    suspend fun getPatternsByApp(packageName: String): List<AppUsagePatternEntity>
    
    @Query("SELECT DISTINCT packageName FROM app_usage_patterns WHERE hourOfDay = :hour AND usageCount >= :minCount")
    suspend fun getTypicalAppsForHour(hour: Int, minCount: Int = 3): List<String>
    
    @Query("UPDATE app_usage_patterns SET usageCount = usageCount + 1, lastUsed = :timestamp WHERE packageName = :packageName AND hourOfDay = :hour")
    suspend fun incrementUsage(packageName: String, hour: Int, timestamp: Long)
    
    @Query("DELETE FROM app_usage_patterns WHERE lastUsed < :before")
    suspend fun deleteOldPatterns(before: Long)
}

/**
 * DAO for location clusters
 */
@Dao
interface LocationClusterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cluster: LocationClusterEntity): Long
    
    @Update
    suspend fun update(cluster: LocationClusterEntity)
    
    @Query("SELECT * FROM location_clusters WHERE isTrusted = 1")
    suspend fun getTrustedClusters(): List<LocationClusterEntity>
    
    @Query("SELECT * FROM location_clusters ORDER BY visitCount DESC")
    suspend fun getAllClusters(): List<LocationClusterEntity>
    
    @Query("SELECT * FROM location_clusters ORDER BY visitCount DESC")
    fun observeAllClusters(): Flow<List<LocationClusterEntity>>
    
    @Query("SELECT * FROM location_clusters WHERE id = :id")
    suspend fun getClusterById(id: Long): LocationClusterEntity?
    
    @Query("UPDATE location_clusters SET visitCount = visitCount + 1, lastVisited = :timestamp WHERE id = :id")
    suspend fun incrementVisit(id: Long, timestamp: Long)
    
    @Query("UPDATE location_clusters SET totalTimeSpentMs = totalTimeSpentMs + :duration WHERE id = :id")
    suspend fun addTimeSpent(id: Long, duration: Long)
    
    @Query("UPDATE location_clusters SET label = :label WHERE id = :id")
    suspend fun setLabel(id: Long, label: String)
    
    @Delete
    suspend fun delete(cluster: LocationClusterEntity)
}

/**
 * DAO for known networks
 */
@Dao
interface KnownNetworkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(network: KnownNetworkEntity)
    
    @Query("SELECT * FROM known_networks WHERE ssid = :ssid")
    suspend fun getNetwork(ssid: String): KnownNetworkEntity?
    
    @Query("SELECT * FROM known_networks WHERE isTrusted = 1")
    suspend fun getTrustedNetworks(): List<KnownNetworkEntity>
    
    @Query("SELECT * FROM known_networks ORDER BY connectionCount DESC")
    suspend fun getAllNetworks(): List<KnownNetworkEntity>
    
    @Query("SELECT EXISTS(SELECT 1 FROM known_networks WHERE ssid = :ssid)")
    suspend fun isKnownNetwork(ssid: String): Boolean
    
    @Query("UPDATE known_networks SET connectionCount = connectionCount + 1, lastConnected = :timestamp WHERE ssid = :ssid")
    suspend fun incrementConnection(ssid: String, timestamp: Long)
    
    @Query("UPDATE known_networks SET isTrusted = :trusted WHERE ssid = :ssid")
    suspend fun setTrusted(ssid: String, trusted: Boolean)
    
    @Delete
    suspend fun delete(network: KnownNetworkEntity)
}

/**
 * DAO for unlock patterns
 */
@Dao
interface UnlockPatternDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pattern: UnlockPatternEntity)
    
    @Query("SELECT * FROM unlock_patterns WHERE hourOfDay = :hour AND dayOfWeek = :dayOfWeek")
    suspend fun getPattern(hour: Int, dayOfWeek: Int): UnlockPatternEntity?
    
    @Query("SELECT * FROM unlock_patterns WHERE hourOfDay = :hour")
    suspend fun getPatternsByHour(hour: Int): List<UnlockPatternEntity>
    
    @Query("SELECT AVG(unlockCount) FROM unlock_patterns WHERE hourOfDay = :hour")
    suspend fun getAverageUnlocksForHour(hour: Int): Float?
    
    @Query("SELECT SUM(unlockCount) FROM unlock_patterns")
    suspend fun getTotalUnlocks(): Int?
    
    @Query("UPDATE unlock_patterns SET unlockCount = unlockCount + 1, lastUpdated = :timestamp WHERE hourOfDay = :hour AND dayOfWeek = :dayOfWeek")
    suspend fun incrementUnlock(hour: Int, dayOfWeek: Int, timestamp: Long)
    
    @Query("UPDATE unlock_patterns SET failedAttempts = failedAttempts + 1 WHERE hourOfDay = :hour AND dayOfWeek = :dayOfWeek")
    suspend fun incrementFailedAttempt(hour: Int, dayOfWeek: Int)
}

/**
 * DAO for behavioral anomalies
 */
@Dao
interface BehavioralAnomalyDao {
    @Insert
    suspend fun insert(anomaly: BehavioralAnomalyEntity): Long
    
    @Query("SELECT * FROM behavioral_anomalies WHERE resolved = 0 ORDER BY timestamp DESC")
    suspend fun getUnresolvedAnomalies(): List<BehavioralAnomalyEntity>
    
    @Query("SELECT * FROM behavioral_anomalies WHERE timestamp > :since ORDER BY timestamp DESC")
    suspend fun getRecentAnomalies(since: Long): List<BehavioralAnomalyEntity>
    
    @Query("SELECT SUM(riskPoints) FROM behavioral_anomalies WHERE resolved = 0 AND timestamp > :since")
    suspend fun getTotalRiskPoints(since: Long): Int?
    
    @Query("UPDATE behavioral_anomalies SET resolved = 1 WHERE id = :id")
    suspend fun resolveAnomaly(id: Long)
    
    @Query("UPDATE behavioral_anomalies SET resolved = 1 WHERE timestamp < :before")
    suspend fun resolveOldAnomalies(before: Long)
    
    @Query("DELETE FROM behavioral_anomalies WHERE timestamp < :before")
    suspend fun deleteOldAnomalies(before: Long)
}
