package com.sentinelguard.security.signal

import android.content.Context
import com.sentinelguard.data.local.preferences.SecurePreferences
import com.sentinelguard.domain.model.SecuritySignal
import com.sentinelguard.domain.repository.SecuritySignalRepository
import com.sentinelguard.security.signal.detector.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * SignalCollector: Central Orchestrator for Security Signal Detection
 * 
 * WHY THIS EXISTS:
 * Coordinates all detectors and stores signals in the repository.
 * Single point of entry for signal collection, called on app open.
 * 
 * DESIGN:
 * - Runs all detectors in parallel (except lifecycle which is event-based)
 * - Aggregates signals and batch-inserts to database
 * - Handles errors gracefully (one detector failure doesn't stop others)
 * - No background processing (runs only when app is foregrounded)
 */
class SignalCollector(
    private val context: Context,
    private val signalRepository: SecuritySignalRepository,
    private val securePreferences: SecurePreferences
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Initialize detectors
    val appLifecycleDetector = AppLifecycleDetector(context)
    val screenRecordingDetector = ScreenRecordingDetector(context)
    val networkDetector = NetworkDetector(context, securePreferences)
    val simDetector = SimDetector(context, securePreferences)
    val rebootDetector = RebootDetector(context, securePreferences)

    private val allDetectors: List<Detector> = listOf(
        appLifecycleDetector,
        screenRecordingDetector,
        networkDetector,
        simDetector,
        rebootDetector
    )

    /**
     * Collects signals from all detectors.
     * Called on app open / foreground.
     * 
     * @return List of all detected signals
     */
    suspend fun collectAllSignals(): List<SecuritySignal> = withContext(Dispatchers.IO) {
        val allSignals = mutableListOf<SecuritySignal>()

        // Add app open signal
        allSignals.add(appLifecycleDetector.createAppOpenSignal())

        // Run all detectors
        for (detector in allDetectors) {
            try {
                val signals = detector.detect()
                allSignals.addAll(signals)
            } catch (e: Exception) {
                android.util.Log.e("SignalCollector", "Detector ${detector.name} failed", e)
                // Continue with other detectors
            }
        }

        // Store all signals
        if (allSignals.isNotEmpty()) {
            signalRepository.insertAll(allSignals)
        }

        allSignals
    }

    /**
     * Records a login attempt signal.
     */
    suspend fun recordLoginAttempt(success: Boolean, method: String? = null) {
        val signal = SecuritySignal(
            id = com.sentinelguard.domain.util.SecureIdGenerator.generateId(),
            type = if (success) 
                com.sentinelguard.domain.model.SignalType.LOGIN_SUCCESS 
            else 
                com.sentinelguard.domain.model.SignalType.LOGIN_FAILURE,
            timestamp = System.currentTimeMillis(),
            value = method?.let { "Authenticated via $it" }
        )
        signalRepository.insert(signal)
    }
    
    /**
     * Records biometric authentication result.
     */
    suspend fun recordBiometricAuth(success: Boolean, reason: String? = null) {
        val signal = SecuritySignal(
            id = com.sentinelguard.domain.util.SecureIdGenerator.generateId(),
            type = if (success) 
                com.sentinelguard.domain.model.SignalType.BIOMETRIC_SUCCESS 
            else 
                com.sentinelguard.domain.model.SignalType.BIOMETRIC_FAILED,
            timestamp = System.currentTimeMillis(),
            value = reason
        )
        signalRepository.insert(signal)
    }
    
    /**
     * Records PIN failure with attempt count.
     */
    suspend fun recordPinFailure(attemptNumber: Int, maxAttempts: Int) {
        val signal = SecuritySignal(
            id = com.sentinelguard.domain.util.SecureIdGenerator.generateId(),
            type = com.sentinelguard.domain.model.SignalType.PIN_FAILED,
            timestamp = System.currentTimeMillis(),
            value = "Wrong PIN entered (Attempt $attemptNumber/$maxAttempts)"
        )
        signalRepository.insert(signal)
    }
    
    /**
     * Records network change (WiFi <--> Mobile).
     */
    suspend fun recordNetworkChange(fromNetwork: String, toNetwork: String, carrier: String? = null) {
        val description = buildString {
            append("$fromNetwork → $toNetwork")
            carrier?.let { append(" ($it)") }
        }
        val signal = SecuritySignal(
            id = com.sentinelguard.domain.util.SecureIdGenerator.generateId(),
            type = com.sentinelguard.domain.model.SignalType.NETWORK_CHANGE,
            timestamp = System.currentTimeMillis(),
            value = description
        )
        signalRepository.insert(signal)
    }
    
    /**
     * Records SIM carrier switch (SIM1 <--> SIM2).
     */
    suspend fun recordSimSwitch(fromSim: String, toSim: String) {
        val signal = SecuritySignal(
            id = com.sentinelguard.domain.util.SecureIdGenerator.generateId(),
            type = com.sentinelguard.domain.model.SignalType.NETWORK_SIM_SWITCH,
            timestamp = System.currentTimeMillis(),
            value = "$fromSim → $toSim"
        )
        signalRepository.insert(signal)
    }
    
    /**
     * Records cell tower change.
     */
    suspend fun recordCellTowerChange(fromTowerId: String?, toTowerId: String, carrier: String?, networkType: String?) {
        val description = buildString {
            if (fromTowerId != null) {
                append("Tower $fromTowerId → $toTowerId")
            } else {
                append("Connected to Tower $toTowerId")
            }
            carrier?.let { append(" ($it") }
            networkType?.let { append(", $it)") } ?: carrier?.let { append(")") }
        }
        val signal = SecuritySignal(
            id = com.sentinelguard.domain.util.SecureIdGenerator.generateId(),
            type = com.sentinelguard.domain.model.SignalType.CELL_TOWER_CHANGE,
            timestamp = System.currentTimeMillis(),
            value = description
        )
        signalRepository.insert(signal)
    }
    
    /**
     * Records security scan result.
     */
    suspend fun recordSecurityScan(threatsFound: Int, threatNames: List<String> = emptyList()) {
        val signal = if (threatsFound == 0) {
            SecuritySignal(
                id = com.sentinelguard.domain.util.SecureIdGenerator.generateId(),
                type = com.sentinelguard.domain.model.SignalType.SECURITY_SCAN_COMPLETE,
                timestamp = System.currentTimeMillis(),
                value = "Security scan complete - No threats detected"
            )
        } else {
            SecuritySignal(
                id = com.sentinelguard.domain.util.SecureIdGenerator.generateId(),
                type = com.sentinelguard.domain.model.SignalType.SECURITY_SCAN_THREAT,
                timestamp = System.currentTimeMillis(),
                value = "$threatsFound threats found: ${threatNames.take(3).joinToString(", ")}"
            )
        }
        signalRepository.insert(signal)
    }

    /**
     * Records an app close signal.
     */
    suspend fun recordAppClose() {
        val pendingSignals = appLifecycleDetector.detect()
        if (pendingSignals.isNotEmpty()) {
            signalRepository.insertAll(pendingSignals)
        }
    }

    /**
     * Gets recent signals from repository.
     */
    suspend fun getRecentSignals(limit: Int = 50): List<SecuritySignal> {
        return signalRepository.getRecent(limit)
    }

    /**
     * Observes recent signals as Flow.
     */
    fun observeRecentSignals(limit: Int = 50): Flow<List<SecuritySignal>> {
        return signalRepository.observeRecent(limit)
    }

    /**
     * Gets signals within a time range.
     */
    suspend fun getSignalsInRange(startTime: Long, endTime: Long): List<SecuritySignal> {
        return signalRepository.getInRange(startTime, endTime)
    }

    /**
     * Cleans up resources when app is destroyed.
     */
    fun cleanup() {
        networkDetector.unregister()
    }

    /**
     * Gets debug info from all detectors.
     */
    fun getDebugInfo(): Map<String, Map<String, Any>> {
        return allDetectors.associate { it.name to it.getDebugInfo() }
    }
}
