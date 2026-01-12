package com.sentinelguard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinelguard.data.local.preferences.SecurePreferences
import com.sentinelguard.scanner.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Scan type options
 */
enum class ScanType {
    QUICK,  // User apps only
    FULL,   // All apps + downloads
    DEEP    // Complete security audit
}

data class ScanUiState(
    // Scan state
    val isScanning: Boolean = false,
    val scanType: ScanType = ScanType.FULL,
    val currentItem: String = "",
    val scannedCount: Int = 0,
    val totalCount: Int = 0,
    val threatsFound: Int = 0,
    val phase: ScanPhase = ScanPhase.INITIALIZING,
    val progress: Float = 0f,
    val scanComplete: Boolean = false,
    
    // Results
    val result: ScanResult? = null,
    val deepScanResult: DeepScanEngine.DeepScanResult? = null,
    
    // Last scan info
    val lastScanTime: Long = 0,
    val lastScanThreats: Int = 0,
    
    // Deep scan specific
    val deepScanPhase: DeepScanEngine.DeepScanPhase = DeepScanEngine.DeepScanPhase.INITIALIZING,
    val hasStoragePermission: Boolean = false,
    
    // Error
    val errorMessage: String? = null
)

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val scanner: MalwareScanner,
    private val deepScanEngine: DeepScanEngine,
    private val fileSystemScanner: FileSystemScanner,
    private val securePreferences: SecurePreferences
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()
    
    private var scanJob: Job? = null
    
    init {
        loadLastScanInfo()
        checkPermissions()
    }
    
    private fun loadLastScanInfo() {
        _uiState.update { it.copy(
            lastScanTime = securePreferences.lastScanTime,
            lastScanThreats = securePreferences.lastScanThreatsFound
        )}
    }
    
    private fun checkPermissions() {
        _uiState.update { it.copy(
            hasStoragePermission = fileSystemScanner.hasFullStorageAccess()
        )}
    }
    
    /**
     * Start a quick scan (user apps only)
     */
    fun startQuickScan() {
        if (_uiState.value.isScanning) return
        
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            _uiState.update { it.copy(
                isScanning = true,
                scanType = ScanType.QUICK,
                scanComplete = false,
                result = null,
                deepScanResult = null,
                scannedCount = 0,
                threatsFound = 0,
                errorMessage = null
            )}
            
            try {
                scanner.performQuickScan().collect { progress ->
                    val progressPercent = if (progress.totalCount > 0) {
                        progress.scannedCount.toFloat() / progress.totalCount
                    } else 0f
                    
                    _uiState.update { it.copy(
                        currentItem = progress.currentItem,
                        scannedCount = progress.scannedCount,
                        totalCount = progress.totalCount,
                        threatsFound = progress.threatsFound,
                        phase = progress.phase,
                        progress = progressPercent
                    )}
                }
                
                val result = scanner.getFinalResult(flowOf())
                saveScanResults(result)
                
                _uiState.update { it.copy(
                    isScanning = false,
                    scanComplete = true,
                    result = result,
                    lastScanTime = System.currentTimeMillis(),
                    lastScanThreats = result.threats.size,
                    phase = ScanPhase.COMPLETE
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isScanning = false,
                    errorMessage = "Scan failed: ${e.message}"
                )}
            }
        }
    }
    
    /**
     * Start a full scan (all apps + downloads)
     */
    fun startFullScan() {
        if (_uiState.value.isScanning) return
        
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            _uiState.update { it.copy(
                isScanning = true,
                scanType = ScanType.FULL,
                scanComplete = false,
                result = null,
                deepScanResult = null,
                scannedCount = 0,
                threatsFound = 0,
                errorMessage = null
            )}
            
            try {
                scanner.performFullScan().collect { progress ->
                    val progressPercent = if (progress.totalCount > 0) {
                        progress.scannedCount.toFloat() / progress.totalCount
                    } else 0f
                    
                    _uiState.update { it.copy(
                        currentItem = progress.currentItem,
                        scannedCount = progress.scannedCount,
                        totalCount = progress.totalCount,
                        threatsFound = progress.threatsFound,
                        phase = progress.phase,
                        progress = progressPercent
                    )}
                }
                
                val result = scanner.getFinalResult(flowOf())
                saveScanResults(result)
                
                _uiState.update { it.copy(
                    isScanning = false,
                    scanComplete = true,
                    result = result,
                    lastScanTime = System.currentTimeMillis(),
                    lastScanThreats = result.threats.size,
                    phase = ScanPhase.COMPLETE
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isScanning = false,
                    errorMessage = "Scan failed: ${e.message}"
                )}
            }
        }
    }
    
    /**
     * Start a deep scan (complete security audit)
     */
    fun startDeepScan() {
        if (_uiState.value.isScanning) return
        
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            _uiState.update { it.copy(
                isScanning = true,
                scanType = ScanType.DEEP,
                scanComplete = false,
                result = null,
                deepScanResult = null,
                scannedCount = 0,
                threatsFound = 0,
                errorMessage = null,
                deepScanPhase = DeepScanEngine.DeepScanPhase.INITIALIZING
            )}
            
            try {
                deepScanEngine.performDeepScan().collect { progress ->
                    _uiState.update { it.copy(
                        currentItem = progress.currentTask,
                        scannedCount = progress.itemsProcessed,
                        totalCount = progress.totalItems,
                        progress = progress.overallProgress,
                        deepScanPhase = progress.phase
                    )}
                }
                
                // Get final deep scan result
                val deepResult = deepScanEngine.getFinalResult()
                
                // Save scan stats
                val now = System.currentTimeMillis()
                securePreferences.lastScanTime = now
                securePreferences.lastScanThreatsFound = deepResult.criticalIssuesCount + deepResult.highIssuesCount
                
                _uiState.update { it.copy(
                    isScanning = false,
                    scanComplete = true,
                    deepScanResult = deepResult,
                    lastScanTime = now,
                    lastScanThreats = deepResult.criticalIssuesCount + deepResult.highIssuesCount,
                    deepScanPhase = DeepScanEngine.DeepScanPhase.COMPLETE
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isScanning = false,
                    errorMessage = "Deep scan failed: ${e.message}"
                )}
            }
        }
    }
    
    /**
     * Cancel ongoing scan
     */
    fun cancelScan() {
        scanJob?.cancel()
        _uiState.update { it.copy(
            isScanning = false,
            scanComplete = false,
            phase = ScanPhase.INITIALIZING,
            deepScanPhase = DeepScanEngine.DeepScanPhase.INITIALIZING
        )}
    }
    
    /**
     * Reset to start new scan
     */
    fun resetScan() {
        _uiState.update { ScanUiState(
            lastScanTime = it.lastScanTime,
            lastScanThreats = it.lastScanThreats,
            hasStoragePermission = it.hasStoragePermission
        )}
    }
    
    /**
     * Refresh permissions after user grants
     */
    fun refreshPermissions() {
        checkPermissions()
    }
    
    /**
     * Save scan results to preferences
     */
    private fun saveScanResults(result: ScanResult) {
        val now = System.currentTimeMillis()
        securePreferences.lastScanTime = now
        securePreferences.lastScanThreatsFound = result.threats.size
        securePreferences.lastScanAppsScanned = result.appsScanned
    }
    
    /**
     * Get deep scan phase display name
     */
    fun getPhaseDisplayName(phase: DeepScanEngine.DeepScanPhase): String {
        return when (phase) {
            DeepScanEngine.DeepScanPhase.INITIALIZING -> "Initializing..."
            DeepScanEngine.DeepScanPhase.SYSTEM_INTEGRITY -> "Checking System Integrity"
            DeepScanEngine.DeepScanPhase.APP_ANALYSIS -> "Analyzing Installed Apps"
            DeepScanEngine.DeepScanPhase.FILE_SYSTEM -> "Scanning File System"
            DeepScanEngine.DeepScanPhase.NETWORK_SECURITY -> "Checking Network Security"
            DeepScanEngine.DeepScanPhase.PRIVACY_AUDIT -> "Auditing App Permissions"
            DeepScanEngine.DeepScanPhase.GENERATING_REPORT -> "Generating Report"
            DeepScanEngine.DeepScanPhase.COMPLETE -> "Scan Complete"
        }
    }
    
    /**
     * Get risk level color (for UI)
     */
    fun getRiskLevelColor(level: DeepScanEngine.OverallRiskLevel): Long {
        return when (level) {
            DeepScanEngine.OverallRiskLevel.SECURE -> 0xFF4CAF50    // Green
            DeepScanEngine.OverallRiskLevel.LOW -> 0xFF8BC34A       // Light Green
            DeepScanEngine.OverallRiskLevel.MEDIUM -> 0xFFFFEB3B    // Yellow
            DeepScanEngine.OverallRiskLevel.HIGH -> 0xFFFF9800      // Orange
            DeepScanEngine.OverallRiskLevel.CRITICAL -> 0xFFF44336  // Red
        }
    }
}
