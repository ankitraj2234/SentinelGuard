package com.sentinelguard.security.signal.detector

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.sentinelguard.domain.model.SecuritySignal
import com.sentinelguard.domain.model.SignalType
import com.sentinelguard.domain.util.SecureIdGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.util.Calendar

/**
 * AppLifecycleDetector: Tracks App Open/Close Events
 * 
 * WHY THIS EXISTS:
 * Knowing when the app is opened provides crucial behavioral data:
 * - Time-of-day patterns (user typically opens at 9 AM, not 3 AM)
 * - Frequency patterns (user opens 5x/day, not 50x)
 * - Session duration (typical session is 2 min, not 30 min)
 * 
 * IMPLEMENTATION:
 * Uses ProcessLifecycleOwner to observe app-level lifecycle.
 * This fires even if Activities are destroyed/recreated.
 * 
 * SIGNALS PRODUCED:
 * - APP_OPEN: When app comes to foreground
 * - APP_CLOSE: When app goes to background
 * - APP_SESSION: After close, with duration metadata
 */
class AppLifecycleDetector(
    private val context: Context
) : Detector, DefaultLifecycleObserver {

    override val name: String = "AppLifecycle"

    private var _appOpenTime: Long? = null
    private var _lastAppCloseTime: Long? = null
    private var _sessionCount: Int = 0
    
    private val _isInForeground = MutableStateFlow(false)
    val isInForeground: StateFlow<Boolean> = _isInForeground.asStateFlow()

    private val pendingSignals = mutableListOf<SecuritySignal>()

    init {
        // Register as lifecycle observer
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    /**
     * Called when app enters foreground.
     */
    override fun onStart(owner: LifecycleOwner) {
        val now = System.currentTimeMillis()
        _appOpenTime = now
        _sessionCount++
        _isInForeground.value = true

        // Create APP_OPEN signal
        val signal = SecuritySignal(
            id = SecureIdGenerator.generateId(),
            type = SignalType.APP_OPEN,
            value = null,
            metadata = buildOpenMetadata(now),
            timestamp = now
        )
        
        synchronized(pendingSignals) {
            pendingSignals.add(signal)
        }
    }

    /**
     * Called when app enters background.
     */
    override fun onStop(owner: LifecycleOwner) {
        val now = System.currentTimeMillis()
        _lastAppCloseTime = now
        _isInForeground.value = false

        val sessionDuration = _appOpenTime?.let { now - it }

        // Create APP_CLOSE signal
        val closeSignal = SecuritySignal(
            id = SecureIdGenerator.generateId(),
            type = SignalType.APP_CLOSE,
            value = sessionDuration?.toString(),
            metadata = null,
            timestamp = now
        )

        // Create APP_SESSION signal with duration
        val sessionSignal = sessionDuration?.let {
            SecuritySignal(
                id = SecureIdGenerator.generateId(),
                type = SignalType.APP_SESSION,
                value = it.toString(),
                metadata = buildSessionMetadata(it),
                timestamp = now
            )
        }

        synchronized(pendingSignals) {
            pendingSignals.add(closeSignal)
            sessionSignal?.let { pendingSignals.add(it) }
        }
    }

    /**
     * Detects current state (used on collector run).
     * Returns pending signals and clears the buffer.
     */
    override suspend fun detect(): List<SecuritySignal> {
        return synchronized(pendingSignals) {
            val signals = pendingSignals.toList()
            pendingSignals.clear()
            signals
        }
    }

    /**
     * Creates an APP_OPEN signal for immediate use.
     * Called by SignalCollector on app open.
     */
    fun createAppOpenSignal(): SecuritySignal {
        val now = System.currentTimeMillis()
        return SecuritySignal(
            id = SecureIdGenerator.generateId(),
            type = SignalType.APP_OPEN,
            value = null,
            metadata = buildOpenMetadata(now),
            timestamp = now
        )
    }

    override fun getDebugInfo(): Map<String, Any> {
        return mapOf(
            "isInForeground" to _isInForeground.value,
            "sessionCount" to _sessionCount,
            "lastOpenTime" to (_appOpenTime ?: 0L),
            "lastCloseTime" to (_lastAppCloseTime ?: 0L)
        )
    }

    private fun buildOpenMetadata(timestamp: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        return JSONObject().apply {
            put("hour", calendar.get(Calendar.HOUR_OF_DAY))
            put("dayOfWeek", calendar.get(Calendar.DAY_OF_WEEK))
            put("sessionNumber", _sessionCount)
        }.toString()
    }

    private fun buildSessionMetadata(durationMs: Long): String {
        return JSONObject().apply {
            put("durationMs", durationMs)
            put("durationMinutes", durationMs / 60000.0)
        }.toString()
    }

    // ============ Utility Methods ============

    fun getHourOfDay(): Int {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    }

    fun getDayOfWeek(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    }

    fun getSessionDurationMs(): Long? {
        return _appOpenTime?.let { System.currentTimeMillis() - it }
    }
}
