package com.sentinelguard.security.signal.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sentinelguard.data.local.preferences.SecurePreferences

/**
 * BootReceiver: Receives BOOT_COMPLETED Broadcast
 * 
 * WHY THIS EXISTS:
 * Updates stored boot time when device boots.
 * This allows RebootDetector to detect reboots even if app wasn't running.
 * 
 * REQUIRES:
 * - RECEIVE_BOOT_COMPLETED permission
 * - Registered in AndroidManifest.xml
 * 
 * NOTE:
 * Does NOT start any background service. Only updates preferences.
 */
class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Device boot completed")
            
            try {
                val prefs = SecurePreferences(context)
                val bootTime = System.currentTimeMillis() - android.os.SystemClock.elapsedRealtime()
                prefs.lastBootTime = bootTime
                Log.d(TAG, "Updated boot time: $bootTime")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update boot time", e)
            }
        }
    }
}
