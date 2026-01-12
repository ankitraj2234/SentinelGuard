package com.sentinelguard.security.signal.detector

import android.content.Context
import android.os.SystemClock
import com.sentinelguard.data.local.preferences.SecurePreferences
import com.sentinelguard.domain.model.SecuritySignal
import com.sentinelguard.domain.model.SignalType
import com.sentinelguard.domain.util.SecureIdGenerator
import org.json.JSONObject

/**
 * RebootDetector: Detects Device Reboots
 * 
 * WHY THIS EXISTS:
 * Device reboots are significant security events:
 * - Reboot clears RAM (potential evidence destruction)
 * - Reboot may reset some security measures
 * - Combined with SIM removal, strongly indicates theft
 * - Factory reset preceded by reboot is total data loss
 * 
 * DETECTION METHOD:
 * Compares SystemClock.elapsedRealtime() (time since boot) with
 * last recorded boot time. If current boot time < stored, device rebooted.
 * 
 * SIGNALS PRODUCED:
 * - DEVICE_BOOT: Device has rebooted since last app open
 */
class RebootDetector(
    private val context: Context,
    private val securePreferences: SecurePreferences
) : Detector {

    override val name: String = "Reboot"

    override suspend fun detect(): List<SecuritySignal> {
        val signals = mutableListOf<SecuritySignal>()
        val now = System.currentTimeMillis()

        // Calculate actual boot timestamp
        val bootTimestamp = calculateBootTimestamp()
        val lastRecordedBootTime = securePreferences.lastBootTime

        // If we have a previous boot time and current boot is different, device rebooted
        if (lastRecordedBootTime > 0 && bootTimestamp != lastRecordedBootTime) {
            // Tolerance of 1 second for clock drift
            val timeDiff = kotlin.math.abs(bootTimestamp - lastRecordedBootTime)
            if (timeDiff > 1000) {
                signals.add(
                    SecuritySignal(
                        id = SecureIdGenerator.generateId(),
                        type = SignalType.DEVICE_BOOT,
                        value = bootTimestamp.toString(),
                        metadata = buildMetadata(bootTimestamp, lastRecordedBootTime),
                        timestamp = now
                    )
                )
            }
        }

        // Update stored boot time
        securePreferences.lastBootTime = bootTimestamp

        return signals
    }

    /**
     * Calculates the timestamp when the device booted.
     * 
     * currentTimeMillis - elapsedRealtime = boot time
     */
    private fun calculateBootTimestamp(): Long {
        val elapsedSinceBoot = SystemClock.elapsedRealtime()
        return System.currentTimeMillis() - elapsedSinceBoot
    }

    /**
     * Returns time since last boot in milliseconds.
     */
    fun getUptimeMs(): Long {
        return SystemClock.elapsedRealtime()
    }

    /**
     * Returns time since last boot in human-readable format.
     */
    fun getUptimeFormatted(): String {
        val uptimeMs = getUptimeMs()
        val seconds = (uptimeMs / 1000) % 60
        val minutes = (uptimeMs / (1000 * 60)) % 60
        val hours = (uptimeMs / (1000 * 60 * 60)) % 24
        val days = uptimeMs / (1000 * 60 * 60 * 24)

        return when {
            days > 0 -> "${days}d ${hours}h ${minutes}m"
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m ${seconds}s"
        }
    }

    /**
     * Checks if device was recently booted (within threshold).
     */
    fun wasRecentlyBooted(thresholdMs: Long = 5 * 60 * 1000): Boolean {
        return getUptimeMs() < thresholdMs
    }

    private fun buildMetadata(currentBoot: Long, lastBoot: Long): String {
        return JSONObject().apply {
            put("bootTimestamp", currentBoot)
            put("previousBootTimestamp", lastBoot)
            put("uptimeMs", getUptimeMs())
            put("uptimeFormatted", getUptimeFormatted())
            put("recentlyBooted", wasRecentlyBooted())
        }.toString()
    }

    override fun getDebugInfo(): Map<String, Any> {
        return mapOf(
            "bootTimestamp" to calculateBootTimestamp(),
            "uptimeMs" to getUptimeMs(),
            "uptimeFormatted" to getUptimeFormatted(),
            "recentlyBooted" to wasRecentlyBooted()
        )
    }
}
