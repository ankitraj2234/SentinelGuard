package com.sentinelguard.admin

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * SentinelDeviceAdminReceiver: Device Administrator for Advanced Security
 * 
 * Capabilities:
 * - Force device lock on critical threat
 * - Wipe device data on confirmed theft
 * - Monitor failed password attempts
 * - Disable camera during lockdown
 * 
 * Note: User must explicitly enable device admin in Settings.
 */
class SentinelDeviceAdminReceiver : DeviceAdminReceiver() {

    companion object {
        private const val TAG = "SentinelDeviceAdmin"

        /**
         * Get ComponentName for this receiver
         */
        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context, SentinelDeviceAdminReceiver::class.java)
        }

        /**
         * Check if device admin is enabled
         */
        fun isAdminActive(context: Context): Boolean {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            return dpm.isAdminActive(getComponentName(context))
        }

        /**
         * Request device admin activation
         */
        fun requestAdminActivation(context: Context): Intent {
            return Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, getComponentName(context))
                putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "SentinelGuard needs device administrator access to:\n\n" +
                    "• Lock your device on security threats\n" +
                    "• Wipe data if device is stolen\n" +
                    "• Monitor failed unlock attempts\n"
                )
            }
        }

        /**
         * Force lock the device immediately
         */
        fun lockDevice(context: Context): Boolean {
            return try {
                val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                if (isAdminActive(context)) {
                    dpm.lockNow()
                    Log.i(TAG, "Device locked successfully")
                    true
                } else {
                    Log.w(TAG, "Cannot lock device: Admin not active")
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to lock device", e)
                false
            }
        }

        /**
         * Wipe device data (Factory reset)
         * USE WITH EXTREME CAUTION - This is irreversible!
         */
        fun wipeDevice(context: Context): Boolean {
            return try {
                val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                if (isAdminActive(context)) {
                    Log.w(TAG, "INITIATING DEVICE WIPE - This is irreversible!")
                    dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE)
                    true
                } else {
                    Log.w(TAG, "Cannot wipe device: Admin not active")
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to wipe device", e)
                false
            }
        }

        /**
         * Disable camera (for lockdown mode)
         */
        fun setCameraDisabled(context: Context, disabled: Boolean): Boolean {
            return try {
                val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                if (isAdminActive(context)) {
                    dpm.setCameraDisabled(getComponentName(context), disabled)
                    Log.i(TAG, "Camera disabled: $disabled")
                    true
                } else {
                    Log.w(TAG, "Cannot control camera: Admin not active")
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to control camera", e)
                false
            }
        }

        /**
         * Get current password quality enforcement
         */
        @Suppress("DEPRECATION")
        fun getPasswordQuality(context: Context): Int {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            return dpm.getPasswordQuality(getComponentName(context))
        }

        /**
         * Get count of failed password attempts
         */
        fun getFailedPasswordAttempts(context: Context): Int {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            return dpm.currentFailedPasswordAttempts
        }
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.i(TAG, "Device admin enabled")
        Toast.makeText(context, "SentinelGuard security features activated", Toast.LENGTH_SHORT).show()
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        Log.w(TAG, "Device admin disable requested")
        return "Disabling SentinelGuard admin will reduce your device's security protection. Are you sure?"
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.w(TAG, "Device admin disabled")
        Toast.makeText(context, "SentinelGuard security features deactivated", Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Deprecated in DeviceAdminReceiver", ReplaceWith(""))
    @Suppress("DEPRECATION")
    override fun onPasswordFailed(context: Context, intent: Intent) {
        super.onPasswordFailed(context, intent)
        val failedAttempts = getFailedPasswordAttempts(context)
        Log.w(TAG, "Password failed. Total failed attempts: $failedAttempts")
        
        // Trigger security response on multiple failures
        if (failedAttempts >= 3) {
            Log.w(TAG, "Multiple failed password attempts detected!")
            // This will be integrated with SecurityAlertManager
        }
    }

    @Deprecated("Deprecated in DeviceAdminReceiver", ReplaceWith(""))
    @Suppress("DEPRECATION")
    override fun onPasswordSucceeded(context: Context, intent: Intent) {
        super.onPasswordSucceeded(context, intent)
        Log.i(TAG, "Password succeeded")
    }

    @Deprecated("Deprecated in DeviceAdminReceiver", ReplaceWith(""))
    @Suppress("DEPRECATION")
    override fun onPasswordChanged(context: Context, intent: Intent) {
        super.onPasswordChanged(context, intent)
        Log.i(TAG, "Device password changed")
    }
}
