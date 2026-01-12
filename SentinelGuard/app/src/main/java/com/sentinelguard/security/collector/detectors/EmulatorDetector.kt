package com.sentinelguard.security.collector.detectors

import android.os.Build
import android.os.Debug
import org.json.JSONObject
import java.io.File

/**
 * Detects if the app is running on an emulator.
 * 
 * Uses multiple heuristics:
 * - Build properties (fingerprint, model, brand, etc.)
 * - Hardware characteristics
 * - File system checks
 * 
 * Limitation: Sophisticated emulators may bypass these checks.
 */
class EmulatorDetector {

    private val detectedReasons = mutableListOf<String>()

    /**
     * Returns true if emulator is detected.
     */
    fun isEmulator(): Boolean {
        detectedReasons.clear()
        
        // Check build properties
        if (checkBuildProperties()) return true
        
        // Check hardware
        if (checkHardware()) return true
        
        // Check files
        if (checkEmulatorFiles()) return true
        
        return false
    }

    /**
     * Returns details about why emulator was detected.
     */
    fun getDetectionDetails(): String {
        return JSONObject().apply {
            put("reasons", detectedReasons.joinToString(", "))
            put("build_fingerprint", Build.FINGERPRINT)
            put("build_model", Build.MODEL)
            put("build_brand", Build.BRAND)
            put("build_device", Build.DEVICE)
            put("build_product", Build.PRODUCT)
        }.toString()
    }

    private fun checkBuildProperties(): Boolean {
        var detected = false

        // Fingerprint checks
        val fingerprint = Build.FINGERPRINT.lowercase()
        if (fingerprint.contains("generic") || 
            fingerprint.contains("vbox") ||
            fingerprint.contains("test-keys")) {
            detectedReasons.add("fingerprint:$fingerprint")
            detected = true
        }

        // Model checks
        val model = Build.MODEL.lowercase()
        if (model.contains("emulator") ||
            model.contains("sdk") ||
            model.contains("google_sdk") ||
            model.contains("android sdk")) {
            detectedReasons.add("model:$model")
            detected = true
        }

        // Brand checks
        val brand = Build.BRAND.lowercase()
        if (brand.contains("generic") || brand.contains("google") && model.contains("sdk")) {
            detectedReasons.add("brand:$brand")
            detected = true
        }

        // Device checks
        val device = Build.DEVICE.lowercase()
        if (device.contains("generic") || 
            device.contains("emulator") ||
            device.contains("vbox")) {
            detectedReasons.add("device:$device")
            detected = true
        }

        // Product checks
        val product = Build.PRODUCT.lowercase()
        if (product.contains("sdk") ||
            product.contains("google_sdk") ||
            product.contains("emulator") ||
            product.contains("vbox")) {
            detectedReasons.add("product:$product")
            detected = true
        }

        // Manufacturer check
        val manufacturer = Build.MANUFACTURER.lowercase()
        if (manufacturer.contains("genymotion") ||
            manufacturer.contains("bluestacks")) {
            detectedReasons.add("manufacturer:$manufacturer")
            detected = true
        }

        // Hardware check
        val hardware = Build.HARDWARE.lowercase()
        if (hardware.contains("goldfish") ||
            hardware.contains("ranchu") ||
            hardware.contains("vbox")) {
            detectedReasons.add("hardware:$hardware")
            detected = true
        }

        return detected
    }

    private fun checkHardware(): Boolean {
        // Check for Goldfish (QEMU) or Ranchu hardware
        val hardware = Build.HARDWARE
        if (hardware == "goldfish" || hardware == "ranchu") {
            detectedReasons.add("qemu_hardware:$hardware")
            return true
        }

        // Check board
        val board = Build.BOARD.lowercase()
        if (board.contains("goldfish") || board.contains("unknown")) {
            detectedReasons.add("board:$board")
            return true
        }

        return false
    }

    private fun checkEmulatorFiles(): Boolean {
        val emulatorFiles = listOf(
            "/dev/socket/genyd",
            "/dev/socket/baseband_genyd",
            "/dev/socket/qemud",
            "/dev/qemu_pipe",
            "/system/lib/libc_malloc_debug_qemu.so",
            "/sys/qemu_trace",
            "/system/bin/qemu-props",
            "/system/bin/nox-prop",
            "/system/bin/microvirtd",
            "/init.goldfish.rc"
        )

        for (filePath in emulatorFiles) {
            if (File(filePath).exists()) {
                detectedReasons.add("file:$filePath")
                return true
            }
        }

        return false
    }
}
