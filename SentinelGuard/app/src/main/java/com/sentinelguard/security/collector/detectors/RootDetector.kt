package com.sentinelguard.security.collector.detectors

import android.os.Build
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Detects if the device is rooted.
 * 
 * Uses multiple detection methods:
 * - Common root binary checks
 * - System properties
 * - Build tags
 * - Write access to system paths
 * 
 * Limitation: Magisk Hide and similar tools can bypass these checks.
 * This is documented in the app's limitations section.
 */
class RootDetector {

    private val detectedReasons = mutableListOf<String>()

    companion object {
        private val ROOT_BINARIES = listOf(
            "su",
            "busybox",
            "supersu",
            "magisk"
        )

        private val ROOT_PATHS = listOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su",
            "/system/bin/.ext/.su",
            "/system/usr/we-need-root/su-backup",
            "/system/xbin/mu",
            // Magisk paths
            "/sbin/.magisk",
            "/data/adb/magisk",
            "/data/adb/modules"
        )

        private val ROOT_PACKAGES = listOf(
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.topjohnwu.magisk"
        )
    }

    /**
     * Returns true if root is detected.
     */
    fun isRooted(): Boolean {
        detectedReasons.clear()
        
        // Check test-keys build
        if (checkBuildTags()) return true
        
        // Check for root binaries
        if (checkRootBinaries()) return true
        
        // Check for root paths
        if (checkRootPaths()) return true
        
        // Check system properties
        if (checkSystemProperties()) return true
        
        // Try to execute su
        if (checkSuExecution()) return true
        
        return false
    }

    /**
     * Returns details about why root was detected.
     */
    fun getDetectionDetails(): String {
        return JSONObject().apply {
            put("reasons", detectedReasons.joinToString(", "))
            put("build_tags", Build.TAGS)
        }.toString()
    }

    private fun checkBuildTags(): Boolean {
        val tags = Build.TAGS
        if (tags != null && tags.contains("test-keys")) {
            detectedReasons.add("test-keys")
            return true
        }
        return false
    }

    private fun checkRootBinaries(): Boolean {
        val paths = System.getenv("PATH")?.split(":") ?: return false
        
        for (path in paths) {
            for (binary in ROOT_BINARIES) {
                val file = File(path, binary)
                if (file.exists() && file.canExecute()) {
                    detectedReasons.add("binary:$path/$binary")
                    return true
                }
            }
        }
        return false
    }

    private fun checkRootPaths(): Boolean {
        for (path in ROOT_PATHS) {
            if (File(path).exists()) {
                detectedReasons.add("path:$path")
                return true
            }
        }
        return false
    }

    private fun checkSystemProperties(): Boolean {
        // Check for common root-related system properties
        try {
            val process = Runtime.getRuntime().exec("getprop")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            
            while (reader.readLine().also { line = it } != null) {
                line?.let {
                    if (it.contains("ro.debuggable=1") ||
                        it.contains("ro.secure=0") ||
                        it.contains("ro.build.selinux=0")) {
                        detectedReasons.add("prop:${it.trim()}")
                        reader.close()
                        return true
                    }
                }
            }
            reader.close()
        } catch (e: Exception) {
            // Ignore - can't check properties
        }
        return false
    }

    private fun checkSuExecution(): Boolean {
        // Try to run 'which su' or 'su --version' to detect su binary
        try {
            val process = Runtime.getRuntime().exec(arrayOf("which", "su"))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readLine()
            reader.close()
            
            if (!output.isNullOrBlank()) {
                detectedReasons.add("which_su:$output")
                return true
            }
        } catch (e: Exception) {
            // Expected on non-rooted devices
        }
        
        return false
    }
}
