package com.sentinelguard.security.collector.detectors

import android.app.ActivityManager
import android.content.Context
import android.os.Build

/**
 * Detects if screen recording or screen capture is active.
 * 
 * Detection methods:
 * - Media projection detection (limited API)
 * - Running service detection
 * 
 * Limitation: This detection has limited capability on Android.
 * Modern screen recording may not be fully detectable.
 */
class ScreenRecordingDetector(private val context: Context) {

    companion object {
        // Known screen recording app package names
        private val RECORDING_PACKAGES = listOf(
            "com.hecorat.screenrecorder.free",
            "com.kimcy929.screenrecorder",
            "com.nll.screenrecorder",
            "com.duapps.recorder",
            "com.rsupport.mvagent",
            "com.applisto.appcloner",
            "com.az.screen.recorder",
            "screenrecorder",
            "scrcpy"
        )

        private val RECORDING_SERVICE_NAMES = listOf(
            "MediaProjection",
            "ScreenCapture",
            "ScreenRecorder"
        )
    }

    /**
     * Returns true if screen recording is likely active.
     */
    fun isScreenRecording(): Boolean {
        // Check for running recording services
        if (checkRecordingServices()) return true
        
        // Check for known recording apps running
        if (checkRecordingApps()) return true
        
        return false
    }

    private fun checkRecordingServices(): Boolean {
        try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            
            @Suppress("DEPRECATION")
            val runningServices = activityManager.getRunningServices(50)
            
            for (service in runningServices) {
                val serviceName = service.service.className.lowercase()
                for (recordingName in RECORDING_SERVICE_NAMES) {
                    if (serviceName.contains(recordingName.lowercase())) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            // Cannot check services
        }
        return false
    }

    private fun checkRecordingApps(): Boolean {
        try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            
            // Get running app processes
            val runningApps = activityManager.runningAppProcesses ?: return false
            
            for (app in runningApps) {
                val processName = app.processName.lowercase()
                for (recordingPackage in RECORDING_PACKAGES) {
                    if (processName.contains(recordingPackage)) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            // Cannot check apps
        }
        return false
    }
}
