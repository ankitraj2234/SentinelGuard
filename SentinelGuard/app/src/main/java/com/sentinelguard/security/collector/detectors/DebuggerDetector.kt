package com.sentinelguard.security.collector.detectors

import android.os.Debug
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * Detects if a debugger or analysis tool is attached.
 * 
 * Detection methods:
 * - Android Debug API
 * - TracerPid check (detects ptrace-based debuggers)
 * - Frida detection (common instrumentation tool)
 * 
 * Limitation: Sophisticated anti-detection techniques may bypass.
 */
class DebuggerDetector {

    private val detectedReasons = mutableListOf<String>()

    /**
     * Returns true if any debugger/analysis tool is detected.
     */
    fun isDebuggerAttached(): Boolean {
        detectedReasons.clear()
        
        // Check Android Debug API
        if (Debug.isDebuggerConnected()) {
            detectedReasons.add("debug_api_connected")
            return true
        }

        // Check TracerPid
        if (checkTracerPid()) {
            return true
        }

        // Check for Frida
        if (checkFrida()) {
            return true
        }

        // Check for Xposed
        if (checkXposed()) {
            return true
        }

        return false
    }

    /**
     * Returns detection details.
     */
    fun getDetectionDetails(): String {
        return JSONObject().apply {
            put("reasons", detectedReasons.joinToString(", "))
            put("debugger_connected", Debug.isDebuggerConnected())
            put("waiting_for_debugger", Debug.waitingForDebugger())
        }.toString()
    }

    /**
     * Checks TracerPid in /proc/self/status.
     * A non-zero TracerPid indicates ptrace is active.
     */
    private fun checkTracerPid(): Boolean {
        try {
            val reader = BufferedReader(FileReader("/proc/self/status"))
            var line: String?
            
            while (reader.readLine().also { line = it } != null) {
                if (line?.startsWith("TracerPid:") == true) {
                    val tracerPid = line!!.split(":")[1].trim().toIntOrNull() ?: 0
                    if (tracerPid != 0) {
                        detectedReasons.add("tracer_pid:$tracerPid")
                        reader.close()
                        return true
                    }
                    break
                }
            }
            reader.close()
        } catch (e: Exception) {
            // Ignore - can't check
        }
        return false
    }

    /**
     * Checks for Frida server or agent.
     */
    private fun checkFrida(): Boolean {
        // Check for default Frida port
        try {
            val socket = java.net.Socket()
            socket.connect(java.net.InetSocketAddress("127.0.0.1", 27042), 100)
            socket.close()
            detectedReasons.add("frida_port:27042")
            return true
        } catch (e: Exception) {
            // Port not open - good
        }

        // Check for Frida libraries in memory maps
        try {
            val reader = BufferedReader(FileReader("/proc/self/maps"))
            var line: String?
            
            while (reader.readLine().also { line = it } != null) {
                val lowerLine = line?.lowercase() ?: continue
                if (lowerLine.contains("frida") || lowerLine.contains("linjector")) {
                    detectedReasons.add("frida_lib:${line?.substringAfterLast("/")}")
                    reader.close()
                    return true
                }
            }
            reader.close()
        } catch (e: Exception) {
            // Ignore
        }

        // Check for Frida-related files
        val fridaPaths = listOf(
            "/data/local/tmp/frida-server",
            "/data/local/tmp/re.frida.server",
            "/sdcard/frida-server"
        )
        for (path in fridaPaths) {
            if (File(path).exists()) {
                detectedReasons.add("frida_file:$path")
                return true
            }
        }

        return false
    }

    /**
     * Checks for Xposed framework.
     */
    private fun checkXposed(): Boolean {
        // Check for Xposed installer
        try {
            val stackTrace = Thread.currentThread().stackTrace
            for (element in stackTrace) {
                if (element.className.contains("de.robv.android.xposed")) {
                    detectedReasons.add("xposed_stack")
                    return true
                }
            }
        } catch (e: Exception) {
            // Ignore
        }

        // Check for Xposed files
        val xposedPaths = listOf(
            "/system/framework/XposedBridge.jar",
            "/data/data/de.robv.android.xposed.installer",
            "/system/bin/app_process.orig"
        )
        for (path in xposedPaths) {
            if (File(path).exists()) {
                detectedReasons.add("xposed_file:$path")
                return true
            }
        }

        return false
    }
}
