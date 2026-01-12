package com.sentinelguard.security.collector

import android.content.Context
import android.os.Build
import com.google.android.gms.location.FusedLocationProviderClient
import com.sentinelguard.data.database.dao.SecuritySignalDao
import com.sentinelguard.data.database.entities.SecuritySignalEntity
import com.sentinelguard.data.database.entities.SignalType
import com.sentinelguard.data.preferences.SecurePreferencesManager
import com.sentinelguard.security.collector.detectors.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central orchestrator for security signal collection.
 * 
 * Coordinates all detectors and stores collected signals to the database.
 * Signals are collected on-demand (when app opens) or via broadcast receivers.
 */
@Singleton
class SignalCollector @Inject constructor(
    private val context: Context,
    private val securitySignalDao: SecuritySignalDao,
    private val securePrefs: SecurePreferencesManager,
    private val fusedLocationClient: FusedLocationProviderClient
) {

    // Initialize all detectors
    private val emulatorDetector = EmulatorDetector()
    private val rootDetector = RootDetector()
    private val debuggerDetector = DebuggerDetector()
    private val screenRecordingDetector = ScreenRecordingDetector(context)
    private val networkDetector = NetworkDetector(context)
    private val simDetector = SimDetector(context)
    private val timezoneLocaleDetector = TimezoneLocaleDetector(context)
    private val locationDetector = LocationDetector(context, fusedLocationClient)
    private val deviceStateDetector = DeviceStateDetector(context, securePrefs)
    private val appUsageDetector = AppUsageDetector()

    /**
     * Collects all security signals when app opens.
     * This is the main entry point called from MainActivity.
     */
    suspend fun collectAllSignals(): List<SecuritySignalEntity> = withContext(Dispatchers.IO) {
        val signals = mutableListOf<SecuritySignalEntity>()
        val timestamp = System.currentTimeMillis()

        // App usage signal
        signals.add(
            SecuritySignalEntity(
                signalType = SignalType.APP_OPEN,
                timestamp = timestamp
            )
        )

        // Environment security checks
        if (emulatorDetector.isEmulator()) {
            signals.add(
                SecuritySignalEntity(
                    signalType = SignalType.EMULATOR_DETECTED,
                    value = emulatorDetector.getDetectionDetails(),
                    timestamp = timestamp
                )
            )
        }

        if (rootDetector.isRooted()) {
            signals.add(
                SecuritySignalEntity(
                    signalType = SignalType.ROOT_DETECTED,
                    value = rootDetector.getDetectionDetails(),
                    timestamp = timestamp
                )
            )
        }

        if (debuggerDetector.isDebuggerAttached()) {
            signals.add(
                SecuritySignalEntity(
                    signalType = SignalType.DEBUGGER_DETECTED,
                    value = debuggerDetector.getDetectionDetails(),
                    timestamp = timestamp
                )
            )
        }

        if (screenRecordingDetector.isScreenRecording()) {
            signals.add(
                SecuritySignalEntity(
                    signalType = SignalType.SCREEN_RECORDING_DETECTED,
                    timestamp = timestamp
                )
            )
        }

        // Network state
        val networkState = networkDetector.getCurrentNetworkState()
        val networkSignalType = when (networkState.type) {
            NetworkType.WIFI -> SignalType.NETWORK_WIFI
            NetworkType.MOBILE -> SignalType.NETWORK_MOBILE
            NetworkType.NONE -> SignalType.NETWORK_NONE
        }
        
        // Check for network change
        val lastNetworkType = securePrefs.lastNetworkType
        if (lastNetworkType != null && lastNetworkType != networkState.type.name) {
            signals.add(
                SecuritySignalEntity(
                    signalType = SignalType.NETWORK_CHANGE,
                    value = JSONObject().apply {
                        put("from", lastNetworkType)
                        put("to", networkState.type.name)
                    }.toString(),
                    timestamp = timestamp
                )
            )
        }
        securePrefs.lastNetworkType = networkState.type.name
        
        signals.add(
            SecuritySignalEntity(
                signalType = networkSignalType,
                value = networkState.toJson(),
                timestamp = timestamp
            )
        )

        // SIM state
        val simState = simDetector.getSimState()
        val simSignalType = when {
            simState.isRemoved -> SignalType.SIM_REMOVED
            simState.hasChanged -> SignalType.SIM_CHANGED
            else -> SignalType.SIM_PRESENT
        }
        
        if (simState.isRemoved || simState.hasChanged) {
            signals.add(
                SecuritySignalEntity(
                    signalType = simSignalType,
                    value = simState.toJson(),
                    timestamp = timestamp
                )
            )
        }

        // Device boot detection
        val bootDetection = deviceStateDetector.checkBootState()
        if (bootDetection.rebootDetected) {
            signals.add(
                SecuritySignalEntity(
                    signalType = SignalType.DEVICE_BOOT,
                    value = bootDetection.toJson(),
                    timestamp = timestamp
                )
            )
        }

        // Timezone/locale changes
        val tzLocale = timezoneLocaleDetector.checkChanges()
        if (tzLocale.timezoneChanged) {
            signals.add(
                SecuritySignalEntity(
                    signalType = SignalType.TIMEZONE_CHANGE,
                    value = JSONObject().apply {
                        put("from", tzLocale.previousTimezone)
                        put("to", tzLocale.currentTimezone)
                    }.toString(),
                    timestamp = timestamp
                )
            )
        }
        if (tzLocale.localeChanged) {
            signals.add(
                SecuritySignalEntity(
                    signalType = SignalType.LOCALE_CHANGE,
                    value = JSONObject().apply {
                        put("from", tzLocale.previousLocale)
                        put("to", tzLocale.currentLocale)
                    }.toString(),
                    timestamp = timestamp
                )
            )
        }

        // Location (on app open only)
        try {
            val location = locationDetector.getCurrentLocation()
            if (location != null) {
                signals.add(
                    SecuritySignalEntity(
                        signalType = SignalType.LOCATION_UPDATE,
                        value = location.toJson(),
                        timestamp = timestamp
                    )
                )
                
                // Update last known location
                securePrefs.lastLocationLat = location.latitude
                securePrefs.lastLocationLng = location.longitude
            }
        } catch (e: Exception) {
            // Location not available - don't fail signal collection
        }

        // Store all signals
        if (signals.isNotEmpty()) {
            securitySignalDao.insertAll(signals)
        }

        signals
    }

    /**
     * Records a login attempt result.
     */
    suspend fun recordLoginAttempt(success: Boolean) {
        val signalType = if (success) SignalType.LOGIN_SUCCESS else SignalType.LOGIN_FAILURE
        securitySignalDao.insert(
            SecuritySignalEntity(
                signalType = signalType,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    /**
     * Records app session end.
     */
    suspend fun recordAppClose() {
        securitySignalDao.insert(
            SecuritySignalEntity(
                signalType = SignalType.APP_CLOSE,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    /**
     * Gets recent signals for analysis.
     */
    suspend fun getRecentSignals(limit: Int = 100): List<SecuritySignalEntity> {
        return securitySignalDao.getRecent(limit)
    }

    /**
     * Gets signals within a time range.
     */
    suspend fun getSignalsInRange(startTime: Long, endTime: Long): List<SecuritySignalEntity> {
        return securitySignalDao.getInRange(startTime, endTime)
    }

    /**
     * Cleans up old signals based on retention policy.
     */
    suspend fun cleanupOldSignals() {
        val retentionDays = securePrefs.dataRetentionDays
        val cutoffTime = System.currentTimeMillis() - (retentionDays * 24 * 60 * 60 * 1000L)
        securitySignalDao.deleteOlderThan(cutoffTime)
    }
}
