package com.sentinelguard.security.signal.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log

/**
 * NetworkReceiver: Receives Network Connectivity Changes (Legacy)
 * 
 * WHY THIS EXISTS:
 * Legacy support for CONNECTIVITY_ACTION broadcast.
 * Modern apps use ConnectivityManager.NetworkCallback (in NetworkDetector),
 * but this receiver provides backup for edge cases.
 * 
 * NOTE:
 * On API 24+, this receiver works only when app is running.
 * Does NOT wake app from background.
 */
class NetworkReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "NetworkReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        @Suppress("DEPRECATION")
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            Log.d(TAG, "Network connectivity changed")
            
            // Note: We don't do anything here because NetworkDetector
            // handles changes via NetworkCallback. This is just for logging.
        }
    }
}
