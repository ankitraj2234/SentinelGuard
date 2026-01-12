package com.sentinelguard

import android.app.Application
import com.sentinelguard.crash.CrashHandler
import com.sentinelguard.email.EmailCredentialInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * SentinelGuard Application class.
 * 
 * This is the main entry point for the application.
 * Hilt uses this class for dependency injection setup.
 * 
 * Security Note:
 * - No third-party analytics SDKs
 * - Crash reports saved locally to Downloads folder
 * - No cloud-based services
 * - All processing happens on-device
 */
@HiltAndroidApp
class SentinelGuardApp : Application() {

    @Inject
    lateinit var emailCredentialInitializer: EmailCredentialInitializer
    
    @Inject
    lateinit var crashHandler: CrashHandler

    override fun onCreate() {
        super.onCreate()
        
        // Initialize SQLCipher library
        System.loadLibrary("sqlcipher")
        
        // Initialize crash handler (saves crash logs to Downloads folder)
        crashHandler.initialize()
        
        // Initialize email credentials (encrypted storage)
        emailCredentialInitializer.initializeIfNeeded()
    }
}
