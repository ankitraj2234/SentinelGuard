package com.sentinelguard.security.signal.detector

import com.sentinelguard.domain.model.SecuritySignal

/**
 * Detector Interface
 * 
 * All security signal detectors implement this interface.
 * Detectors are stateless and produce signals based on current device state.
 */
interface Detector {
    
    /**
     * Unique name for this detector (for logging/debugging).
     */
    val name: String
    
    /**
     * Whether this detector requires a specific permission.
     * Returns null if no permission required, or permission string otherwise.
     */
    val requiredPermission: String?
        get() = null
    
    /**
     * Collects current signals from this detector.
     * 
     * Called on app open. Should be fast and non-blocking.
     * If detector cannot run (permission denied, etc.), returns empty list.
     * 
     * @return List of detected signals
     */
    suspend fun detect(): List<SecuritySignal>
    
    /**
     * Returns detailed detection info for debugging/logging.
     * Should not include sensitive data.
     */
    fun getDebugInfo(): Map<String, Any> = emptyMap()
}

/**
 * Result of a detection operation.
 */
sealed class DetectionResult {
    data class Success(val signals: List<SecuritySignal>) : DetectionResult()
    data class PermissionDenied(val permission: String) : DetectionResult()
    data class Error(val message: String, val cause: Throwable? = null) : DetectionResult()
}
