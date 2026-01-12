package com.sentinelguard.di

import android.content.Context
import com.sentinelguard.alert.EmailAlertService
import com.sentinelguard.data.database.dao.AlertHistoryDao
import com.sentinelguard.domain.repository.IncidentRepository
import com.sentinelguard.domain.repository.SecuritySignalRepository
import com.sentinelguard.incident.TimelineBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlertModule {

    @Provides
    @Singleton
    fun provideEmailAlertService(
        @ApplicationContext context: Context,
        alertHistoryDao: AlertHistoryDao
    ): EmailAlertService {
        return EmailAlertService(context, alertHistoryDao)
    }

    @Provides
    @Singleton
    fun provideTimelineBuilder(
        signalRepository: SecuritySignalRepository,
        incidentRepository: IncidentRepository
    ): TimelineBuilder {
        return TimelineBuilder(signalRepository, incidentRepository)
    }
}
