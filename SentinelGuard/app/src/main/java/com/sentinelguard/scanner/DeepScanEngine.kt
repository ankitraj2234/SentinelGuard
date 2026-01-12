package com.sentinelguard.scanner

import android.content.Context
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DeepScanEngine: Orchestrates All Security Scanners
 * 
 * Coordinates:
 * - MalwareScanner: App malware detection
 * - FileSystemScanner: Deep file analysis
 * - SystemIntegrityScanner: Root/Xposed/Frida detection
 * - NetworkSecurityScanner: WiFi/VPN/DNS checks
 * - PrivacyScanner: Permission audit
 * 
 * Produces comprehensive security report.
 */
@Singleton
class DeepScanEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val malwareScanner: MalwareScanner,
    private val fileSystemScanner: FileSystemScanner,
    private val systemIntegrityScanner: SystemIntegrityScanner,
    private val networkSecurityScanner: NetworkSecurityScanner,
    private val privacyScanner: PrivacyScanner
) {
    companion object {
        private const val TAG = "DeepScanEngine"
    }
    
    /**
     * Deep scan result containing all scan data
     */
    data class DeepScanResult(
        val scanStartTime: Long,
        val scanEndTime: Long,
        val scanDurationMs: Long,
        
        // Individual results
        val malwareScanResult: ScanResult?,
        val fileScanResult: FileSystemScanner.FileScanResult?,
        val systemIntegrityResult: SystemIntegrityScanner.SystemIntegrityResult?,
        val networkSecurityResult: NetworkSecurityScanner.NetworkSecurityResult?,
        val privacyScanResult: PrivacyScanner.PrivacyScanResult?,
        
        // Summary
        val overallRiskScore: Int,
        val riskLevel: OverallRiskLevel,
        val criticalIssuesCount: Int,
        val highIssuesCount: Int,
        val mediumIssuesCount: Int,
        val lowIssuesCount: Int,
        val recommendations: List<SecurityRecommendation>,
        
        // Metadata
        val deviceInfo: DeviceScanInfo
    )
    
    data class SecurityRecommendation(
        val title: String,
        val description: String,
        val priority: RecommendationPriority,
        val actionType: ActionType
    )
    
    enum class RecommendationPriority {
        CRITICAL, HIGH, MEDIUM, LOW
    }
    
    enum class ActionType {
        UNINSTALL_APP,
        REVOKE_PERMISSION,
        CHANGE_SETTING,
        INVESTIGATE,
        INFORM
    }
    
    enum class OverallRiskLevel {
        SECURE,     // 0-20
        LOW,        // 21-40
        MEDIUM,     // 41-60
        HIGH,       // 61-80
        CRITICAL    // 81-100
    }
    
    data class DeviceScanInfo(
        val manufacturer: String,
        val model: String,
        val androidVersion: String,
        val apiLevel: Int,
        val appVersion: String,
        val scanTimestamp: String
    )
    
    /**
     * Deep scan progress
     */
    data class DeepScanProgress(
        val phase: DeepScanPhase,
        val phaseProgress: Float,
        val overallProgress: Float,
        val currentTask: String,
        val itemsProcessed: Int,
        val totalItems: Int
    )
    
    enum class DeepScanPhase {
        INITIALIZING,
        SYSTEM_INTEGRITY,
        APP_ANALYSIS,
        FILE_SYSTEM,
        NETWORK_SECURITY,
        PRIVACY_AUDIT,
        GENERATING_REPORT,
        COMPLETE
    }
    
    /**
     * Perform comprehensive deep scan
     */
    fun performDeepScan(): Flow<DeepScanProgress> = flow {
        val startTime = System.currentTimeMillis()
        
        var malwareResult: ScanResult? = null
        var fileResult: FileSystemScanner.FileScanResult? = null
        var systemResult: SystemIntegrityScanner.SystemIntegrityResult? = null
        var networkResult: NetworkSecurityScanner.NetworkSecurityResult? = null
        var privacyResult: PrivacyScanner.PrivacyScanResult? = null
        
        emit(DeepScanProgress(
            phase = DeepScanPhase.INITIALIZING,
            phaseProgress = 0f,
            overallProgress = 0f,
            currentTask = "Preparing security scan...",
            itemsProcessed = 0,
            totalItems = 0
        ))
        
        // Phase 1: System Integrity (10%)
        emit(DeepScanProgress(
            phase = DeepScanPhase.SYSTEM_INTEGRITY,
            phaseProgress = 0f,
            overallProgress = 0.02f,
            currentTask = "Checking system integrity...",
            itemsProcessed = 0,
            totalItems = 11
        ))
        
        try {
            systemResult = systemIntegrityScanner.performFullScan()
            Log.i(TAG, "System integrity scan complete. Risk: ${systemResult.overallRiskScore}")
        } catch (e: Exception) {
            Log.e(TAG, "System integrity scan failed", e)
        }
        
        emit(DeepScanProgress(
            phase = DeepScanPhase.SYSTEM_INTEGRITY,
            phaseProgress = 1f,
            overallProgress = 0.10f,
            currentTask = "System integrity check complete",
            itemsProcessed = 11,
            totalItems = 11
        ))
        
        // Phase 2: App Analysis (30%)
        emit(DeepScanProgress(
            phase = DeepScanPhase.APP_ANALYSIS,
            phaseProgress = 0f,
            overallProgress = 0.10f,
            currentTask = "Analyzing installed apps...",
            itemsProcessed = 0,
            totalItems = 0
        ))
        
        try {
            var lastProgress: ScanProgress? = null
            malwareScanner.performFullScan().collect { progress ->
                lastProgress = progress
                val phaseProgress = if (progress.totalCount > 0) {
                    progress.scannedCount.toFloat() / progress.totalCount
                } else 0f
                
                emit(DeepScanProgress(
                    phase = DeepScanPhase.APP_ANALYSIS,
                    phaseProgress = phaseProgress,
                    overallProgress = 0.10f + (phaseProgress * 0.20f),
                    currentTask = "Scanning: ${progress.currentItem}",
                    itemsProcessed = progress.scannedCount,
                    totalItems = progress.totalCount
                ))
            }
            
            // Get final result
            malwareResult = malwareScanner.getFinalResult(flow { })
            Log.i(TAG, "App scan complete. Threats: ${malwareResult.threats.size}")
        } catch (e: Exception) {
            Log.e(TAG, "App scan failed", e)
        }
        
        emit(DeepScanProgress(
            phase = DeepScanPhase.APP_ANALYSIS,
            phaseProgress = 1f,
            overallProgress = 0.30f,
            currentTask = "App analysis complete",
            itemsProcessed = malwareResult?.appsScanned ?: 0,
            totalItems = malwareResult?.appsScanned ?: 0
        ))
        
        // Phase 3: File System (30%)
        if (fileSystemScanner.hasFullStorageAccess()) {
            emit(DeepScanProgress(
                phase = DeepScanPhase.FILE_SYSTEM,
                phaseProgress = 0f,
                overallProgress = 0.30f,
                currentTask = "Scanning file system...",
                itemsProcessed = 0,
                totalItems = 0
            ))
            
            try {
                var filesScanned = 0
                var suspiciousFound = 0
                
                fileSystemScanner.performDeepFileScan().collect { progress ->
                    filesScanned = progress.filesScanned
                    suspiciousFound = progress.suspiciousFound
                    
                    emit(DeepScanProgress(
                        phase = DeepScanPhase.FILE_SYSTEM,
                        phaseProgress = 0.5f, // Approximate
                        overallProgress = 0.30f + (0.15f),
                        currentTask = "Scanning: ${progress.currentFile}",
                        itemsProcessed = progress.filesScanned,
                        totalItems = 0 // Unknown total
                    ))
                }
                
                fileResult = FileSystemScanner.FileScanResult(
                    totalFilesScanned = filesScanned,
                    totalDirectoriesScanned = 0,
                    suspiciousFiles = emptyList(), // Will be populated
                    apkFilesFound = 0,
                    hiddenFilesFound = 0,
                    recentlyModifiedCount = 0,
                    scanDurationMs = 0
                )
                
                Log.i(TAG, "File scan complete. Files: $filesScanned, Suspicious: $suspiciousFound")
            } catch (e: Exception) {
                Log.e(TAG, "File scan failed", e)
            }
        } else {
            emit(DeepScanProgress(
                phase = DeepScanPhase.FILE_SYSTEM,
                phaseProgress = 1f,
                overallProgress = 0.60f,
                currentTask = "File scan skipped (no permission)",
                itemsProcessed = 0,
                totalItems = 0
            ))
        }
        
        emit(DeepScanProgress(
            phase = DeepScanPhase.FILE_SYSTEM,
            phaseProgress = 1f,
            overallProgress = 0.60f,
            currentTask = "File system scan complete",
            itemsProcessed = fileResult?.totalFilesScanned ?: 0,
            totalItems = fileResult?.totalFilesScanned ?: 0
        ))
        
        // Phase 4: Network Security (10%)
        emit(DeepScanProgress(
            phase = DeepScanPhase.NETWORK_SECURITY,
            phaseProgress = 0f,
            overallProgress = 0.60f,
            currentTask = "Checking network security...",
            itemsProcessed = 0,
            totalItems = 7
        ))
        
        try {
            networkResult = networkSecurityScanner.performFullScan()
            Log.i(TAG, "Network scan complete. Risk: ${networkResult.overallRiskScore}")
        } catch (e: Exception) {
            Log.e(TAG, "Network scan failed", e)
        }
        
        emit(DeepScanProgress(
            phase = DeepScanPhase.NETWORK_SECURITY,
            phaseProgress = 1f,
            overallProgress = 0.70f,
            currentTask = "Network security check complete",
            itemsProcessed = 7,
            totalItems = 7
        ))
        
        // Phase 5: Privacy Audit (20%)
        emit(DeepScanProgress(
            phase = DeepScanPhase.PRIVACY_AUDIT,
            phaseProgress = 0f,
            overallProgress = 0.70f,
            currentTask = "Auditing app permissions...",
            itemsProcessed = 0,
            totalItems = 0
        ))
        
        try {
            privacyResult = privacyScanner.performFullScan()
            Log.i(TAG, "Privacy scan complete. High risk apps: ${privacyResult.highRiskApps.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Privacy scan failed", e)
        }
        
        emit(DeepScanProgress(
            phase = DeepScanPhase.PRIVACY_AUDIT,
            phaseProgress = 1f,
            overallProgress = 0.90f,
            currentTask = "Privacy audit complete",
            itemsProcessed = privacyResult?.totalAppsScanned ?: 0,
            totalItems = privacyResult?.totalAppsScanned ?: 0
        ))
        
        // Phase 6: Generate Report
        emit(DeepScanProgress(
            phase = DeepScanPhase.GENERATING_REPORT,
            phaseProgress = 0.5f,
            overallProgress = 0.95f,
            currentTask = "Generating security report...",
            itemsProcessed = 0,
            totalItems = 1
        ))
        
        emit(DeepScanProgress(
            phase = DeepScanPhase.COMPLETE,
            phaseProgress = 1f,
            overallProgress = 1f,
            currentTask = "Scan complete",
            itemsProcessed = 1,
            totalItems = 1
        ))
        
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get final deep scan result
     */
    suspend fun getFinalResult(): DeepScanResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        
        // Run all scans
        val systemResult = try {
            systemIntegrityScanner.performFullScan()
        } catch (e: Exception) {
            Log.e(TAG, "System scan failed", e)
            null
        }
        
        val malwareResult = try {
            malwareScanner.getFinalResult(flow { })
        } catch (e: Exception) {
            Log.e(TAG, "Malware scan failed", e)
            null
        }
        
        val networkResult = try {
            networkSecurityScanner.performFullScan()
        } catch (e: Exception) {
            Log.e(TAG, "Network scan failed", e)
            null
        }
        
        val privacyResult = try {
            privacyScanner.performFullScan()
        } catch (e: Exception) {
            Log.e(TAG, "Privacy scan failed", e)
            null
        }
        
        val endTime = System.currentTimeMillis()
        
        // Calculate overall risk
        val overallRisk = calculateOverallRisk(
            systemResult, malwareResult, networkResult, privacyResult
        )
        
        // Count issues
        val (critical, high, medium, low) = countIssues(
            systemResult, malwareResult, networkResult, privacyResult
        )
        
        // Generate recommendations
        val recommendations = generateRecommendations(
            systemResult, malwareResult, networkResult, privacyResult
        )
        
        // Device info
        val deviceInfo = getDeviceInfo()
        
        DeepScanResult(
            scanStartTime = startTime,
            scanEndTime = endTime,
            scanDurationMs = endTime - startTime,
            malwareScanResult = malwareResult,
            fileScanResult = null, // Optional for quick result
            systemIntegrityResult = systemResult,
            networkSecurityResult = networkResult,
            privacyScanResult = privacyResult,
            overallRiskScore = overallRisk,
            riskLevel = getRiskLevel(overallRisk),
            criticalIssuesCount = critical,
            highIssuesCount = high,
            mediumIssuesCount = medium,
            lowIssuesCount = low,
            recommendations = recommendations,
            deviceInfo = deviceInfo
        )
    }
    
    /**
     * Calculate overall risk score
     */
    private fun calculateOverallRisk(
        systemResult: SystemIntegrityScanner.SystemIntegrityResult?,
        malwareResult: ScanResult?,
        networkResult: NetworkSecurityScanner.NetworkSecurityResult?,
        privacyResult: PrivacyScanner.PrivacyScanResult?
    ): Int {
        var totalRisk = 0
        var factors = 0
        
        systemResult?.let {
            totalRisk += it.overallRiskScore
            factors++
        }
        
        malwareResult?.let {
            // Calculate risk from threats
            val threatRisk = it.threats.fold(0) { acc, threat ->
                acc + when (threat.severity) {
                    ThreatSeverity.CRITICAL -> 40
                    ThreatSeverity.HIGH -> 25
                    ThreatSeverity.MEDIUM -> 15
                    ThreatSeverity.LOW -> 5
                }
            }.coerceAtMost(60)
            totalRisk += threatRisk
            factors++
        }
        
        networkResult?.let {
            totalRisk += it.overallRiskScore
            factors++
        }
        
        privacyResult?.let {
            totalRisk += it.privacyScore
            factors++
        }
        
        return if (factors > 0) {
            (totalRisk / factors).coerceAtMost(100)
        } else 0
    }
    
    /**
     * Count issues by severity
     */
    private fun countIssues(
        systemResult: SystemIntegrityScanner.SystemIntegrityResult?,
        malwareResult: ScanResult?,
        networkResult: NetworkSecurityScanner.NetworkSecurityResult?,
        privacyResult: PrivacyScanner.PrivacyScanResult?
    ): List<Int> {
        var critical = 0
        var high = 0
        var medium = 0
        var low = 0
        
        // System issues
        systemResult?.integrityIssues?.forEach { issue ->
            when (issue.severity) {
                SystemIntegrityScanner.IssueSeverity.CRITICAL -> critical++
                SystemIntegrityScanner.IssueSeverity.HIGH -> high++
                SystemIntegrityScanner.IssueSeverity.MEDIUM -> medium++
                SystemIntegrityScanner.IssueSeverity.LOW -> low++
            }
        }
        
        // Malware threats
        malwareResult?.threats?.forEach { threat ->
            when (threat.severity) {
                ThreatSeverity.CRITICAL -> critical++
                ThreatSeverity.HIGH -> high++
                ThreatSeverity.MEDIUM -> medium++
                ThreatSeverity.LOW -> low++
            }
        }
        
        // Network warnings
        networkResult?.warnings?.forEach { warning ->
            when (warning.severity) {
                NetworkSecurityScanner.WarningSeverity.CRITICAL -> critical++
                NetworkSecurityScanner.WarningSeverity.HIGH -> high++
                NetworkSecurityScanner.WarningSeverity.MEDIUM -> medium++
                NetworkSecurityScanner.WarningSeverity.LOW -> low++
            }
        }
        
        // Privacy high risk apps
        critical += privacyResult?.highRiskApps?.count { 
            it.riskLevel == PrivacyScanner.RiskLevel.CRITICAL 
        } ?: 0
        high += privacyResult?.highRiskApps?.count { 
            it.riskLevel == PrivacyScanner.RiskLevel.HIGH 
        } ?: 0
        
        return listOf(critical, high, medium, low)
    }
    
    /**
     * Generate security recommendations
     */
    private fun generateRecommendations(
        systemResult: SystemIntegrityScanner.SystemIntegrityResult?,
        malwareResult: ScanResult?,
        networkResult: NetworkSecurityScanner.NetworkSecurityResult?,
        privacyResult: PrivacyScanner.PrivacyScanResult?
    ): List<SecurityRecommendation> {
        val recommendations = mutableListOf<SecurityRecommendation>()
        
        // System recommendations
        if (systemResult?.isRooted == true) {
            recommendations.add(SecurityRecommendation(
                title = "Device is Rooted",
                description = "Your device has root access which can bypass security measures. Consider unrooting for maximum security.",
                priority = RecommendationPriority.CRITICAL,
                actionType = ActionType.INVESTIGATE
            ))
        }
        
        if (systemResult?.hasXposed == true) {
            recommendations.add(SecurityRecommendation(
                title = "Xposed Framework Detected",
                description = "Xposed can modify app behavior. Remove it if not needed.",
                priority = RecommendationPriority.HIGH,
                actionType = ActionType.UNINSTALL_APP
            ))
        }
        
        // Malware recommendations
        malwareResult?.threats?.filter { 
            it.severity == ThreatSeverity.CRITICAL || it.severity == ThreatSeverity.HIGH 
        }?.forEach { threat ->
            recommendations.add(SecurityRecommendation(
                title = "Remove ${threat.appName}",
                description = threat.description,
                priority = if (threat.severity == ThreatSeverity.CRITICAL) 
                    RecommendationPriority.CRITICAL else RecommendationPriority.HIGH,
                actionType = ActionType.UNINSTALL_APP
            ))
        }
        
        // Network recommendations
        if (networkResult?.wifiInfo?.isSecure == false) {
            recommendations.add(SecurityRecommendation(
                title = "Insecure WiFi Network",
                description = "You're connected to an open WiFi network. Use VPN for protection.",
                priority = RecommendationPriority.HIGH,
                actionType = ActionType.CHANGE_SETTING
            ))
        }
        
        if (networkResult?.suspiciousPorts?.isNotEmpty() == true) {
            recommendations.add(SecurityRecommendation(
                title = "Suspicious Ports Open",
                description = "Ports ${networkResult.suspiciousPorts.joinToString()} are open and may indicate malware.",
                priority = RecommendationPriority.CRITICAL,
                actionType = ActionType.INVESTIGATE
            ))
        }
        
        // Privacy recommendations
        privacyResult?.accessibilityApps?.filter { !it.isSystemApp }?.forEach { app ->
            recommendations.add(SecurityRecommendation(
                title = "Review ${app.appName}",
                description = "This app has accessibility access which allows it to read all screen content.",
                priority = RecommendationPriority.MEDIUM,
                actionType = ActionType.REVOKE_PERMISSION
            ))
        }
        
        privacyResult?.highRiskApps?.forEach { app ->
            recommendations.add(SecurityRecommendation(
                title = "High Risk: ${app.appName}",
                description = app.reason,
                priority = when (app.riskLevel) {
                    PrivacyScanner.RiskLevel.CRITICAL -> RecommendationPriority.CRITICAL
                    PrivacyScanner.RiskLevel.HIGH -> RecommendationPriority.HIGH
                    PrivacyScanner.RiskLevel.MEDIUM -> RecommendationPriority.MEDIUM
                    PrivacyScanner.RiskLevel.LOW -> RecommendationPriority.LOW
                },
                actionType = ActionType.INVESTIGATE
            ))
        }
        
        return recommendations.sortedBy { it.priority.ordinal }
    }
    
    /**
     * Get risk level from score
     */
    private fun getRiskLevel(score: Int): OverallRiskLevel {
        return when {
            score <= 20 -> OverallRiskLevel.SECURE
            score <= 40 -> OverallRiskLevel.LOW
            score <= 60 -> OverallRiskLevel.MEDIUM
            score <= 80 -> OverallRiskLevel.HIGH
            else -> OverallRiskLevel.CRITICAL
        }
    }
    
    /**
     * Get device information
     */
    private fun getDeviceInfo(): DeviceScanInfo {
        val appVersion = try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
        
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date())
        
        return DeviceScanInfo(
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            androidVersion = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT,
            appVersion = appVersion,
            scanTimestamp = timestamp
        )
    }
}
