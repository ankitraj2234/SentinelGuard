package com.sentinelguard.security.collector.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

/**
 * Receives network connectivity changes.
 * Note: This receiver is for legacy support. 
 * NetworkDetector uses NetworkCallback for modern detection.
 */
class NetworkChangeReceiver : BroadcastReceiver() {

    @Suppress("DEPRECATION")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            // Network change detected
            // Signal will be captured on next app open
        }
    }
}
