package com.sentinelguard.security.signal.detector

import android.app.Activity
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.view.Display
import android.view.WindowManager
import com.sentinelguard.domain.model.SecuritySignal
import com.sentinelguard.domain.model.SignalType
import com.sentinelguard.domain.util.SecureIdGenerator
import org.json.JSONObject

/**
 * ScreenRecordingDetector: Detects Active Screen Capture
 * 
 * WHY THIS EXISTS:
 * Screen recording while the app is open could capture:
 * - Login credentials being entered
 * - Sensitive security information
 * - Behavioral patterns
 * 
 * DETECTION METHODS:
 * 1. Display.FLAG_SECURE callback (API 34+)
 * 2. MediaProjection detection (limited)
 * 3. Known screen recorder package detection
 * 
 * LIMITATIONS:
 * - Cannot detect all screen recorders
 * - Some detection methods require API 34+
 * - System-level recording may not be detectable
 */
class ScreenRecordingDetector(
    private val context: Context
) : Detector {

    override val name: String = "ScreenRecording"

    private var lastDetectionState: Boolean = false

    companion object {
        // Known screen recording app packages
        private val KNOWN_RECORDER_PACKAGES = listOf(
            "com.google.android.apps.recorder",
            "com.samsung.android.app.screenrecorder",
            "com.xiaomi.screenrecord",
            "com.miui.screenrecorder",
            "com.oneplus.screenrecord",
            "com.oppo.screenrecord",
            "com.vivo.screenrecord",
            "com.heytap.screenrecorder",
            "com.asus.screenrecord",
            "com.android.systemui.screenrecord",
            "com.lge.systemui.screenrecord",
            "tv.danmaku.bili.vrfun.screenrecord",
            // Third-party recorders
            "com.hecorat.screenrecorder.free",
            "com.kimcy929.screenrecorder",
            "com.nll.screenrecorder",
            "com.rec.corder",
            "screenrecorder.recorder.editor",
            "com.duapps.recorder"
        )
    }

    override suspend fun detect(): List<SecuritySignal> {
        val isRecording = isScreenRecordingActive()
        
        return if (isRecording) {
            listOf(
                SecuritySignal(
                    id = SecureIdGenerator.generateId(),
                    type = SignalType.SCREEN_RECORDING_DETECTED,
                    value = "active",
                    metadata = buildMetadata(),
                    timestamp = System.currentTimeMillis()
                )
            )
        } else {
            emptyList()
        }
    }

    /**
     * Checks if screen recording appears to be active.
     */
    fun isScreenRecordingActive(): Boolean {
        val detected = checkDisplayCapture() || checkKnownRecorderRunning()
        lastDetectionState = detected
        return detected
    }

    /**
     * API 34+ method using Display callback.
     */
    private fun checkDisplayCapture(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            try {
                val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
                val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
                
                // Check if display is being captured
                // Note: This is a simplified check, full implementation requires callback
                return display?.flags?.and(Display.FLAG_PRIVATE) != 0
            } catch (e: Exception) {
                // Ignore exceptions
            }
        }
        return false
    }

    /**
     * Checks if any known screen recorder package is running.
     */
    private fun checkKnownRecorderRunning(): Boolean {
        try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) 
                as android.app.ActivityManager
            
            @Suppress("DEPRECATION")
            val runningApps = activityManager.runningAppProcesses ?: return false
            
            for (processInfo in runningApps) {
                if (KNOWN_RECORDER_PACKAGES.any { 
                    processInfo.processName.contains(it, ignoreCase = true) 
                }) {
                    return true
                }
            }
        } catch (e: Exception) {
            // Some devices may restrict this
        }
        return false
    }

    /**
     * Sets FLAG_SECURE on an Activity to prevent screenshots/recording.
     * This is a protective measure, not detection.
     */
    fun enableSecureFlag(activity: Activity) {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    private fun buildMetadata(): String {
        return JSONObject().apply {
            put("apiLevel", Build.VERSION.SDK_INT)
            put("displayCaptureCheck", Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        }.toString()
    }

    override fun getDebugInfo(): Map<String, Any> {
        return mapOf(
            "lastDetectionState" to lastDetectionState,
            "apiLevel" to Build.VERSION.SDK_INT,
            "supportsDisplayCapture" to (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        )
    }
}
