package com.sentinelguard.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sentinelguard.data.database.dao.SecuritySignalDao
import com.sentinelguard.data.local.preferences.SecurePreferences
import com.sentinelguard.data.preferences.SecurePreferencesManager
import com.sentinelguard.domain.repository.BaselineRepository
import com.sentinelguard.domain.repository.RiskScoreRepository
import com.sentinelguard.domain.repository.SecuritySignalRepository
import com.sentinelguard.security.baseline.BaselineEngine
import com.sentinelguard.security.collector.SignalCollector
import com.sentinelguard.security.risk.RiskScoringEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing security engine dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideFusedLocationClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideSecurePreferences(
        @ApplicationContext context: Context
    ): SecurePreferences {
        return SecurePreferences(context)
    }

    @Provides
    @Singleton
    fun provideSignalCollector(
        @ApplicationContext context: Context,
        securitySignalDao: SecuritySignalDao,
        securePrefsManager: SecurePreferencesManager,
        fusedLocationClient: FusedLocationProviderClient
    ): SignalCollector {
        return SignalCollector(context, securitySignalDao, securePrefsManager, fusedLocationClient)
    }

    @Provides
    @Singleton
    fun provideBaselineEngine(
        baselineRepository: BaselineRepository,
        signalRepository: SecuritySignalRepository,
        securePreferences: SecurePreferences
    ): BaselineEngine {
        return BaselineEngine(baselineRepository, signalRepository, securePreferences)
    }

    // RiskScoringEngine now uses @Inject constructor with all behavioral analyzers
    // It is automatically provided by Hilt - no manual @Provides needed
}
