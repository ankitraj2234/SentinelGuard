package com.sentinelguard.crash

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CrashHandler: Captures uncaught exceptions and saves detailed crash logs
 * to the Downloads folder for debugging.
 * 
 * Features:
 * - Captures all uncaught exceptions
 * - Saves detailed device info, stack trace, and app state
 * - Works on Android 10+ (Scoped Storage) and older versions
 * - Automatically saves to Downloads/SentinelGuard_Crash_[timestamp].txt
 */
@Singleton
class CrashHandler @Inject constructor(
    @ApplicationContext private val context: Context
) : Thread.UncaughtExceptionHandler {
    
    private var defaultHandler: Thread.UncaughtExceptionHandler? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
    private val logDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    
    companion object {
        private const val TAG = "CrashHandler"
        private const val FILE_PREFIX = "SentinelGuard_Crash_"
        private const val FILE_EXTENSION = ".txt"
    }
    
    /**
     * Initialize the crash handler - call this in Application.onCreate()
     */
    fun initialize() {
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        android.util.Log.i(TAG, "CrashHandler initialized")
    }
    
    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            // Generate crash report
            val crashReport = generateCrashReport(thread, throwable)
            
            // Save to Downloads folder
            saveCrashReport(crashReport)
            
            android.util.Log.e(TAG, "Crash report saved to Downloads folder")
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to save crash report", e)
        } finally {
            // Call the default handler to show crash dialog / kill app
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
    
    /**
     * Generate a comprehensive crash report
     */
    private fun generateCrashReport(thread: Thread, throwable: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        val stackTrace = sw.toString()
        
        return buildString {
            appendLine("=" .repeat(60))
            appendLine("SENTINELGUARD CRASH REPORT")
            appendLine("=" .repeat(60))
            appendLine()
            
            // Timestamp
            appendLine("📅 Crash Time: ${logDateFormat.format(Date())}")
            appendLine()
            
            // App Info
            appendLine("─".repeat(40))
            appendLine("📱 APP INFORMATION")
            appendLine("─".repeat(40))
            try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                appendLine("App Version: ${packageInfo.versionName}")
                appendLine("Version Code: ${if (Build.VERSION.SDK_INT >= 28) packageInfo.longVersionCode else packageInfo.versionCode}")
                appendLine("Package: ${context.packageName}")
            } catch (e: Exception) {
                appendLine("App Info: Unable to retrieve")
            }
            appendLine()
            
            // Device Info
            appendLine("─".repeat(40))
            appendLine("📱 DEVICE INFORMATION")
            appendLine("─".repeat(40))
            appendLine("Manufacturer: ${Build.MANUFACTURER}")
            appendLine("Model: ${Build.MODEL}")
            appendLine("Device: ${Build.DEVICE}")
            appendLine("Brand: ${Build.BRAND}")
            appendLine("Android Version: ${Build.VERSION.RELEASE}")
            appendLine("SDK Level: ${Build.VERSION.SDK_INT}")
            appendLine("Build ID: ${Build.ID}")
            appendLine("Hardware: ${Build.HARDWARE}")
            appendLine("Product: ${Build.PRODUCT}")
            appendLine()
            
            // Memory Info
            appendLine("─".repeat(40))
            appendLine("💾 MEMORY INFORMATION")
            appendLine("─".repeat(40))
            val runtime = Runtime.getRuntime()
            val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
            val freeMemory = runtime.freeMemory() / 1024 / 1024
            val totalMemory = runtime.totalMemory() / 1024 / 1024
            val maxMemory = runtime.maxMemory() / 1024 / 1024
            appendLine("Used Memory: ${usedMemory}MB")
            appendLine("Free Memory: ${freeMemory}MB")
            appendLine("Total Memory: ${totalMemory}MB")
            appendLine("Max Memory: ${maxMemory}MB")
            appendLine()
            
            // Thread Info
            appendLine("─".repeat(40))
            appendLine("🧵 THREAD INFORMATION")
            appendLine("─".repeat(40))
            appendLine("Thread Name: ${thread.name}")
            appendLine("Thread ID: ${thread.id}")
            appendLine("Thread Priority: ${thread.priority}")
            appendLine("Thread State: ${thread.state}")
            appendLine("Is Daemon: ${thread.isDaemon}")
            appendLine()
            
            // Exception Info
            appendLine("─".repeat(40))
            appendLine("❌ EXCEPTION DETAILS")
            appendLine("─".repeat(40))
            appendLine("Exception Type: ${throwable.javaClass.name}")
            appendLine("Message: ${throwable.message ?: "No message"}")
            appendLine()
            
            // Cause Chain
            var cause = throwable.cause
            var causeLevel = 1
            while (cause != null) {
                appendLine("Cause #$causeLevel: ${cause.javaClass.name}")
                appendLine("Message: ${cause.message ?: "No message"}")
                cause = cause.cause
                causeLevel++
            }
            appendLine()
            
            // Full Stack Trace
            appendLine("─".repeat(40))
            appendLine("📋 FULL STACK TRACE")
            appendLine("─".repeat(40))
            appendLine(stackTrace)
            appendLine()
            
            // All Thread States
            appendLine("─".repeat(40))
            appendLine("🧵 ALL THREADS")
            appendLine("─".repeat(40))
            Thread.getAllStackTraces().forEach { (t, stack) ->
                appendLine("Thread: ${t.name} (${t.state})")
                stack.take(5).forEach { element ->
                    appendLine("    at $element")
                }
                if (stack.size > 5) {
                    appendLine("    ... ${stack.size - 5} more")
                }
                appendLine()
            }
            
            appendLine("=" .repeat(60))
            appendLine("END OF CRASH REPORT")
            appendLine("=" .repeat(60))
        }
    }
    
    /**
     * Save crash report to Downloads folder
     * Supports both legacy storage and Scoped Storage (Android 10+)
     */
    private fun saveCrashReport(report: String) {
        val fileName = "$FILE_PREFIX${dateFormat.format(Date())}$FILE_EXTENSION"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ using MediaStore
            saveUsingMediaStore(fileName, report)
        } else {
            // Legacy storage
            saveUsingLegacyStorage(fileName, report)
        }
    }
    
    /**
     * Save using MediaStore API (Android 10+)
     */
    private fun saveUsingMediaStore(fileName: String, content: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "text/plain")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(content.toByteArray())
            }
            
            // Mark as complete
            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
            
            android.util.Log.i(TAG, "Crash report saved: $fileName")
        }
    }
    
    /**
     * Save using legacy storage (Android 9 and below)
     */
    @Suppress("DEPRECATION")
    private fun saveUsingLegacyStorage(fileName: String, content: String) {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        
        FileOutputStream(file).use { fos ->
            fos.write(content.toByteArray())
        }
        
        android.util.Log.i(TAG, "Crash report saved: ${file.absolutePath}")
    }
    
    /**
     * Manually trigger a test crash (for testing purposes only)
     */
    fun triggerTestCrash() {
        throw RuntimeException("Test crash triggered by CrashHandler")
    }
}
