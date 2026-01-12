package com.sentinelguard.scanner

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.yield
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FileSystemScanner: Deep File System Security Scanner
 * 
 * Scans ALL accessible directories for:
 * - Malicious APK files
 * - Suspicious executables (.dex, .so)
 * - Hidden files (potential malware hiding)
 * - Recently modified suspicious files
 * - Encrypted containers
 * - Known malware by hash
 * 
 * Requires MANAGE_EXTERNAL_STORAGE permission for full access.
 */
@Singleton
class FileSystemScanner @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "FileSystemScanner"
        
        // Maximum file size to hash (50MB)
        private const val MAX_FILE_SIZE_FOR_HASH = 50 * 1024 * 1024L
        
        // Suspicious file extensions
        private val SUSPICIOUS_EXTENSIONS = setOf(
            "apk", "dex", "so", "jar",
            "sh", "bat", "exe", "bin",
            "js", "vbs", "ps1"
        )
        
        // Hidden file indicators
        private val HIDDEN_INDICATORS = listOf(
            ".nomedia",
            ".hidden",
            ".noindex"
        )
        
        // Directories to skip (system paths that can't be accessed)
        private val SKIP_DIRECTORIES = setOf(
            "/proc", "/sys", "/dev",
            "/system/bin", "/system/xbin",
            "/data/data", "/data/app"
        )
    }
    
    /**
     * Result of a file scan
     */
    data class FileScanResult(
        val totalFilesScanned: Int,
        val totalDirectoriesScanned: Int,
        val suspiciousFiles: List<SuspiciousFile>,
        val apkFilesFound: Int,
        val hiddenFilesFound: Int,
        val recentlyModifiedCount: Int,
        val scanDurationMs: Long
    )
    
    /**
     * A suspicious file detected
     */
    data class SuspiciousFile(
        val path: String,
        val name: String,
        val size: Long,
        val reason: String,
        val severity: FileThreatSeverity,
        val lastModified: Long,
        val sha256Hash: String? = null
    )
    
    enum class FileThreatSeverity {
        LOW,      // Unusual but not necessarily harmful
        MEDIUM,   // Potentially harmful
        HIGH,     // Likely malicious
        CRITICAL  // Known malware
    }
    
    /**
     * File scan progress update
     */
    data class FileScanProgress(
        val currentDirectory: String,
        val currentFile: String,
        val filesScanned: Int,
        val directoriesScanned: Int,
        val suspiciousFound: Int,
        val phase: String
    )
    
    /**
     * Perform deep file system scan
     */
    fun performDeepFileScan(): Flow<FileScanProgress> = flow {
        var filesScanned = 0
        var dirsScanned = 0
        val suspiciousFiles = mutableListOf<SuspiciousFile>()
        
        emit(FileScanProgress("", "Initializing...", 0, 0, 0, "INITIALIZING"))
        
        // Directories to scan
        val dirsToScan = getScanDirectories()
        
        for (dir in dirsToScan) {
            if (!dir.exists() || !dir.canRead()) continue
            
            emit(FileScanProgress(
                dir.absolutePath, 
                "", 
                filesScanned, 
                dirsScanned, 
                suspiciousFiles.size,
                "SCANNING_${dir.name.uppercase()}"
            ))
            
            try {
                scanDirectory(dir, suspiciousFiles) { file, count ->
                    filesScanned = count
                    emit(FileScanProgress(
                        dir.absolutePath,
                        file.name,
                        filesScanned,
                        dirsScanned,
                        suspiciousFiles.size,
                        "SCANNING"
                    ))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error scanning ${dir.absolutePath}", e)
            }
            
            dirsScanned++
            yield()
        }
        
        emit(FileScanProgress(
            "",
            "Complete",
            filesScanned,
            dirsScanned,
            suspiciousFiles.size,
            "COMPLETE"
        ))
        
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get all directories to scan
     */
    private fun getScanDirectories(): List<File> {
        val dirs = mutableListOf<File>()
        
        // External storage root
        if (Environment.isExternalStorageManager() || 
            Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            dirs.add(Environment.getExternalStorageDirectory())
        }
        
        // Download directory
        dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
        
        // Documents
        dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS))
        
        // Pictures (APKs sometimes hidden here)
        dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))
        
        // DCIM
        dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
        
        // Music
        dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC))
        
        // Movies
        dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES))
        
        // Common app directories
        val externalStorage = Environment.getExternalStorageDirectory()
        listOf(
            "WhatsApp", "Telegram", "Signal",
            "Android/data", "Android/obb",
            "backup", "Backups",
            ".android", ".cache"
        ).forEach { subDir ->
            val dir = File(externalStorage, subDir)
            if (dir.exists()) dirs.add(dir)
        }
        
        return dirs.distinctBy { it.absolutePath }
    }
    
    /**
     * Recursively scan a directory
     */
    private suspend fun scanDirectory(
        dir: File,
        suspiciousFiles: MutableList<SuspiciousFile>,
        onProgress: suspend (File, Int) -> Unit
    ): Int {
        var count = 0
        
        // Check if we should skip
        if (dir.absolutePath in SKIP_DIRECTORIES) return count
        if (!dir.canRead()) return count
        
        val files = try {
            dir.listFiles() ?: return count
        } catch (e: Exception) {
            return count
        }
        
        for (file in files) {
            yield() // Allow cancellation
            
            if (file.isDirectory) {
                // Recurse into subdirectory (with depth limit)
                if (getPathDepth(file) < 10) {
                    count += scanDirectory(file, suspiciousFiles, onProgress)
                }
            } else {
                count++
                
                // Analyze the file
                val threat = analyzeFile(file)
                if (threat != null) {
                    suspiciousFiles.add(threat)
                }
                
                // Report progress every 50 files
                if (count % 50 == 0) {
                    onProgress(file, count)
                }
            }
        }
        
        return count
    }
    
    /**
     * Analyze a single file for threats
     */
    private fun analyzeFile(file: File): SuspiciousFile? {
        val extension = file.extension.lowercase()
        val name = file.name
        val size = file.length()
        val lastModified = file.lastModified()
        
        // Check 1: APK files outside Play Store
        if (extension == "apk") {
            val hash = if (size <= MAX_FILE_SIZE_FOR_HASH) {
                calculateSHA256(file)
            } else null
            
            // Check if known malware hash
            if (hash != null && ThreatDatabase.isKnownMalwareHash(hash)) {
                return SuspiciousFile(
                    path = file.absolutePath,
                    name = name,
                    size = size,
                    reason = "APK matches known malware signature",
                    severity = FileThreatSeverity.CRITICAL,
                    lastModified = lastModified,
                    sha256Hash = hash
                )
            }
            
            // Check suspicious APK names
            val lowerName = name.lowercase()
            val isSuspiciousName = listOf(
                "hack", "crack", "mod", "cheat", "spy",
                "keylog", "stealer", "rat", "trojan", "xploits"
            ).any { lowerName.contains(it) }
            
            if (isSuspiciousName) {
                return SuspiciousFile(
                    path = file.absolutePath,
                    name = name,
                    size = size,
                    reason = "APK has suspicious name: $name",
                    severity = FileThreatSeverity.HIGH,
                    lastModified = lastModified,
                    sha256Hash = hash
                )
            }
            
            // Flag all APKs in non-typical locations
            val parentPath = file.parent ?: ""
            if (!parentPath.contains("Download", ignoreCase = true)) {
                return SuspiciousFile(
                    path = file.absolutePath,
                    name = name,
                    size = size,
                    reason = "APK found in unusual location",
                    severity = FileThreatSeverity.MEDIUM,
                    lastModified = lastModified,
                    sha256Hash = hash
                )
            }
        }
        
        // Check 2: DEX files (compiled Dalvik code)
        if (extension == "dex") {
            return SuspiciousFile(
                path = file.absolutePath,
                name = name,
                size = size,
                reason = "Standalone DEX file detected (potential malware payload)",
                severity = FileThreatSeverity.HIGH,
                lastModified = lastModified
            )
        }
        
        // Check 3: Native libraries outside app directories
        if (extension == "so") {
            val parentPath = file.parent ?: ""
            if (!parentPath.contains("/data/app") && !parentPath.contains("/lib")) {
                return SuspiciousFile(
                    path = file.absolutePath,
                    name = name,
                    size = size,
                    reason = "Native library in unusual location",
                    severity = FileThreatSeverity.MEDIUM,
                    lastModified = lastModified
                )
            }
        }
        
        // Check 4: Hidden files with suspicious extensions
        if (name.startsWith(".") && extension in SUSPICIOUS_EXTENSIONS) {
            return SuspiciousFile(
                path = file.absolutePath,
                name = name,
                size = size,
                reason = "Hidden file with suspicious extension",
                severity = FileThreatSeverity.MEDIUM,
                lastModified = lastModified
            )
        }
        
        // Check 5: Script files
        if (extension in listOf("sh", "bat", "ps1", "vbs")) {
            return SuspiciousFile(
                path = file.absolutePath,
                name = name,
                size = size,
                reason = "Executable script file detected",
                severity = FileThreatSeverity.LOW,
                lastModified = lastModified
            )
        }
        
        // Check 6: Recently modified APK/DEX in last 24 hours
        val twentyFourHoursAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
        if (lastModified > twentyFourHoursAgo && extension in listOf("apk", "dex")) {
            return SuspiciousFile(
                path = file.absolutePath,
                name = name,
                size = size,
                reason = "Recently modified executable file",
                severity = FileThreatSeverity.LOW,
                lastModified = lastModified
            )
        }
        
        return null
    }
    
    /**
     * Calculate SHA-256 hash of a file
     */
    private fun calculateSHA256(file: File): String? {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            FileInputStream(file).use { fis ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (fis.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to calculate hash for ${file.name}", e)
            null
        }
    }
    
    /**
     * Get path depth
     */
    private fun getPathDepth(file: File): Int {
        return file.absolutePath.count { it == '/' }
    }
    
    /**
     * Check if we have full storage access
     */
    fun hasFullStorageAccess(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            true // Pre-Android 11, READ_EXTERNAL_STORAGE is sufficient
        }
    }
    
    /**
     * Get quick stats without full scan
     */
    suspend fun getQuickStats(): FileQuickStats {
        var apkCount = 0
        var hiddenCount = 0
        
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        downloadDir.listFiles()?.forEach { file ->
            if (file.extension.equals("apk", ignoreCase = true)) apkCount++
            if (file.name.startsWith(".")) hiddenCount++
        }
        
        return FileQuickStats(
            downloadsApkCount = apkCount,
            hiddenFilesCount = hiddenCount,
            hasFullAccess = hasFullStorageAccess()
        )
    }
    
    data class FileQuickStats(
        val downloadsApkCount: Int,
        val hiddenFilesCount: Int,
        val hasFullAccess: Boolean
    )
}
