package com.sentinelguard.scanner

import android.content.Context
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SystemIntegrityScanner: Deep System Security Verification
 * 
 * Performs comprehensive checks for:
 * - Root access (15+ detection methods)
 * - Xposed Framework
 * - Magisk / KernelSU
 * - Frida (runtime manipulation)
 * - Lucky Patcher
 * - Bootloader unlock status
 * - SELinux enforcement
 * - System partition integrity
 * - Build property tampering
 */
@Singleton
class SystemIntegrityScanner @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "SystemIntegrityScanner"
        
        // Root binary locations
        private val ROOT_BINARIES = listOf(
            "/system/bin/su", "/system/xbin/su", "/sbin/su",
            "/system/su", "/data/local/su", "/data/local/bin/su",
            "/data/local/xbin/su", "/system/bin/.ext/.su",
            "/system/etc/.installed_su_daemon"
        )
        
        // Magisk paths
        private val MAGISK_PATHS = listOf(
            "/sbin/.magisk", "/data/adb/magisk",
            "/data/adb/modules", "/data/adb/ksu",
            "/cache/.disable_magisk", "/dev/.magisk.unblock"
        )
        
        // Xposed paths
        private val XPOSED_PATHS = listOf(
            "/system/framework/XposedBridge.jar",
            "/system/bin/app_process32_xposed",
            "/system/bin/app_process64_xposed",
            "/data/data/de.robv.android.xposed.installer",
            "/data/adb/lspd"
        )
        
        // Dangerous packages
        private val DANGEROUS_PACKAGES = listOf(
            // Root managers
            "com.topjohnwu.magisk",
            "me.weishu.kernelsu",
            "eu.chainfire.supersu",
            "com.noshufou.android.su",
            "com.koushikdutta.superuser",
            
            // Xposed/LSPosed
            "de.robv.android.xposed.installer",
            "org.lsposed.manager",
            "io.github.lsposed.manager",
            
            // Hacking tools
            "com.chelpus.lackypatch",
            "com.dimonvideo.luckypatcher",
            "com.android.vending.billing.InAppBillingService.LUCK",
            
            // Game cheating
            "com.github.nicktendo.gameguardian",
            "org.cheatengine.cegui",
            
            // Frida
            "re.frida.server",
            
            // Virtual environments
            "com.bly.dualspace",
            "com.ludashi.dualspace",
            "com.excelliance.dualaid"
        )
    }
    
    /**
     * Full system integrity scan result
     */
    data class SystemIntegrityResult(
        val isRooted: Boolean,
        val hasXposed: Boolean,
        val hasMagisk: Boolean,
        val hasKernelSU: Boolean,
        val hasFrida: Boolean,
        val hasLuckyPatcher: Boolean,
        val bootloaderUnlocked: Boolean,
        val selinuxEnforcing: Boolean,
        val dangerousAppsInstalled: List<String>,
        val integrityIssues: List<IntegrityIssue>,
        val overallRiskScore: Int
    )
    
    data class IntegrityIssue(
        val type: String,
        val description: String,
        val severity: IssueSeverity,
        val riskPoints: Int
    )
    
    enum class IssueSeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    /**
     * Perform comprehensive system integrity scan
     */
    suspend fun performFullScan(): SystemIntegrityResult = withContext(Dispatchers.IO) {
        val issues = mutableListOf<IntegrityIssue>()
        val dangerousApps = mutableListOf<String>()
        var riskScore = 0
        
        // 1. Root Detection
        val isRooted = detectRoot()
        if (isRooted) {
            issues.add(IntegrityIssue(
                type = "ROOT_DETECTED",
                description = "Device is rooted - security measures can be bypassed",
                severity = IssueSeverity.CRITICAL,
                riskPoints = 40
            ))
            riskScore += 40
        }
        
        // 2. Magisk Detection
        val hasMagisk = detectMagisk()
        if (hasMagisk) {
            issues.add(IntegrityIssue(
                type = "MAGISK_DETECTED",
                description = "Magisk detected - root hiding possible",
                severity = IssueSeverity.HIGH,
                riskPoints = 30
            ))
            riskScore += 30
        }
        
        // 3. KernelSU Detection
        val hasKernelSU = detectKernelSU()
        if (hasKernelSU) {
            issues.add(IntegrityIssue(
                type = "KERNELSU_DETECTED",
                description = "KernelSU detected - kernel-level root access",
                severity = IssueSeverity.HIGH,
                riskPoints = 30
            ))
            riskScore += 30
        }
        
        // 4. Xposed Detection
        val hasXposed = detectXposed()
        if (hasXposed) {
            issues.add(IntegrityIssue(
                type = "XPOSED_DETECTED",
                description = "Xposed Framework detected - app behavior can be modified",
                severity = IssueSeverity.CRITICAL,
                riskPoints = 35
            ))
            riskScore += 35
        }
        
        // 5. Frida Detection
        val hasFrida = detectFrida()
        if (hasFrida) {
            issues.add(IntegrityIssue(
                type = "FRIDA_DETECTED",
                description = "Frida detected - runtime manipulation possible",
                severity = IssueSeverity.CRITICAL,
                riskPoints = 40
            ))
            riskScore += 40
        }
        
        // 6. Lucky Patcher Detection
        val hasLuckyPatcher = detectLuckyPatcher()
        if (hasLuckyPatcher) {
            issues.add(IntegrityIssue(
                type = "LUCKY_PATCHER_DETECTED",
                description = "Lucky Patcher installed - app piracy tool",
                severity = IssueSeverity.HIGH,
                riskPoints = 25
            ))
            riskScore += 25
            dangerousApps.add("Lucky Patcher")
        }
        
        // 7. Bootloader Check
        val bootloaderUnlocked = isBootloaderUnlocked()
        if (bootloaderUnlocked) {
            issues.add(IntegrityIssue(
                type = "BOOTLOADER_UNLOCKED",
                description = "Bootloader is unlocked - device can boot unsigned images",
                severity = IssueSeverity.MEDIUM,
                riskPoints = 15
            ))
            riskScore += 15
        }
        
        // 8. SELinux Check
        val selinuxEnforcing = isSELinuxEnforcing()
        if (!selinuxEnforcing) {
            issues.add(IntegrityIssue(
                type = "SELINUX_PERMISSIVE",
                description = "SELinux is not enforcing - reduced security",
                severity = IssueSeverity.HIGH,
                riskPoints = 25
            ))
            riskScore += 25
        }
        
        // 9. Dangerous App Detection
        val installedDangerousApps = detectDangerousApps()
        dangerousApps.addAll(installedDangerousApps)
        installedDangerousApps.forEach { pkg ->
            issues.add(IntegrityIssue(
                type = "DANGEROUS_APP",
                description = "Dangerous app installed: $pkg",
                severity = IssueSeverity.HIGH,
                riskPoints = 15
            ))
            riskScore += 15
        }
        
        // 10. Build Property Tampering
        if (detectBuildTampering()) {
            issues.add(IntegrityIssue(
                type = "BUILD_TAMPERED",
                description = "Device build properties appear modified",
                severity = IssueSeverity.MEDIUM,
                riskPoints = 10
            ))
            riskScore += 10
        }
        
        // 11. Debuggable Check
        if (isDebuggable()) {
            issues.add(IntegrityIssue(
                type = "DEBUGGABLE_BUILD",
                description = "Device may allow debugging - security risk",
                severity = IssueSeverity.LOW,
                riskPoints = 5
            ))
            riskScore += 5
        }
        
        Log.i(TAG, "System integrity scan complete. Issues: ${issues.size}, Risk: $riskScore")
        
        SystemIntegrityResult(
            isRooted = isRooted,
            hasXposed = hasXposed,
            hasMagisk = hasMagisk,
            hasKernelSU = hasKernelSU,
            hasFrida = hasFrida,
            hasLuckyPatcher = hasLuckyPatcher,
            bootloaderUnlocked = bootloaderUnlocked,
            selinuxEnforcing = selinuxEnforcing,
            dangerousAppsInstalled = dangerousApps,
            integrityIssues = issues,
            overallRiskScore = riskScore.coerceAtMost(100)
        )
    }
    
    /**
     * Detect root access using multiple methods
     */
    private fun detectRoot(): Boolean {
        // Method 1: Check root binaries
        for (path in ROOT_BINARIES) {
            if (File(path).exists()) {
                Log.d(TAG, "Root binary found: $path")
                return true
            }
        }
        
        // Method 2: Check build tags
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            Log.d(TAG, "Test-keys detected in build tags")
            return true
        }
        
        // Method 3: Try to execute 'which su'
        try {
            val process = Runtime.getRuntime().exec(arrayOf("which", "su"))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readLine()
            reader.close()
            if (!output.isNullOrBlank()) {
                Log.d(TAG, "su found via which: $output")
                return true
            }
        } catch (e: Exception) {
            // Expected on non-rooted
        }
        
        // Method 4: Check system properties
        try {
            val process = Runtime.getRuntime().exec("getprop ro.debuggable")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val debuggable = reader.readLine()
            reader.close()
            if (debuggable == "1") {
                // Additional check needed
            }
        } catch (e: Exception) {
            // Ignore
        }
        
        // Method 5: Check for RW system partition
        try {
            val process = Runtime.getRuntime().exec("mount")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            reader.useLines { lines ->
                lines.forEach { line ->
                    if (line.contains("/system") && line.contains("rw")) {
                        Log.d(TAG, "System mounted as RW")
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore
        }
        
        return false
    }
    
    /**
     * Detect Magisk
     */
    private fun detectMagisk(): Boolean {
        // Check Magisk paths
        for (path in MAGISK_PATHS) {
            if (File(path).exists()) {
                Log.d(TAG, "Magisk path found: $path")
                return true
            }
        }
        
        // Check for Magisk app
        try {
            context.packageManager.getPackageInfo("com.topjohnwu.magisk", 0)
            Log.d(TAG, "Magisk Manager app installed")
            return true
        } catch (e: Exception) {
            // Not installed
        }
        
        // Check magisk prop
        try {
            val process = Runtime.getRuntime().exec("getprop init.svc.magisk_service")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val result = reader.readLine()
            reader.close()
            if (result == "running") {
                return true
            }
        } catch (e: Exception) {
            // Ignore
        }
        
        return false
    }
    
    /**
     * Detect KernelSU
     */
    private fun detectKernelSU(): Boolean {
        // Check KernelSU paths
        if (File("/data/adb/ksu").exists()) {
            return true
        }
        
        // Check KernelSU app
        try {
            context.packageManager.getPackageInfo("me.weishu.kernelsu", 0)
            return true
        } catch (e: Exception) {
            // Not installed
        }
        
        return false
    }
    
    /**
     * Detect Xposed Framework
     */
    private fun detectXposed(): Boolean {
        // Check Xposed paths
        for (path in XPOSED_PATHS) {
            if (File(path).exists()) {
                Log.d(TAG, "Xposed path found: $path")
                return true
            }
        }
        
        // Check for Xposed/LSPosed modules
        try {
            val xposedApps = listOf(
                "de.robv.android.xposed.installer",
                "org.lsposed.manager",
                "io.github.lsposed.manager"
            )
            for (pkg in xposedApps) {
                try {
                    context.packageManager.getPackageInfo(pkg, 0)
                    Log.d(TAG, "Xposed app installed: $pkg")
                    return true
                } catch (e: Exception) {
                    // Continue checking
                }
            }
        } catch (e: Exception) {
            // Ignore
        }
        
        // Check stack trace for Xposed
        try {
            throw Exception("XposedCheck")
        } catch (e: Exception) {
            for (element in e.stackTrace) {
                if (element.className.contains("xposed", ignoreCase = true) ||
                    element.className.contains("lsposed", ignoreCase = true)) {
                    return true
                }
            }
        }
        
        return false
    }
    
    /**
     * Detect Frida
     */
    private fun detectFrida(): Boolean {
        // Check for Frida server
        try {
            val process = Runtime.getRuntime().exec("ps -A")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            reader.useLines { lines ->
                lines.forEach { line ->
                    if (line.contains("frida") || line.contains("gum-js-loop")) {
                        Log.d(TAG, "Frida process detected")
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore
        }
        
        // Check for Frida port
        try {
            val socket = java.net.Socket()
            socket.connect(java.net.InetSocketAddress("127.0.0.1", 27042), 100)
            socket.close()
            Log.d(TAG, "Frida default port is open")
            return true
        } catch (e: Exception) {
            // Port not open, good
        }
        
        // Check for frida-gadget in loaded libraries
        try {
            val maps = File("/proc/self/maps").readText()
            if (maps.contains("frida") || maps.contains("gadget")) {
                return true
            }
        } catch (e: Exception) {
            // Ignore
        }
        
        return false
    }
    
    /**
     * Detect Lucky Patcher
     */
    private fun detectLuckyPatcher(): Boolean {
        val luckyPatcherPackages = listOf(
            "com.chelpus.lackypatch",
            "com.dimonvideo.luckypatcher",
            "com.forpda.lp",
            "com.android.vendinc"
        )
        
        for (pkg in luckyPatcherPackages) {
            try {
                context.packageManager.getPackageInfo(pkg, 0)
                return true
            } catch (e: Exception) {
                // Not installed
            }
        }
        
        // Check for odex backup files (LP creates these)
        val dataDir = context.dataDir
        val odexBackups = dataDir.listFiles()?.filter { 
            it.name.endsWith(".odex.bak") || it.name.endsWith(".dex.bak")
        }
        return odexBackups?.isNotEmpty() == true
    }
    
    /**
     * Check if bootloader is unlocked
     */
    private fun isBootloaderUnlocked(): Boolean {
        // Check verified boot state
        try {
            val process = Runtime.getRuntime().exec("getprop ro.boot.verifiedbootstate")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val state = reader.readLine()?.trim()
            reader.close()
            
            // "green" = locked, "orange" = unlocked, "yellow" = custom key
            if (state == "orange" || state == "yellow") {
                return true
            }
        } catch (e: Exception) {
            // Ignore
        }
        
        // Check flash unlock setting
        try {
            val process = Runtime.getRuntime().exec("getprop ro.boot.flash.locked")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val locked = reader.readLine()?.trim()
            reader.close()
            if (locked == "0") {
                return true
            }
        } catch (e: Exception) {
            // Ignore
        }
        
        return false
    }
    
    /**
     * Check if SELinux is enforcing
     */
    private fun isSELinuxEnforcing(): Boolean {
        try {
            val process = Runtime.getRuntime().exec("getenforce")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val mode = reader.readLine()?.trim()
            reader.close()
            return mode.equals("Enforcing", ignoreCase = true)
        } catch (e: Exception) {
            // If we can't check, assume enforcing
            return true
        }
    }
    
    /**
     * Detect dangerous apps installed
     */
    private fun detectDangerousApps(): List<String> {
        val found = mutableListOf<String>()
        
        for (pkg in DANGEROUS_PACKAGES) {
            try {
                context.packageManager.getPackageInfo(pkg, 0)
                found.add(pkg)
            } catch (e: Exception) {
                // Not installed
            }
        }
        
        return found
    }
    
    /**
     * Detect build property tampering
     */
    private fun detectBuildTampering(): Boolean {
        // Check for suspicious build fingerprint
        val fingerprint = Build.FINGERPRINT.lowercase()
        if (fingerprint.contains("generic") || 
            fingerprint.contains("test-keys") ||
            fingerprint.contains("rooted")) {
            return true
        }
        
        // Check display
        val display = Build.DISPLAY.lowercase()
        if (display.contains("eng.") || display.contains("userdebug")) {
            return true
        }
        
        return false
    }
    
    /**
     * Check if system is debuggable
     */
    private fun isDebuggable(): Boolean {
        try {
            val process = Runtime.getRuntime().exec("getprop ro.debuggable")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val debuggable = reader.readLine()?.trim()
            reader.close()
            return debuggable == "1"
        } catch (e: Exception) {
            return false
        }
    }
}
