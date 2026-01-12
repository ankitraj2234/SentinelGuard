package com.sentinelguard.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.sentinelguard.data.database.AppDatabase
import com.sentinelguard.data.database.DatabasePassphraseManager
import com.sentinelguard.data.database.dao.*
import com.sentinelguard.data.local.preferences.SecurePreferences
import com.sentinelguard.data.repository.*
import com.sentinelguard.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.zetetic.database.sqlcipher.SQLiteDatabase
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import javax.inject.Singleton

/**
 * Hilt module providing database and data layer dependencies.
 * 
 * Database Security:
 * - SQLCipher encryption with AES-256
 * - Passphrase stored encrypted using Android Keystore (hardware-backed)
 * - Key is 256-bit AES-GCM protected
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // ============ Database ============

    @Provides
    @Singleton
    fun provideDatabasePassphraseManager(
        @ApplicationContext context: Context
    ): DatabasePassphraseManager {
        return DatabasePassphraseManager(context)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        passphraseManager: DatabasePassphraseManager
    ): AppDatabase {
        // Load SQLCipher native library
        System.loadLibrary("sqlcipher")
        
        // Get or create the encryption passphrase (backed by Android Keystore)
        val passphrase = passphraseManager.getOrCreatePassphrase()
        
        // Create SQLCipher SupportOpenHelperFactory with the passphrase
        val factory: SupportSQLiteOpenHelper.Factory = SupportOpenHelperFactory(passphrase)
        
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sentinel_guard.db"
        )
            .openHelperFactory(factory)  // Enable SQLCipher encryption
            .fallbackToDestructiveMigration()
            .build()
    }

    // ============ DAOs ============

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun provideSecuritySignalDao(database: AppDatabase): SecuritySignalDao = database.securitySignalDao()

    @Provides
    fun provideBehavioralBaselineDao(database: AppDatabase): BehavioralBaselineDao = database.behavioralBaselineDao()

    @Provides
    fun provideRiskScoreDao(database: AppDatabase): RiskScoreDao = database.riskScoreDao()

    @Provides
    fun provideIncidentDao(database: AppDatabase): IncidentDao = database.incidentDao()

    @Provides
    fun provideAlertHistoryDao(database: AppDatabase): AlertHistoryDao = database.alertHistoryDao()

    @Provides
    fun provideAlertQueueDao(database: AppDatabase): AlertQueueDao = database.alertQueueDao()

    // Behavioral Pattern DAOs
    @Provides
    fun provideAppUsagePatternDao(database: AppDatabase): AppUsagePatternDao = database.appUsagePatternDao()

    @Provides
    fun provideLocationClusterDao(database: AppDatabase): LocationClusterDao = database.locationClusterDao()

    @Provides
    fun provideKnownNetworkDao(database: AppDatabase): KnownNetworkDao = database.knownNetworkDao()

    @Provides
    fun provideUnlockPatternDao(database: AppDatabase): UnlockPatternDao = database.unlockPatternDao()

    @Provides
    fun provideBehavioralAnomalyDao(database: AppDatabase): BehavioralAnomalyDao = database.behavioralAnomalyDao()

    @Provides
    fun provideCellTowerDao(database: AppDatabase): CellTowerDao = database.cellTowerDao()

    // ============ Repositories ============

    @Provides
    @Singleton
    fun provideUserRepository(
        userDao: UserDao,
        securePreferences: SecurePreferences
    ): UserRepository {
        return UserRepositoryImpl(userDao, securePreferences)
    }

    @Provides
    @Singleton
    fun provideSecuritySignalRepository(
        dao: SecuritySignalDao
    ): SecuritySignalRepository {
        return SecuritySignalRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideBaselineRepository(
        dao: BehavioralBaselineDao
    ): BaselineRepository {
        return BaselineRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideRiskScoreRepository(
        dao: RiskScoreDao
    ): RiskScoreRepository {
        return RiskScoreRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideIncidentRepository(
        dao: IncidentDao
    ): IncidentRepository {
        return IncidentRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideAlertQueueRepository(
        dao: AlertQueueDao
    ): AlertQueueRepository {
        return AlertQueueRepositoryImpl(dao)
    }
}
