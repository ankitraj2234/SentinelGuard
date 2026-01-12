package com.sentinelguard.email

import com.sentinelguard.domain.model.RiskScore
import java.text.SimpleDateFormat
import java.util.*

/**
 * Formats security alerts for email delivery.
 * Creates professional, readable email content.
 */
object AlertEmailFormatter {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    
    /**
     * Format a risk-based security alert email
     */
    fun formatSecurityAlert(
        riskScore: RiskScore,
        deviceInfo: DeviceInfo,
        recentEvents: List<SecurityEvent>
    ): EmailContent {
        val subject = when {
            riskScore.totalScore >= 90 -> "🚨 CRITICAL: SentinelGuard Security Alert"
            riskScore.totalScore >= 70 -> "⚠️ HIGH RISK: SentinelGuard Alert"
            else -> "⚠️ SentinelGuard Security Warning"
        }
        
        val body = buildString {
            appendLine("═══════════════════════════════════════════")
            appendLine("         SENTINELGUARD SECURITY ALERT")
            appendLine("═══════════════════════════════════════════")
            appendLine()
            appendLine("Alert Time: ${dateFormat.format(Date())}")
            appendLine("Risk Level: ${riskScore.level.name} (${riskScore.totalScore}/100)")
            appendLine()
            appendLine("───────────────────────────────────────────")
            appendLine("DEVICE INFORMATION")
            appendLine("───────────────────────────────────────────")
            appendLine("Device: ${deviceInfo.manufacturer} ${deviceInfo.model}")
            appendLine("Android: ${deviceInfo.androidVersion}")
            appendLine("Last Known Location: ${deviceInfo.lastLocation ?: "Unknown"}")
            appendLine()
            appendLine("───────────────────────────────────────────")
            appendLine("RISK BREAKDOWN")
            appendLine("───────────────────────────────────────────")
            appendLine("Trigger Reason: ${riskScore.triggerReason}")
            appendLine()
            
            riskScore.contributions.forEach { (signalType, score) ->
                if (score > 0) {
                    appendLine("• ${signalType.name}: $score points")
                }
            }
            
            appendLine()
            appendLine("───────────────────────────────────────────")
            appendLine("RECENT SECURITY EVENTS")
            appendLine("───────────────────────────────────────────")
            
            if (recentEvents.isEmpty()) {
                appendLine("No recent events logged.")
            } else {
                recentEvents.take(10).forEach { event ->
                    appendLine("• [${dateFormat.format(Date(event.timestamp))}]")
                    appendLine("  ${event.type}: ${event.description}")
                }
            }
            
            appendLine()
            appendLine("───────────────────────────────────────────")
            appendLine("RECOMMENDED ACTIONS")
            appendLine("───────────────────────────────────────────")
            appendLine(getRecommendedActions(riskScore))
            appendLine()
            appendLine("═══════════════════════════════════════════")
            appendLine("This is an automated alert from SentinelGuard.")
            appendLine("If this was you, you can safely ignore this message.")
            appendLine("═══════════════════════════════════════════")
        }
        
        return EmailContent(subject, body)
    }
    
    /**
     * Format a password recovery email
     */
    fun formatRecoveryEmail(code: String): EmailContent {
        val subject = "🔐 SentinelGuard Password Recovery Code"
        
        val body = buildString {
            appendLine("═══════════════════════════════════════════")
            appendLine("      SENTINELGUARD PASSWORD RECOVERY")
            appendLine("═══════════════════════════════════════════")
            appendLine()
            appendLine("Your password recovery code is:")
            appendLine()
            appendLine("        ╔═══════════════╗")
            appendLine("        ║    $code     ║")
            appendLine("        ╚═══════════════╝")
            appendLine()
            appendLine("This code expires in 10 minutes.")
            appendLine()
            appendLine("If you did not request this code, please")
            appendLine("ignore this email and secure your account.")
            appendLine()
            appendLine("═══════════════════════════════════════════")
        }
        
        return EmailContent(subject, body)
    }
    
    /**
     * Format a daily security digest
     */
    fun formatDailyDigest(
        stats: DailyStats,
        alerts: Int,
        topApps: List<String>
    ): EmailContent {
        val subject = "📊 SentinelGuard Daily Security Report"
        
        val body = buildString {
            appendLine("═══════════════════════════════════════════")
            appendLine("      DAILY SECURITY DIGEST")
            appendLine("      ${dateFormat.format(Date())}")
            appendLine("═══════════════════════════════════════════")
            appendLine()
            appendLine("SUMMARY")
            appendLine("─────────────────────────")
            appendLine("• Security Alerts: $alerts")
            appendLine("• Average Risk Score: ${stats.avgRiskScore}")
            appendLine("• Max Risk Score: ${stats.maxRiskScore}")
            appendLine("• Sessions Today: ${stats.sessionCount}")
            appendLine()
            appendLine("TOP APPS USED")
            appendLine("─────────────────────────")
            topApps.forEachIndexed { i, app ->
                appendLine("${i + 1}. $app")
            }
            appendLine()
            appendLine("Your device security: ${if (stats.avgRiskScore < 40) "✅ GOOD" else "⚠️ REVIEW NEEDED"}")
            appendLine()
            appendLine("═══════════════════════════════════════════")
        }
        
        return EmailContent(subject, body)
    }
    
    private fun getRecommendedActions(riskScore: RiskScore): String {
        val actions = mutableListOf<String>()
        
        riskScore.contributions.forEach { (signalType, score) ->
            if (score > 0) {
                val name = signalType.name
                when {
                    name.contains("LOCATION", true) -> 
                        actions.add("• Verify your current location is expected")
                    name.contains("SIM", true) -> 
                        actions.add("• Check if SIM was changed legitimately")
                    name.contains("ROOT", true) -> 
                        actions.add("• Verify device has not been tampered with")
                    name.contains("LOGIN", true) || name.contains("AUTH", true) -> 
                        actions.add("• Change your password immediately")
                    name.contains("NETWORK", true) -> 
                        actions.add("• Verify you are on a trusted network")
                }
            }
        }
        
        if (actions.isEmpty()) {
            actions.add("• Monitor your device for further unusual activity")
        }
        
        if (riskScore.totalScore >= 70) {
            actions.add("• Consider enabling device lock remotely")
            actions.add("• Check for unauthorized access to sensitive apps")
        }
        
        return actions.joinToString("\n")
    }
}

data class EmailContent(
    val subject: String,
    val body: String
)

data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val androidVersion: String,
    val lastLocation: String?
)

data class SecurityEvent(
    val timestamp: Long,
    val type: String,
    val description: String
)

data class DailyStats(
    val avgRiskScore: Int,
    val maxRiskScore: Int,
    val sessionCount: Int
)
