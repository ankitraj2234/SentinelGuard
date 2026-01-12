package com.sentinelguard.security.collector.detectors

import android.content.Context
import android.os.SystemClock
import com.sentinelguard.data.preferences.SecurePreferencesManager
import org.json.JSONObject

/**
 * Device boot detection result.
 */
data class BootDetection(
    val rebootDetected: Boolean,
    val currentBootTime: Long,
    val previousBootTime: Long?,
    val uptimeMs: Long
) {
    fun toJson(): String = JSONObject().apply {
        put("reboot_detected", rebootDetected)
        put("current_boot_time", currentBootTime)
        put("previous_boot_time", previousBootTime)
        put("uptime_ms", uptimeMs)
    }.toString()
}

/**
 * Detects device boots and reboots.
 * 
 * Detection method:
 * - Compare system boot time with last recorded boot time
 * - SystemClock.elapsedRealtime() gives time since boot
 * 
 * Also receives BOOT_COMPLETED broadcast via BootReceiver.
 */
class DeviceStateDetector(
    private val context: Context,
    private val securePrefs: SecurePreferencesManager
) {

    /**
     * Checks if device was rebooted since last app open.
     */
    fun checkBootState(): BootDetection {
        // Calculate boot time (current time - uptime)
        val uptimeMs = SystemClock.elapsedRealtime()
        val currentBootTime = System.currentTimeMillis() - uptimeMs
        
        // Get previously recorded boot time
        val previousBootTime = securePrefs.lastBootTime
        
        // Detect if this is a new boot
        // Allow 5 minute tolerance for clock differences
        val rebootDetected = previousBootTime > 0 && 
            kotlin.math.abs(currentBootTime - previousBootTime) > 300_000
        
        // Store current boot time
        securePrefs.lastBootTime = currentBootTime
        
        return BootDetection(
            rebootDetected = rebootDetected,
            currentBootTime = currentBootTime,
            previousBootTime = if (previousBootTime > 0) previousBootTime else null,
            uptimeMs = uptimeMs
        )
    }

    /**
     * Gets current device uptime in milliseconds.
     */
    fun getUptimeMs(): Long {
        return SystemClock.elapsedRealtime()
    }

    /**
     * Called from BootReceiver when device boots.
     */
    fun onBootCompleted() {
        val uptimeMs = SystemClock.elapsedRealtime()
        val bootTime = System.currentTimeMillis() - uptimeMs
        securePrefs.lastBootTime = bootTime
    }
}
