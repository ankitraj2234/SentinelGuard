package com.sentinelguard.di

import android.content.Context
import com.sentinelguard.auth.AuthRepository
import com.sentinelguard.auth.BiometricAuthManager
import com.sentinelguard.data.database.dao.UserDao
import com.sentinelguard.data.preferences.SecurePreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing authentication dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideSecurePreferencesManager(
        @ApplicationContext context: Context
    ): SecurePreferencesManager {
        return SecurePreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        userDao: UserDao,
        securePreferencesManager: SecurePreferencesManager
    ): AuthRepository {
        return AuthRepository(userDao, securePreferencesManager)
    }

    @Provides
    @Singleton
    fun provideBiometricAuthManager(
        @ApplicationContext context: Context
    ): BiometricAuthManager {
        return BiometricAuthManager(context)
    }
}
