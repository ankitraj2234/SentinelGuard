package com.sentinelguard.security.response

import com.sentinelguard.alert.EmailAlertService
import com.sentinelguard.domain.model.*
import com.sentinelguard.domain.repository.IncidentRepository
import com.sentinelguard.domain.util.SecureIdGenerator
import com.sentinelguard.security.risk.RiskScoringEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ResponseEngine: Executes Security Responses
 * 
 * WHY THIS EXISTS:
 * Takes action based on risk level. Coordinates lock, session, and alerting.
 * 
 * RESPONSES BY LEVEL:
 * - NORMAL: None
 * - WARNING: Log incident, UI shows warning
 * - HIGH: Lock app, require password/biometric, send email alert
 * - CRITICAL: Lock, wipe session, send email, log incident
 * 
 * LIMITATIONS (App-Level Only):
 * - Cannot lock OS screen
 * - Cannot prevent app uninstall
 * - Cannot access other apps' data
 * - Cannot intercept lockscreen PIN
 */
class ResponseEngine(
    private val riskScoringEngine: RiskScoringEngine,
    private val appLockManager: AppLockManager,
    private val sessionManager: SessionManager,
    private val incidentRepository: IncidentRepository,
    private val emailAlertService: EmailAlertService
) {
    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    }
    
    /**
     * Evaluates current risk and executes appropriate response.
     * Called after signal collection on app open.
     */
    suspend fun evaluateAndRespond(): ResponseResult = withContext(Dispatchers.IO) {
        val riskScore = riskScoringEngine.calculateRiskScore()
        executeResponse(riskScore)
    }

    /**
     * Executes response based on risk score.
     */
    suspend fun executeResponse(riskScore: RiskScore): ResponseResult = withContext(Dispatchers.IO) {
        val actions = mutableListOf<ResponseAction>()

        when (riskScore.level) {
            RiskLevel.NORMAL -> {
                actions.add(ResponseAction.NONE)
            }
            
            RiskLevel.WARNING -> {
                actions.add(ResponseAction.UI_WARNING)
                logIncident(riskScore, IncidentSeverity.WARNING, actions)
            }
            
            RiskLevel.HIGH -> {
                appLockManager.lock()
                sessionManager.requireBiometric()
                actions.add(ResponseAction.APP_LOCKED)
                actions.add(ResponseAction.BIOMETRIC_REQUIRED)
                
                val incidentId = logIncident(riskScore, IncidentSeverity.HIGH, actions)
                
                // Send email alert for HIGH risk
                sendSecurityAlert(riskScore, incidentId, "HIGH")
                actions.add(ResponseAction.ALERT_QUEUED)
            }
            
            RiskLevel.CRITICAL -> {
                appLockManager.forceExtendedLockout()
                sessionManager.wipeSession()
                actions.add(ResponseAction.APP_LOCKED)
                actions.add(ResponseAction.SESSION_WIPED)
                actions.add(ResponseAction.COOLDOWN_STARTED)
                
                val incidentId = logIncident(riskScore, IncidentSeverity.CRITICAL, actions)
                
                // Send email alert for CRITICAL risk
                sendSecurityAlert(riskScore, incidentId, "CRITICAL")
                actions.add(ResponseAction.ALERT_QUEUED)
            }
        }

        ResponseResult(
            riskLevel = riskScore.level,
            score = riskScore.totalScore,
            actions = actions,
            requiresLock = riskScore.level >= RiskLevel.HIGH,
            requiresAuth = riskScore.level >= RiskLevel.HIGH
        )
    }

    /**
     * Handles successful authentication.
     */
    fun onAuthSuccess(userId: String) {
        appLockManager.unlock()
        sessionManager.startSession(userId)
        sessionManager.clearBiometricRequirement()
    }

    /**
     * Handles failed authentication attempt.
     * Sends email alert after 3 failed attempts.
     */
    suspend fun onAuthFailure() {
        val failedCount = appLockManager.recordFailedAttempt()
        
        // Send alert after 3 or more failed attempts
        if (failedCount >= 3 && failedCount % 3 == 0) {
            sendFailedLoginAlert(failedCount)
        }
    }

    /**
     * Checks if app is currently locked.
     */
    fun isAppLocked(): Boolean = appLockManager.isLocked.value

    /**
     * Checks if authentication is required.
     */
    fun requiresAuthentication(): Boolean = sessionManager.needsAuthentication()

    /**
     * Checks if app is in cooldown.
     */
    fun isInCooldown(): Boolean = appLockManager.isInCooldown()

    /**
     * Gets remaining cooldown formatted.
     */
    fun getCooldownRemaining(): String = appLockManager.getRemainingCooldownFormatted()

    private suspend fun logIncident(
        riskScore: RiskScore,
        severity: IncidentSeverity,
        actions: List<ResponseAction>
    ): String {
        val incident = Incident(
            id = SecureIdGenerator.generateId(),
            severity = severity,
            riskScore = riskScore.totalScore,
            triggers = riskScore.contributions.keys.toList(),
            actionsTaken = actions,
            summary = riskScore.triggerReason,
            timestamp = System.currentTimeMillis()
        )
        return incidentRepository.insert(incident)
    }

    /**
     * Sends security alert email for HIGH or CRITICAL risk events.
     */
    private suspend fun sendSecurityAlert(riskScore: RiskScore, incidentId: String, level: String) {
        val emoji = if (level == "CRITICAL") "🚨" else "⚠️"
        val subject = "$emoji $level Security Alert"
        
        val triggersList = riskScore.contributions.entries
            .sortedByDescending { it.value }
            .take(5)
            .joinToString("\n") { "• ${it.key}: ${it.value} points" }
        
        val actionsList = when (level) {
            "CRITICAL" -> """
                |• App locked immediately
                |• Session data wiped
                |• Extended cooldown applied (15 minutes)
            """.trimMargin()
            "HIGH" -> """
                |• App locked
                |• Biometric authentication required
            """.trimMargin()
            else -> "• Monitoring increased"
        }
        
        val body = """
            |$emoji $level SECURITY ALERT
            |━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            |
            |📊 RISK ASSESSMENT
            |━━━━━━━━━━━━━━━━━━━━
            |Risk Score: ${riskScore.totalScore}/100
            |Level: $level
            |Time: ${dateFormat.format(Date())}
            |Incident ID: $incidentId
            |
            |🔍 TOP RISK TRIGGERS
            |━━━━━━━━━━━━━━━━━━━━
            |$triggersList
            |
            |📝 REASON
            |━━━━━━━━━━━━━━━━━━━━
            |${riskScore.triggerReason}
            |
            |🔒 ACTIONS TAKEN
            |━━━━━━━━━━━━━━━━━━━━
            |$actionsList
            |
            |⚠️ IF THIS WAS NOT YOU
            |━━━━━━━━━━━━━━━━━━━━
            |Your device may be compromised. Consider:
            |1. Changing your SentinelGuard password
            |2. Checking for unauthorized apps
            |3. Reviewing device access permissions
            |4. Running a malware scan
        """.trimMargin()
        
        try {
            emailAlertService.sendAlert(subject, body)
        } catch (e: Exception) {
            // Log error but don't crash - email is best effort
            android.util.Log.e("ResponseEngine", "Failed to send security alert email", e)
        }
    }

    /**
     * Sends alert for multiple failed login attempts.
     */
    private suspend fun sendFailedLoginAlert(attemptCount: Int) {
        val subject = "🔐 Failed Login Attempts Detected"
        
        val body = """
            |🔐 FAILED LOGIN ATTEMPTS ALERT
            |━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            |
            |Someone has failed to log into your SentinelGuard app.
            |
            |📊 DETAILS
            |━━━━━━━━━━━━━━━━━━━━
            |Failed Attempts: $attemptCount
            |Time: ${dateFormat.format(Date())}
            |
            |⚠️ POSSIBLE CAUSES
            |━━━━━━━━━━━━━━━━━━━━
            |• Incorrect password entered
            |• Someone else trying to access your app
            |• Biometric authentication failed
            |
            |🔒 RECOMMENDED ACTIONS
            |━━━━━━━━━━━━━━━━━━━━
            |• If this was you, no action needed
            |• If this was NOT you:
            |  1. Lock your device
            |  2. Change your SentinelGuard password
            |  3. Enable additional security measures
        """.trimMargin()
        
        try {
            emailAlertService.sendAlert(subject, body)
        } catch (e: Exception) {
            android.util.Log.e("ResponseEngine", "Failed to send failed login alert", e)
        }
    }
}

/**
 * Result of a response evaluation.
 */
data class ResponseResult(
    val riskLevel: RiskLevel,
    val score: Int,
    val actions: List<ResponseAction>,
    val requiresLock: Boolean,
    val requiresAuth: Boolean
)
