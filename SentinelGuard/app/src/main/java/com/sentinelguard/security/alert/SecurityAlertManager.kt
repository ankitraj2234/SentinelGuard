package com.sentinelguard.security.alert

import android.content.Context
import android.os.Build
import com.sentinelguard.data.local.preferences.SecurePreferences
import com.sentinelguard.domain.model.RiskScore
import com.sentinelguard.email.AlertEmailFormatter
import com.sentinelguard.email.DeviceInfo
import com.sentinelguard.email.EmailService
import com.sentinelguard.email.SecurityEvent
import com.sentinelguard.security.intruder.IntruderCaptureService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SecurityAlertManager: Central manager for security alerts.
 * 
 * Responsibilities:
 * - Collect and store security events
 * - Determine when to send alerts
 * - Format and dispatch email alerts
 * - Manage alert cooldowns
 * - Trigger intruder selfie capture on high risk
 */
@Singleton
class SecurityAlertManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val emailService: EmailService,
    private val securePreferences: SecurePreferences,
    private val intruderCaptureService: IntruderCaptureService
) {
    
    companion object {
        private const val MAX_EVENTS = 100
    }
    
    // In-memory event log (most recent events)
    private val recentEvents = ConcurrentLinkedQueue<SecurityEvent>()
    
    /**
     * Log a security event
     */
    fun logEvent(type: String, description: String) {
        val event = SecurityEvent(
            timestamp = System.currentTimeMillis(),
            type = type,
            description = description
        )
        
        recentEvents.add(event)
        
        // Keep only last MAX_EVENTS
        while (recentEvents.size > MAX_EVENTS) {
            recentEvents.poll()
        }
    }
    
    /**
     * Send a security alert email if conditions are met
     */
    suspend fun sendSecurityAlert(riskScore: RiskScore): Result<Unit> = withContext(Dispatchers.IO) {
        // Check cooldown
        val now = System.currentTimeMillis()
        val cooldownMs = securePreferences.cooldownMinutes * 60 * 1000L
        
        if (now < securePreferences.cooldownUntil) {
            return@withContext Result.failure(Exception("Alert on cooldown"))
        }
        
        // Get recipient
        val recipient = securePreferences.alertRecipient
        if (recipient.isNullOrBlank()) {
            return@withContext Result.failure(Exception("No alert recipient configured"))
        }
        
        // Check if email service is configured
        if (!emailService.hasCredentials()) {
            return@withContext Result.failure(Exception("Email service not configured"))
        }
        
        // Build email content
        val deviceInfo = getDeviceInfo()
        val events = recentEvents.toList()
        val emailContent = AlertEmailFormatter.formatSecurityAlert(riskScore, deviceInfo, events)
        
        // Send email
        val result = emailService.sendEmail(recipient, emailContent.subject, emailContent.body)
        
        if (result.isSuccess) {
            // Set cooldown
            securePreferences.cooldownUntil = now + cooldownMs
            
            // Log the alert send
            logEvent("ALERT_SENT", "Security alert email sent to $recipient")
            
            // Trigger intruder capture if enabled (on high risk)
            if (riskScore.totalScore >= 70 && intruderCaptureService.isEnabled()) {
                try {
                    intruderCaptureService.captureAndSendIntruderPhoto(
                        reason = riskScore.triggerReason ?: "High risk score detected",
                        location = getLastKnownLocation()
                    )
                    logEvent("INTRUDER_CAPTURE", "Intruder photo captured and sent")
                } catch (e: Exception) {
                    logEvent("INTRUDER_CAPTURE_FAILED", "Failed to capture intruder photo: ${e.message}")
                }
            }
        }
        
        result
    }
    
    /**
     * Check if alert should be triggered based on risk score
     */
    fun shouldTriggerAlert(riskScore: RiskScore): Boolean {
        // Alert thresholds
        val threshold = riskScore.totalScore >= 70
        
        if (!threshold) return false
        
        // Check cooldown
        return System.currentTimeMillis() >= securePreferences.cooldownUntil
    }
    
    /**
     * Get recent events (for UI display)
     */
    fun getRecentEvents(limit: Int = 20): List<SecurityEvent> {
        return recentEvents.toList().takeLast(limit).reversed()
    }
    
    /**
     * Clear event log
     */
    fun clearEvents() {
        recentEvents.clear()
    }
    
    /**
     * Get recent logs as formatted strings (for PDF export)
     */
    fun getRecentLogs(limit: Int = 100): List<String> {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        return recentEvents.toList()
            .takeLast(limit)
            .reversed()
            .map { event ->
                "[${dateFormat.format(java.util.Date(event.timestamp))}] ${event.type}: ${event.description}"
            }
    }
    
    private fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            lastLocation = getLastKnownLocation()
        )
    }
    
    private fun getLastKnownLocation(): String? {
        val lat = securePreferences.lastLocationLat
        val lng = securePreferences.lastLocationLng
        
        return if (lat != 0.0 && lng != 0.0) {
            String.format("%.4f, %.4f", lat, lng)
        } else {
            null
        }
    }
}
