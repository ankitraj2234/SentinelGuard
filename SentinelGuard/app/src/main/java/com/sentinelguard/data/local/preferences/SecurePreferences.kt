package com.sentinelguard.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * SecurePreferences: Encrypted SharedPreferences Wrapper
 * 
 * WHY THIS EXISTS:
 * Some data doesn't belong in the database but must persist securely:
 * - SMTP credentials for email alerts
 * - App state flags (is setup complete?)
 * - Session tracking
 * 
 * Uses EncryptedSharedPreferences with MasterKey stored in Keystore.
 */
class SecurePreferences(context: Context) {

    companion object {
        private const val PREFS_NAME = "sentinel_secure_prefs"
        
        // Keys
        private const val KEY_SETUP_COMPLETE = "setup_complete"
        private const val KEY_SESSION_ACTIVE = "session_active"
        private const val KEY_CURRENT_USER_ID = "current_user_id"
        private const val KEY_APP_LOCKED = "app_locked"
        private const val KEY_COOLDOWN_UNTIL = "cooldown_until"
        
        // SMTP Configuration
        private const val KEY_SMTP_HOST = "smtp_host"
        private const val KEY_SMTP_PORT = "smtp_port"
        private const val KEY_SMTP_USERNAME = "smtp_username"
        private const val KEY_SMTP_PASSWORD = "smtp_password"
        private const val KEY_SMTP_USE_TLS = "smtp_use_tls"
        private const val KEY_ALERT_RECIPIENT = "alert_recipient"
        
        // Tracking
        private const val KEY_LAST_NETWORK_TYPE = "last_network_type"
        private const val KEY_LAST_BOOT_TIME = "last_boot_time"
        private const val KEY_LAST_LOCATION_LAT = "last_location_lat"
        private const val KEY_LAST_LOCATION_LNG = "last_location_lng"
        
        // Settings
        private const val KEY_DATA_RETENTION_DAYS = "data_retention_days"
        private const val DEFAULT_RETENTION_DAYS = 30
        
        // Recovery
        private const val KEY_RECOVERY_CODE = "recovery_code"
        private const val KEY_RECOVERY_CODE_EXPIRY = "recovery_code_expiry"
        
        // Intruder Capture
        private const val KEY_INTRUDER_CAPTURE_ENABLED = "intruder_capture_enabled"
        private const val KEY_ALERT_EMAIL = "alert_email"
        
        // Cell Tower API Keys
        private const val KEY_GOOGLE_GEOLOCATION_API_KEY = "google_geolocation_api_key"
    }

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // ============ Setup & Session ============
    
    var isSetupComplete: Boolean
        get() = prefs.getBoolean(KEY_SETUP_COMPLETE, false)
        set(value) = prefs.edit().putBoolean(KEY_SETUP_COMPLETE, value).apply()

    var isSessionActive: Boolean
        get() = prefs.getBoolean(KEY_SESSION_ACTIVE, false)
        set(value) = prefs.edit().putBoolean(KEY_SESSION_ACTIVE, value).apply()

    var currentUserId: String?
        get() = prefs.getString(KEY_CURRENT_USER_ID, null)
        set(value) = prefs.edit().putString(KEY_CURRENT_USER_ID, value).apply()

    var isAppLocked: Boolean
        get() = prefs.getBoolean(KEY_APP_LOCKED, false)
        set(value) = prefs.edit().putBoolean(KEY_APP_LOCKED, value).apply()

    var cooldownUntil: Long
        get() = prefs.getLong(KEY_COOLDOWN_UNTIL, 0)
        set(value) = prefs.edit().putLong(KEY_COOLDOWN_UNTIL, value).apply()

    // ============ SMTP Configuration ============
    
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

    fun isSmtpConfigured(): Boolean {
        return !smtpHost.isNullOrBlank() && !smtpUsername.isNullOrBlank() && !smtpPassword.isNullOrBlank()
    }

    // ============ State Tracking ============
    
    var lastNetworkType: String?
        get() = prefs.getString(KEY_LAST_NETWORK_TYPE, null)
        set(value) = prefs.edit().putString(KEY_LAST_NETWORK_TYPE, value).apply()

    var lastBootTime: Long
        get() = prefs.getLong(KEY_LAST_BOOT_TIME, 0)
        set(value) = prefs.edit().putLong(KEY_LAST_BOOT_TIME, value).apply()

    var lastLocationLat: Double
        get() = Double.fromBits(prefs.getLong(KEY_LAST_LOCATION_LAT, 0.0.toRawBits()))
        set(value) = prefs.edit().putLong(KEY_LAST_LOCATION_LAT, value.toRawBits()).apply()

    var lastLocationLng: Double
        get() = Double.fromBits(prefs.getLong(KEY_LAST_LOCATION_LNG, 0.0.toRawBits()))
        set(value) = prefs.edit().putLong(KEY_LAST_LOCATION_LNG, value.toRawBits()).apply()

    // ============ Settings ============
    
    var dataRetentionDays: Int
        get() = prefs.getInt(KEY_DATA_RETENTION_DAYS, DEFAULT_RETENTION_DAYS)
        set(value) = prefs.edit().putInt(KEY_DATA_RETENTION_DAYS, value).apply()

    var cooldownMinutes: Int
        get() = prefs.getInt("cooldown_minutes", 30)
        set(value) = prefs.edit().putInt("cooldown_minutes", value).apply()

    var learningPeriodDays: Int
        get() = prefs.getInt("learning_period_days", 7)
        set(value) = prefs.edit().putInt("learning_period_days", value).apply()

    var learningStartDate: Long
        get() = prefs.getLong("learning_start_date", 0)
        set(value) = prefs.edit().putLong("learning_start_date", value).apply()

    var lastAuthTime: Long
        get() = prefs.getLong("last_auth_time", 0)
        set(value) = prefs.edit().putLong("last_auth_time", value).apply()

    // ============ Cell Tower APIs ============
    
    /**
     * Google Geolocation API key for cell tower lookup fallback
     * Get from: https://console.cloud.google.com/apis/credentials
     * Enable Geolocation API in your project
     */
    var googleGeolocationApiKey: String?
        get() = prefs.getString(KEY_GOOGLE_GEOLOCATION_API_KEY, null)
        set(value) = prefs.edit().putString(KEY_GOOGLE_GEOLOCATION_API_KEY, value).apply()

    // ============ Recovery ============
    
    var recoveryCode: String?
        get() = prefs.getString(KEY_RECOVERY_CODE, null)
        set(value) = prefs.edit().putString(KEY_RECOVERY_CODE, value).apply()

    var recoveryCodeExpiry: Long
        get() = prefs.getLong(KEY_RECOVERY_CODE_EXPIRY, 0)
        set(value) = prefs.edit().putLong(KEY_RECOVERY_CODE_EXPIRY, value).apply()

    // ============ Scan History ============
    
    var lastScanTime: Long
        get() = prefs.getLong("last_scan_time", 0)
        set(value) = prefs.edit().putLong("last_scan_time", value).apply()

    var lastScanThreatsFound: Int
        get() = prefs.getInt("last_scan_threats", 0)
        set(value) = prefs.edit().putInt("last_scan_threats", value).apply()

    var lastScanAppsScanned: Int
        get() = prefs.getInt("last_scan_apps", 0)
        set(value) = prefs.edit().putInt("last_scan_apps", value).apply()

    // ============ Trust Overrides ============
    
    var rootTrustedUntil: Long
        get() = prefs.getLong("root_trusted_until", 0)
        set(value) = prefs.edit().putLong("root_trusted_until", value).apply()

    var simChangeTrustedUntil: Long
        get() = prefs.getLong("sim_change_trusted_until", 0)
        set(value) = prefs.edit().putLong("sim_change_trusted_until", value).apply()

    // ============ Onboarding & App State ============
    
    var hasCompletedOnboarding: Boolean
        get() = prefs.getBoolean("has_completed_onboarding", false)
        set(value) = prefs.edit().putBoolean("has_completed_onboarding", value).apply()
    
    // ============ Theme Settings ============
    
    /** Theme mode: "dark", "light", or "system" */
    var themeMode: String
        get() = prefs.getString("theme_mode", "dark") ?: "dark"
        set(value) = prefs.edit().putString("theme_mode", value).apply()
    
    // ============ Auto-Lock Settings ============
    
    /** Auto-lock timeout in minutes (0 = disabled) */
    var autoLockTimeoutMinutes: Int
        get() = prefs.getInt("auto_lock_timeout_minutes", 5)
        set(value) = prefs.edit().putInt("auto_lock_timeout_minutes", value).apply()
    
    /** Last activity timestamp for auto-lock */
    var lastActivityTime: Long
        get() = prefs.getLong("last_activity_time", System.currentTimeMillis())
        set(value) = prefs.edit().putLong("last_activity_time", value).apply()
    
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

    // ============ Session Management ============
    
    fun startSession(userId: String) {
        prefs.edit()
            .putBoolean(KEY_SESSION_ACTIVE, true)
            .putString(KEY_CURRENT_USER_ID, userId)
            .apply()
        updateLastActivity()
    }

    fun clearSession() {
        prefs.edit()
            .putBoolean(KEY_SESSION_ACTIVE, false)
            .remove(KEY_CURRENT_USER_ID)
            .putBoolean(KEY_APP_LOCKED, false)
            .putLong(KEY_COOLDOWN_UNTIL, 0)
            .apply()
    }

    // ============ Full Clear ============
    
    fun clearAll() {
        prefs.edit().clear().apply()
    }

    fun clearSmtpConfig() {
        prefs.edit()
            .remove(KEY_SMTP_HOST)
            .remove(KEY_SMTP_PORT)
            .remove(KEY_SMTP_USERNAME)
            .remove(KEY_SMTP_PASSWORD)
            .remove(KEY_SMTP_USE_TLS)
            .remove(KEY_ALERT_RECIPIENT)
            .apply()
    }
    
    // ============ Intruder Capture ============
    
    var isIntruderCaptureEnabled: Boolean
        get() = prefs.getBoolean(KEY_INTRUDER_CAPTURE_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_INTRUDER_CAPTURE_ENABLED, value).apply()
    
    var alertEmail: String?
        get() = prefs.getString(KEY_ALERT_EMAIL, null)
        set(value) = prefs.edit().putString(KEY_ALERT_EMAIL, value).apply()
    
    // ============ SMTP Configuration Helper ============
    
    data class SMTPConfiguration(
        val host: String,
        val port: Int,
        val username: String,
        val password: String,
        val useTls: Boolean,
        val recipient: String
    )
    
    fun getSMTPConfiguration(): SMTPConfiguration? {
        val host = smtpHost ?: return null
        val username = smtpUsername ?: return null
        val password = smtpPassword ?: return null
        val recipient = alertRecipient ?: return null
        
        return SMTPConfiguration(
            host = host,
            port = smtpPort,
            username = username,
            password = password,
            useTls = smtpUseTls,
            recipient = recipient
        )
    }
}
