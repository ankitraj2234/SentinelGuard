package com.sentinelguard.email

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Initializes email credentials on first app launch.
 * 
 * This stores the provided credentials securely using Android Keystore.
 * Called during app initialization.
 */
@Singleton
class EmailCredentialInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val emailService: EmailService
) {
    companion object {
        // These are app constants - they get encrypted before storage
        private const val SENDER_EMAIL = "ankuclg11122@gmail.com"
        private const val APP_PASSWORD = "aphj lwym pzgi qxjh"
    }
    
    /**
     * Initializes email credentials if not already configured.
     * Must be called during app startup.
     */
    fun initializeIfNeeded() {
        if (!emailService.hasCredentials()) {
            emailService.storeCredentials(SENDER_EMAIL, APP_PASSWORD)
        }
    }
}
