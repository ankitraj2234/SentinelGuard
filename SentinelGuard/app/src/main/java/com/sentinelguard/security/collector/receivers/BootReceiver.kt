package com.sentinelguard.security.collector.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sentinelguard.data.local.preferences.SecurePreferences
import com.sentinelguard.service.MonitoringService

/**
 * Receives BOOT_COMPLETED broadcast to detect device reboots
 * and restart the monitoring service.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            
            // Update boot time in preferences
            val prefs = SecurePreferences(context)
            val bootTime = System.currentTimeMillis() - android.os.SystemClock.elapsedRealtime()
            prefs.lastBootTime = bootTime
            
            // Restart monitoring service if user was logged in
            if (prefs.isSessionActive && prefs.isSetupComplete) {
                MonitoringService.start(context)
            }
        }
    }
}
