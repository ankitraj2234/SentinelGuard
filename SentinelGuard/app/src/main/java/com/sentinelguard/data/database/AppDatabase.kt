package com.sentinelguard.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sentinelguard.data.database.dao.*
import com.sentinelguard.data.database.entities.*

/**
 * Main Room database for SentinelGuard.
 * 
 * Contains all security-related data stored locally.
 */
@Database(
    entities = [
        UserEntity::class,
        SecuritySignalEntity::class,
        BehavioralBaselineEntity::class,
        RiskScoreEntity::class,
        IncidentEntity::class,
        AlertHistoryEntity::class,
        AlertQueueEntity::class,
        // Behavioral patterns
        AppUsagePatternEntity::class,
        LocationClusterEntity::class,
        KnownNetworkEntity::class,
        UnlockPatternEntity::class,
        BehavioralAnomalyEntity::class,
        // Cell tower intelligence
        CellTowerCacheEntity::class,
        CellTowerHistoryEntity::class,
        CellTowerIncidentEntity::class
    ],
    version = 4,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun securitySignalDao(): SecuritySignalDao
    abstract fun behavioralBaselineDao(): BehavioralBaselineDao
    abstract fun riskScoreDao(): RiskScoreDao
    abstract fun incidentDao(): IncidentDao
    abstract fun alertHistoryDao(): AlertHistoryDao
    abstract fun alertQueueDao(): AlertQueueDao
    
    // Behavioral pattern DAOs
    abstract fun appUsagePatternDao(): AppUsagePatternDao
    abstract fun locationClusterDao(): LocationClusterDao
    abstract fun knownNetworkDao(): KnownNetworkDao
    abstract fun unlockPatternDao(): UnlockPatternDao
    abstract fun behavioralAnomalyDao(): BehavioralAnomalyDao
    
    // Cell tower intelligence
    abstract fun cellTowerDao(): CellTowerDao
}
