package com.sentinelguard.data.preferences

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Secure preferences manager using EncryptedSharedPreferences.
 * 
 * Stores sensitive configuration like SMTP credentials.
 * All values are encrypted using Android Keystore-backed keys.
 */
class SecurePreferencesManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "sentinel_guard_secure_prefs"
        
        // Keys
        private const val KEY_SETUP_COMPLETE = "setup_complete"
        private const val KEY_LEARNING_START_DATE = "learning_start_date"
        private const val KEY_LAST_BOOT_TIME = "last_boot_time"
        private const val KEY_LAST_LOCATION_LAT = "last_location_lat"
        private const val KEY_LAST_LOCATION_LNG = "last_location_lng"
        private const val KEY_LAST_NETWORK_TYPE = "last_network_type"
        private const val KEY_LAST_SIM_STATE = "last_sim_state"
        private const val KEY_DATA_RETENTION_DAYS = "data_retention_days"
        
        // SMTP Config
        private const val KEY_SMTP_HOST = "smtp_host"
        private const val KEY_SMTP_PORT = "smtp_port"
        private const val KEY_SMTP_USERNAME = "smtp_username"
        private const val KEY_SMTP_PASSWORD = "smtp_password"
        private const val KEY_SMTP_USE_TLS = "smtp_use_tls"
        private const val KEY_ALERT_RECIPIENT = "alert_recipient"
        
        // Session
        private const val KEY_SESSION_ACTIVE = "session_active"
        private const val KEY_LAST_AUTH_TIME = "last_auth_time"
        private const val KEY_APP_LOCKED = "app_locked"
        private const val KEY_COOLDOWN_UNTIL = "cooldown_until"
        
        // Onboarding & UI
        private const val KEY_HAS_COMPLETED_ONBOARDING = "has_completed_onboarding"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_AUTO_LOCK_TIMEOUT = "auto_lock_timeout_minutes"
        private const val KEY_LAST_ACTIVITY_TIME = "last_activity_time"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // Setup state
    var isSetupComplete: Boolean
        get() = prefs.getBoolean(KEY_SETUP_COMPLETE, false)
        set(value) = prefs.edit().putBoolean(KEY_SETUP_COMPLETE, value).apply()

    var learningStartDate: Long
        get() = prefs.getLong(KEY_LEARNING_START_DATE, 0L)
        set(value) = prefs.edit().putLong(KEY_LEARNING_START_DATE, value).apply()

    // Device state tracking
    var lastBootTime: Long
        get() = prefs.getLong(KEY_LAST_BOOT_TIME, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_BOOT_TIME, value).apply()

    var lastLocationLat: Double
        get() = Double.fromBits(prefs.getLong(KEY_LAST_LOCATION_LAT, 0.0.toRawBits()))
        set(value) = prefs.edit().putLong(KEY_LAST_LOCATION_LAT, value.toRawBits()).apply()

    var lastLocationLng: Double
        get() = Double.fromBits(prefs.getLong(KEY_LAST_LOCATION_LNG, 0.0.toRawBits()))
        set(value) = prefs.edit().putLong(KEY_LAST_LOCATION_LNG, value.toRawBits()).apply()

    var lastNetworkType: String?
        get() = prefs.getString(KEY_LAST_NETWORK_TYPE, null)
        set(value) = prefs.edit().putString(KEY_LAST_NETWORK_TYPE, value).apply()

    var lastSimState: Int
        get() = prefs.getInt(KEY_LAST_SIM_STATE, -1)
        set(value) = prefs.edit().putInt(KEY_LAST_SIM_STATE, value).apply()

    // Data retention
    var dataRetentionDays: Int
        get() = prefs.getInt(KEY_DATA_RETENTION_DAYS, 30)
        set(value) = prefs.edit().putInt(KEY_DATA_RETENTION_DAYS, value).apply()

    // SMTP Configuration
    var smtpHost: String?
        get() = prefs.getString(KEY_SMTP_HOST, null)
        set(value) = prefs.edit().putString(KEY_SMTP_HOST, value).apply()

    var smtpPort: Int
        get() = prefs.getInt(KEY_SMTP_PORT, 587)
        set(value) = prefs.edit().putInt(KEY_SMTP_PORT, value).apply()

    var smtpUsername: String?
        get() = prefs.getString(KEY_SMTP_USERNAME, null)
        set(value) = prefs.edit().putString(KEY_SMTP_USERNAME, value).apply()

    var smtpPassword: String?
        get() = prefs.getString(KEY_SMTP_PASSWORD, null)
        set(value) = prefs.edit().putString(KEY_SMTP_PASSWORD, value).apply()

    var smtpUseTls: Boolean
        get() = prefs.getBoolean(KEY_SMTP_USE_TLS, true)
        set(value) = prefs.edit().putBoolean(KEY_SMTP_USE_TLS, value).apply()

    var alertRecipient: String?
        get() = prefs.getString(KEY_ALERT_RECIPIENT, null)
        set(value) = prefs.edit().putString(KEY_ALERT_RECIPIENT, value).apply()

    // Session management
    var isSessionActive: Boolean
        get() = prefs.getBoolean(KEY_SESSION_ACTIVE, false)
        set(value) = prefs.edit().putBoolean(KEY_SESSION_ACTIVE, value).apply()

    var lastAuthTime: Long
        get() = prefs.getLong(KEY_LAST_AUTH_TIME, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_AUTH_TIME, value).apply()

    var isAppLocked: Boolean
        get() = prefs.getBoolean(KEY_APP_LOCKED, false)
        set(value) = prefs.edit().putBoolean(KEY_APP_LOCKED, value).apply()

    var cooldownUntil: Long
        get() = prefs.getLong(KEY_COOLDOWN_UNTIL, 0L)
        set(value) = prefs.edit().putLong(KEY_COOLDOWN_UNTIL, value).apply()

    // ============ Onboarding & UI ============
    
    var hasCompletedOnboarding: Boolean
        get() = prefs.getBoolean(KEY_HAS_COMPLETED_ONBOARDING, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_COMPLETED_ONBOARDING, value).apply()
    
    /** Theme mode: \"dark\", \"light\", or \"system\" */
    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, "system") ?: "system"
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()
    
    /** Auto-lock timeout in minutes (0 = disabled) */
    var autoLockTimeoutMinutes: Int
        get() = prefs.getInt(KEY_AUTO_LOCK_TIMEOUT, 5)
        set(value) = prefs.edit().putInt(KEY_AUTO_LOCK_TIMEOUT, value).apply()
    
    /** Last activity timestamp for auto-lock */
    var lastActivityTime: Long
        get() = prefs.getLong(KEY_LAST_ACTIVITY_TIME, System.currentTimeMillis())
        set(value) = prefs.edit().putLong(KEY_LAST_ACTIVITY_TIME, value).apply()
    
    /** Update last activity time (call on user interaction) */
    fun updateLastActivity() {
        lastActivityTime = System.currentTimeMillis()
    }
    
    /** Check if auto-lock should trigger */
    fun shouldAutoLock(): Boolean {
        val timeout = autoLockTimeoutMinutes
        if (timeout <= 0) return false
        val elapsed = System.currentTimeMillis() - lastActivityTime
        return elapsed > timeout * 60 * 1000L
    }

    /**
     * Checks if SMTP is configured.
     */
    fun isSmtpConfigured(): Boolean {
        return !smtpHost.isNullOrBlank() && 
               !smtpUsername.isNullOrBlank() && 
               !smtpPassword.isNullOrBlank()
    }

    /**
     * Clears all preferences (for account deletion).
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }

    /**
     * Clears session data only.
     */
    fun clearSession() {
        prefs.edit()
            .putBoolean(KEY_SESSION_ACTIVE, false)
            .putLong(KEY_LAST_AUTH_TIME, 0L)
            .apply()
    }
}
