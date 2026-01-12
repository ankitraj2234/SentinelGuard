package com.sentinelguard.di

import com.sentinelguard.alert.EmailAlertService
import com.sentinelguard.data.local.preferences.SecurePreferences
import com.sentinelguard.domain.repository.IncidentRepository
import com.sentinelguard.security.response.AppLockManager
import com.sentinelguard.security.response.ResponseEngine
import com.sentinelguard.security.response.SessionManager
import com.sentinelguard.security.risk.RiskScoringEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * ResponseModule: Hilt DI for Response Engine
 */
@Module
@InstallIn(SingletonComponent::class)
object ResponseModule {

    @Provides
    @Singleton
    fun provideAppLockManager(securePreferences: SecurePreferences): AppLockManager {
        return AppLockManager(securePreferences)
    }

    @Provides
    @Singleton
    fun provideSessionManager(securePreferences: SecurePreferences): SessionManager {
        return SessionManager(securePreferences)
    }

    @Provides
    @Singleton
    fun provideResponseEngine(
        riskScoringEngine: RiskScoringEngine,
        appLockManager: AppLockManager,
        sessionManager: SessionManager,
        incidentRepository: IncidentRepository,
        emailAlertService: EmailAlertService
    ): ResponseEngine {
        return ResponseEngine(
            riskScoringEngine = riskScoringEngine,
            appLockManager = appLockManager,
            sessionManager = sessionManager,
            incidentRepository = incidentRepository,
            emailAlertService = emailAlertService
        )
    }
}
