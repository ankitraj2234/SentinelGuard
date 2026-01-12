package com.sentinelguard.permission

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PermissionManager: Handles all runtime permission checks and requests.
 * 
 * Manages:
 * - Runtime permissions (location, phone state, notifications)
 * - Special permissions (usage stats - requires Settings navigation)
 */
@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    /**
     * Permission status for all required permissions
     */
    data class PermissionStatus(
        val locationGranted: Boolean,
        val phoneStateGranted: Boolean,
        val notificationGranted: Boolean,
        val usageStatsGranted: Boolean,
        val cameraGranted: Boolean,
        val allGranted: Boolean
    )
    
    /**
     * Check status of all permissions
     */
    fun checkPermissions(): PermissionStatus {
        val location = hasLocationPermission()
        val phoneState = hasPhoneStatePermission()
        val notification = hasNotificationPermission()
        val usageStats = hasUsageStatsPermission()
        val camera = hasCameraPermission()
        
        return PermissionStatus(
            locationGranted = location,
            phoneStateGranted = phoneState,
            notificationGranted = notification,
            usageStatsGranted = usageStats,
            cameraGranted = camera,
            allGranted = location && phoneState && notification && usageStats && camera
        )
    }
    
    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * List of runtime permissions to request
     */
    fun getRuntimePermissions(): List<String> {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA
        )
        
        // Notification permission only needed on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        return permissions
    }
    
    /**
     * Get permissions that haven't been granted yet
     */
    fun getMissingRuntimePermissions(): List<String> {
        return getRuntimePermissions().filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
    }
    
    // ============ Individual Permission Checks ============
    
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    fun hasPhoneStatePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, 
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Not needed on older Android
        }
    }
    
    /**
     * Check if Usage Stats permission is granted.
     * This requires special handling as it's a system permission.
     */
    fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }
    
    // ============ Intent Builders for Settings ============
    
    /**
     * Get intent to open Usage Stats Settings
     */
    fun getUsageStatsSettingsIntent(): Intent {
        return Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
    
    /**
     * Get intent to open App Settings (for denied permissions)
     */
    fun getAppSettingsIntent(): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
    
    /**
     * Get intent to open Notification Settings
     */
    fun getNotificationSettingsIntent(): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        } else {
            getAppSettingsIntent()
        }
    }
}
