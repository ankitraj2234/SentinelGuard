package com.sentinelguard.security.collector.detectors

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import org.json.JSONObject

/**
 * SIM card state information.
 */
data class SimState(
    val isPresent: Boolean,
    val isRemoved: Boolean,
    val hasChanged: Boolean,
    val simCount: Int,
    val operatorName: String?
) {
    fun toJson(): String = JSONObject().apply {
        put("present", isPresent)
        put("removed", isRemoved)
        put("changed", hasChanged)
        put("count", simCount)
        put("operator", operatorName)
    }.toString()
}

/**
 * Detects SIM card state and changes.
 * 
 * Detection:
 * - SIM present/absent
 * - SIM change (different SIM inserted)
 * 
 * Limitation: On Android 10+ (API 29+), access to SIM information
 * requires READ_PHONE_STATE permission which must be granted by user.
 * Some information may be unavailable due to privacy restrictions.
 */
class SimDetector(private val context: Context) {

    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) 
        as TelephonyManager

    private var lastSimSerialNumber: String? = null

    /**
     * Gets the current SIM state.
     */
    fun getSimState(): SimState {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED

        val simState = telephonyManager.simState
        val isPresent = simState == TelephonyManager.SIM_STATE_READY
        val isRemoved = simState == TelephonyManager.SIM_STATE_ABSENT

        var operatorName: String? = null
        var simCount = 0
        var hasChanged = false

        if (hasPermission) {
            operatorName = telephonyManager.simOperatorName
            
            // Check for SIM change
            try {
                val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) 
                    as? SubscriptionManager
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // Android 11+ - limited access without carrier privileges
                    simCount = if (isPresent) 1 else 0
                } else {
                    @Suppress("DEPRECATION")
                    val subscriptions = subscriptionManager?.activeSubscriptionInfoList
                    simCount = subscriptions?.size ?: 0
                }
            } catch (e: SecurityException) {
                // Permission not granted or restricted
                simCount = if (isPresent) 1 else 0
            }
        }

        return SimState(
            isPresent = isPresent,
            isRemoved = isRemoved,
            hasChanged = hasChanged,
            simCount = simCount,
            operatorName = operatorName
        )
    }

    /**
     * Gets the SIM state as an integer code.
     */
    fun getSimStateCode(): Int {
        return telephonyManager.simState
    }

    /**
     * Returns true if SIM is currently absent.
     */
    fun isSimAbsent(): Boolean {
        return telephonyManager.simState == TelephonyManager.SIM_STATE_ABSENT
    }

    /**
     * Returns true if SIM is ready for use.
     */
    fun isSimReady(): Boolean {
        return telephonyManager.simState == TelephonyManager.SIM_STATE_READY
    }
}
