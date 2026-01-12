package com.sentinelguard.scanner

import android.Manifest
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PrivacyScanner: App Permission and Privacy Analysis
 * 
 * Audits all apps for:
 * - Dangerous permission combinations
 * - Background location access
 * - Camera/Microphone access
 * - Contact/SMS/Call log access
 * - Accessibility services
 * - Device admin apps
 * - Overlay permissions
 * - Battery exempt apps
 */
@Singleton
class PrivacyScanner @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "PrivacyScanner"
        
        // Dangerous permissions to check
        private val CAMERA_PERMISSIONS = listOf(
            Manifest.permission.CAMERA
        )
        
        private val MICROPHONE_PERMISSIONS = listOf(
            Manifest.permission.RECORD_AUDIO
        )
        
        private val LOCATION_PERMISSIONS = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
        
        private val CONTACT_PERMISSIONS = listOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS
        )
        
        private val SMS_PERMISSIONS = listOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS
        )
        
        private val CALL_PERMISSIONS = listOf(
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            "android.permission.PROCESS_OUTGOING_CALLS" // Deprecated but still valid for detection
        )
        
        private val STORAGE_PERMISSIONS = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
        )
    }
    
    /**
     * Privacy scan result
     */
    data class PrivacyScanResult(
        val totalAppsScanned: Int,
        val cameraApps: List<PrivacyAppInfo>,
        val microphoneApps: List<PrivacyAppInfo>,
        val locationApps: List<PrivacyAppInfo>,
        val backgroundLocationApps: List<PrivacyAppInfo>,
        val contactApps: List<PrivacyAppInfo>,
        val smsApps: List<PrivacyAppInfo>,
        val callLogApps: List<PrivacyAppInfo>,
        val storageApps: List<PrivacyAppInfo>,
        val accessibilityApps: List<PrivacyAppInfo>,
        val deviceAdminApps: List<PrivacyAppInfo>,
        val overlayApps: List<PrivacyAppInfo>,
        val batteryExemptApps: List<PrivacyAppInfo>,
        val highRiskApps: List<HighRiskAppInfo>,
        val privacyScore: Int
    )
    
    data class PrivacyAppInfo(
        val packageName: String,
        val appName: String,
        val isSystemApp: Boolean,
        val permissions: List<String>
    )
    
    data class HighRiskAppInfo(
        val packageName: String,
        val appName: String,
        val reason: String,
        val riskLevel: RiskLevel,
        val permissions: List<String>
    )
    
    enum class RiskLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    /**
     * Perform comprehensive privacy scan
     */
    suspend fun performFullScan(): PrivacyScanResult = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        
        // Get all installed packages with permissions
        val packages = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        
        val cameraApps = mutableListOf<PrivacyAppInfo>()
        val microphoneApps = mutableListOf<PrivacyAppInfo>()
        val locationApps = mutableListOf<PrivacyAppInfo>()
        val backgroundLocationApps = mutableListOf<PrivacyAppInfo>()
        val contactApps = mutableListOf<PrivacyAppInfo>()
        val smsApps = mutableListOf<PrivacyAppInfo>()
        val callLogApps = mutableListOf<PrivacyAppInfo>()
        val storageApps = mutableListOf<PrivacyAppInfo>()
        val highRiskApps = mutableListOf<HighRiskAppInfo>()
        
        for (pkg in packages) {
            val permissions = pkg.requestedPermissions?.toList() ?: emptyList()
            val appName = pkg.applicationInfo?.loadLabel(packageManager)?.toString() 
                ?: pkg.packageName
            val isSystem = isSystemApp(pkg.applicationInfo)
            
            // Skip our own app
            if (pkg.packageName == context.packageName) continue
            
            val appInfo = PrivacyAppInfo(
                packageName = pkg.packageName,
                appName = appName,
                isSystemApp = isSystem,
                permissions = permissions
            )
            
            // Camera
            if (permissions.any { it in CAMERA_PERMISSIONS }) {
                cameraApps.add(appInfo)
            }
            
            // Microphone
            if (permissions.any { it in MICROPHONE_PERMISSIONS }) {
                microphoneApps.add(appInfo)
            }
            
            // Location
            if (permissions.any { it in LOCATION_PERMISSIONS }) {
                locationApps.add(appInfo)
                
                if (Manifest.permission.ACCESS_BACKGROUND_LOCATION in permissions) {
                    backgroundLocationApps.add(appInfo)
                }
            }
            
            // Contacts
            if (permissions.any { it in CONTACT_PERMISSIONS }) {
                contactApps.add(appInfo)
            }
            
            // SMS
            if (permissions.any { it in SMS_PERMISSIONS }) {
                smsApps.add(appInfo)
            }
            
            // Call Log
            if (permissions.any { it in CALL_PERMISSIONS }) {
                callLogApps.add(appInfo)
            }
            
            // Storage
            if (permissions.any { it in STORAGE_PERMISSIONS }) {
                storageApps.add(appInfo)
            }
            
            // Detect high-risk apps
            val highRisk = detectHighRiskApp(pkg.packageName, appName, permissions, isSystem)
            if (highRisk != null) {
                highRiskApps.add(highRisk)
            }
        }
        
        // Get accessibility apps
        val accessibilityApps = getAccessibilityApps()
        
        // Get device admin apps
        val deviceAdminApps = getDeviceAdminApps()
        
        // Get overlay apps
        val overlayApps = getOverlayApps(packages)
        
        // Get battery exempt apps
        val batteryExemptApps = getBatteryExemptApps(packages)
        
        // Calculate privacy score (lower is better)
        val privacyScore = calculatePrivacyScore(
            backgroundLocationApps.size,
            accessibilityApps.size,
            deviceAdminApps.size,
            overlayApps.size,
            highRiskApps.size
        )
        
        Log.i(TAG, "Privacy scan complete. High risk apps: ${highRiskApps.size}")
        
        PrivacyScanResult(
            totalAppsScanned = packages.size,
            cameraApps = cameraApps.sortedBy { it.isSystemApp },
            microphoneApps = microphoneApps.sortedBy { it.isSystemApp },
            locationApps = locationApps.sortedBy { it.isSystemApp },
            backgroundLocationApps = backgroundLocationApps.sortedBy { it.isSystemApp },
            contactApps = contactApps.sortedBy { it.isSystemApp },
            smsApps = smsApps.sortedBy { it.isSystemApp },
            callLogApps = callLogApps.sortedBy { it.isSystemApp },
            storageApps = storageApps.sortedBy { it.isSystemApp },
            accessibilityApps = accessibilityApps,
            deviceAdminApps = deviceAdminApps,
            overlayApps = overlayApps,
            batteryExemptApps = batteryExemptApps,
            highRiskApps = highRiskApps,
            privacyScore = privacyScore
        )
    }
    
    /**
     * Detect high-risk app based on permission combinations
     */
    private fun detectHighRiskApp(
        packageName: String,
        appName: String,
        permissions: List<String>,
        isSystemApp: Boolean
    ): HighRiskAppInfo? {
        // Skip system apps for most checks
        if (isSystemApp) return null
        
        val hasCamera = permissions.any { it in CAMERA_PERMISSIONS }
        val hasMic = permissions.any { it in MICROPHONE_PERMISSIONS }
        val hasLocation = permissions.any { it in LOCATION_PERMISSIONS }
        val hasBackgroundLocation = Manifest.permission.ACCESS_BACKGROUND_LOCATION in permissions
        val hasContacts = permissions.any { it in CONTACT_PERMISSIONS }
        val hasSms = permissions.any { it in SMS_PERMISSIONS }
        val hasCalls = permissions.any { it in CALL_PERMISSIONS }
        val hasStorage = permissions.any { it in STORAGE_PERMISSIONS }
        
        // Stalkerware pattern: Camera + Mic + Location + Background
        if (hasCamera && hasMic && hasLocation && hasBackgroundLocation) {
            return HighRiskAppInfo(
                packageName = packageName,
                appName = appName,
                reason = "Has stalkerware permission pattern (camera + mic + background location)",
                riskLevel = RiskLevel.CRITICAL,
                permissions = permissions.filter { 
                    it in CAMERA_PERMISSIONS + MICROPHONE_PERMISSIONS + LOCATION_PERMISSIONS 
                }
            )
        }
        
        // Spyware pattern: SMS + Calls + Contacts
        if (hasSms && hasCalls && hasContacts) {
            return HighRiskAppInfo(
                packageName = packageName,
                appName = appName,
                reason = "Can access SMS, calls, and contacts together",
                riskLevel = RiskLevel.HIGH,
                permissions = permissions.filter { 
                    it in SMS_PERMISSIONS + CALL_PERMISSIONS + CONTACT_PERMISSIONS 
                }
            )
        }
        
        // Background location without obvious need
        if (hasBackgroundLocation && !hasCamera) {
            val lowerName = appName.lowercase()
            val needsBackgroundLocation = listOf(
                "map", "navigation", "gps", "fitness", "run", "walk", 
                "track", "find", "locate", "uber", "lyft", "delivery"
            ).any { lowerName.contains(it) }
            
            if (!needsBackgroundLocation) {
                return HighRiskAppInfo(
                    packageName = packageName,
                    appName = appName,
                    reason = "Has background location access without apparent need",
                    riskLevel = RiskLevel.MEDIUM,
                    permissions = listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                )
            }
        }
        
        return null
    }
    
    /**
     * Get apps with accessibility service enabled
     */
    private fun getAccessibilityApps(): List<PrivacyAppInfo> {
        val apps = mutableListOf<PrivacyAppInfo>()
        
        try {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return apps
            
            val services = enabledServices.split(":")
            for (service in services) {
                if (service.isBlank()) continue
                
                val packageName = service.substringBefore("/")
                if (packageName.isBlank() || packageName == context.packageName) continue
                
                try {
                    val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
                    val appName = context.packageManager.getApplicationLabel(appInfo).toString()
                    
                    apps.add(PrivacyAppInfo(
                        packageName = packageName,
                        appName = appName,
                        isSystemApp = isSystemApp(appInfo),
                        permissions = listOf("ACCESSIBILITY_SERVICE")
                    ))
                } catch (e: Exception) {
                    // Package not found
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get accessibility apps", e)
        }
        
        return apps
    }
    
    /**
     * Get apps with device admin privileges
     */
    private fun getDeviceAdminApps(): List<PrivacyAppInfo> {
        val apps = mutableListOf<PrivacyAppInfo>()
        
        try {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) 
                as android.app.admin.DevicePolicyManager
            
            val admins = dpm.activeAdmins ?: return apps
            
            for (admin in admins) {
                val packageName = admin.packageName
                if (packageName == context.packageName) continue
                
                try {
                    val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
                    val appName = context.packageManager.getApplicationLabel(appInfo).toString()
                    
                    apps.add(PrivacyAppInfo(
                        packageName = packageName,
                        appName = appName,
                        isSystemApp = isSystemApp(appInfo),
                        permissions = listOf("DEVICE_ADMIN")
                    ))
                } catch (e: Exception) {
                    // Package not found
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get device admin apps", e)
        }
        
        return apps
    }
    
    /**
     * Get apps with overlay (draw over other apps) permission
     */
    private fun getOverlayApps(packages: List<android.content.pm.PackageInfo>): List<PrivacyAppInfo> {
        val apps = mutableListOf<PrivacyAppInfo>()
        
        for (pkg in packages) {
            if (pkg.packageName == context.packageName) continue
            
            try {
                if (Settings.canDrawOverlays(context)) {
                    val permissions = pkg.requestedPermissions?.toList() ?: emptyList()
                    if (Manifest.permission.SYSTEM_ALERT_WINDOW in permissions) {
                        val appName = pkg.applicationInfo?.loadLabel(context.packageManager)?.toString() 
                            ?: pkg.packageName
                        
                        apps.add(PrivacyAppInfo(
                            packageName = pkg.packageName,
                            appName = appName,
                            isSystemApp = isSystemApp(pkg.applicationInfo),
                            permissions = listOf("SYSTEM_ALERT_WINDOW")
                        ))
                    }
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
        
        return apps
    }
    
    /**
     * Get apps exempt from battery optimization
     */
    private fun getBatteryExemptApps(packages: List<android.content.pm.PackageInfo>): List<PrivacyAppInfo> {
        val apps = mutableListOf<PrivacyAppInfo>()
        
        try {
            val pm = context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
            
            for (pkg in packages) {
                if (pkg.packageName == context.packageName) continue
                
                if (pm.isIgnoringBatteryOptimizations(pkg.packageName)) {
                    val appName = pkg.applicationInfo?.loadLabel(context.packageManager)?.toString() 
                        ?: pkg.packageName
                    
                    apps.add(PrivacyAppInfo(
                        packageName = pkg.packageName,
                        appName = appName,
                        isSystemApp = isSystemApp(pkg.applicationInfo),
                        permissions = listOf("BATTERY_OPTIMIZATION_EXEMPT")
                    ))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get battery exempt apps", e)
        }
        
        return apps.filter { !it.isSystemApp }
    }
    
    /**
     * Check if app is system app
     */
    private fun isSystemApp(appInfo: ApplicationInfo?): Boolean {
        if (appInfo == null) return false
        return (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0 ||
               (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
    }
    
    /**
     * Calculate privacy score (0-100, lower is better)
     */
    private fun calculatePrivacyScore(
        backgroundLocationCount: Int,
        accessibilityCount: Int,
        deviceAdminCount: Int,
        overlayCount: Int,
        highRiskCount: Int
    ): Int {
        var score = 0
        
        // Background location apps (10 points each, max 30)
        score += (backgroundLocationCount * 10).coerceAtMost(30)
        
        // Accessibility apps (15 points each, max 30)
        score += (accessibilityCount * 15).coerceAtMost(30)
        
        // Device admin apps (10 points each, max 20)
        score += (deviceAdminCount * 10).coerceAtMost(20)
        
        // Overlay apps (5 points each, max 10)
        score += (overlayCount * 5).coerceAtMost(10)
        
        // High risk apps (10 points each)
        score += highRiskCount * 10
        
        return score.coerceAtMost(100)
    }
    
    /**
     * Quick privacy check
     */
    suspend fun quickCheck(): QuickPrivacyCheck = withContext(Dispatchers.IO) {
        val accessibilityApps = getAccessibilityApps().filter { !it.isSystemApp }
        val deviceAdminApps = getDeviceAdminApps().filter { !it.isSystemApp }
        
        QuickPrivacyCheck(
            thirdPartyAccessibilityApps = accessibilityApps.size,
            thirdPartyDeviceAdmins = deviceAdminApps.size,
            hasPrivacyConcerns = accessibilityApps.isNotEmpty() || deviceAdminApps.size > 1
        )
    }
    
    data class QuickPrivacyCheck(
        val thirdPartyAccessibilityApps: Int,
        val thirdPartyDeviceAdmins: Int,
        val hasPrivacyConcerns: Boolean
    )
}
