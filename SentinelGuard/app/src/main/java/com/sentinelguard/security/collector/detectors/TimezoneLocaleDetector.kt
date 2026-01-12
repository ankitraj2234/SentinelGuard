package com.sentinelguard.security.collector.detectors

import android.content.Context
import android.content.SharedPreferences
import java.util.Locale
import java.util.TimeZone

/**
 * Timezone and locale change detection result.
 */
data class TimezoneLocaleState(
    val currentTimezone: String,
    val currentLocale: String,
    val previousTimezone: String?,
    val previousLocale: String?,
    val timezoneChanged: Boolean,
    val localeChanged: Boolean
)

/**
 * Detects timezone and locale changes.
 * 
 * Changes in timezone or locale can indicate:
 * - Device was taken to a different country
 * - Attacker changed settings to avoid detection
 */
class TimezoneLocaleDetector(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "timezone_locale_prefs"
        private const val KEY_LAST_TIMEZONE = "last_timezone"
        private const val KEY_LAST_LOCALE = "last_locale"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, 
        Context.MODE_PRIVATE
    )

    /**
     * Checks for timezone or locale changes since last check.
     */
    fun checkChanges(): TimezoneLocaleState {
        val currentTimezone = TimeZone.getDefault().id
        val currentLocale = Locale.getDefault().toString()
        
        val previousTimezone = prefs.getString(KEY_LAST_TIMEZONE, null)
        val previousLocale = prefs.getString(KEY_LAST_LOCALE, null)
        
        val timezoneChanged = previousTimezone != null && previousTimezone != currentTimezone
        val localeChanged = previousLocale != null && previousLocale != currentLocale
        
        // Update stored values
        prefs.edit()
            .putString(KEY_LAST_TIMEZONE, currentTimezone)
            .putString(KEY_LAST_LOCALE, currentLocale)
            .apply()
        
        return TimezoneLocaleState(
            currentTimezone = currentTimezone,
            currentLocale = currentLocale,
            previousTimezone = previousTimezone,
            previousLocale = previousLocale,
            timezoneChanged = timezoneChanged,
            localeChanged = localeChanged
        )
    }

    /**
     * Gets current timezone.
     */
    fun getCurrentTimezone(): String {
        return TimeZone.getDefault().id
    }

    /**
     * Gets current locale.
     */
    fun getCurrentLocale(): String {
        return Locale.getDefault().toString()
    }

    /**
     * Resets stored values (for testing or initial setup).
     */
    fun reset() {
        prefs.edit().clear().apply()
    }
}
