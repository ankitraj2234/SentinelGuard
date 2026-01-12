package com.sentinelguard.alert

import android.content.Context
import android.os.Build
import com.sentinelguard.data.database.dao.AlertHistoryDao
import com.sentinelguard.data.database.entities.AlertHistoryEntity
import com.sentinelguard.data.database.entities.AlertStatus
import com.sentinelguard.data.database.entities.IncidentEntity
import com.sentinelguard.data.preferences.SecurePreferencesManager
import com.sentinelguard.security.collector.detectors.NetworkDetector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Properties
import javax.inject.Inject
import javax.inject.Singleton
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 * Email Alert Service.
 * 
 * Sends security alerts via direct SMTP connection.
 * No cloud relay - requires user to configure SMTP credentials.
 * 
 * Production Features:
 * - Offline queue with retry on connectivity
 * - Progressive retry backoff
 * - Alert templates
 * - Network connectivity check
 * - Configurable timeouts
 * - App version in all emails
 */
@Singleton
class EmailAlertService @Inject constructor(
    private val context: Context,
    private val alertHistoryDao: AlertHistoryDao
) {

    private val prefs = SecurePreferencesManager(context)
    private val networkDetector = NetworkDetector(context)

    companion object {
        private const val TAG = "EmailAlertService"
        private const val MAX_RETRY_COUNT = 5
        private val RETRY_DELAYS_MS = listOf(
            60_000L,      // 1 minute
            300_000L,     // 5 minutes
            900_000L,     // 15 minutes
            3_600_000L,   // 1 hour
            7_200_000L    // 2 hours
        )
        
        // Timeout for email operations (30 seconds)
        private const val EMAIL_TIMEOUT_MS = 30_000L
        
        // SMTP Timeouts (15 seconds each)
        private const val SMTP_CONNECT_TIMEOUT_MS = "15000"
        private const val SMTP_READ_TIMEOUT_MS = "15000"
        private const val SMTP_WRITE_TIMEOUT_MS = "15000"
    }

    /**
     * Gets app version for email footer.
     */
    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val versionName = packageInfo.versionName ?: "1.0"
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
            "$versionName (Build $versionCode)"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    /**
     * Gets device info for email debugging.
     */
    private fun getDeviceInfo(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE}, API ${Build.VERSION.SDK_INT})"
    }

    /**
     * Generates email footer with app and device info.
     */
    private fun getEmailFooter(): String {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault()).format(Date())
        return """
            |
            |═══════════════════════════════════════════════════
            |🔐 SentinelGuard Security
            |📱 App Version: ${getAppVersion()}
            |📲 Device: ${getDeviceInfo()}
            |⏰ Sent: $timestamp
            |═══════════════════════════════════════════════════
        """.trimMargin()
    }

    /**
     * Queues an alert email for an incident.
     */
    suspend fun queueAlert(incident: IncidentEntity) {
        val recipient = prefs.alertRecipient ?: return
        
        val subject = buildSubject(incident)
        val body = buildBody(incident)
        
        val alert = AlertHistoryEntity(
            recipientEmail = recipient,
            subject = subject,
            body = body,
            status = AlertStatus.QUEUED,
            incidentId = incident.id,
            createdAt = System.currentTimeMillis()
        )
        
        alertHistoryDao.insert(alert)
        
        // Try to send immediately if online
        if (networkDetector.isConnected()) {
            processPendingAlerts()
        }
    }

    /**
     * Processes all pending alerts with network check and timeout.
     */
    suspend fun processPendingAlerts() = withContext(Dispatchers.IO) {
        if (!prefs.isSmtpConfigured()) {
            android.util.Log.w(TAG, "SMTP not configured, skipping alert processing")
            return@withContext
        }
        if (!networkDetector.isConnected()) {
            android.util.Log.w(TAG, "No network, skipping alert processing")
            return@withContext
        }
        
        val pendingAlerts = alertHistoryDao.getPendingSend()
        
        for (alert in pendingAlerts) {
            try {
                withTimeout(EMAIL_TIMEOUT_MS) {
                    sendEmail(alert)
                    alertHistoryDao.updateStatusSent(
                        id = alert.id,
                        status = AlertStatus.SENT,
                        sentAt = System.currentTimeMillis()
                    )
                    android.util.Log.i(TAG, "Alert sent successfully to ${alert.recipientEmail}")
                }
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                android.util.Log.e(TAG, "Alert send timeout after ${EMAIL_TIMEOUT_MS}ms")
                handleSendFailure(alert, "Timeout after ${EMAIL_TIMEOUT_MS / 1000} seconds")
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Failed to send alert", e)
                handleSendFailure(alert, e.message)
            }
        }
    }

    /**
     * Sends a test email to verify SMTP configuration.
     */
    suspend fun sendTestEmail(): Result<Unit> = withContext(Dispatchers.IO) {
        if (!prefs.isSmtpConfigured()) {
            return@withContext Result.failure(Exception("SMTP not configured"))
        }
        
        if (!networkDetector.isConnected()) {
            return@withContext Result.failure(Exception("No network connection"))
        }
        
        val recipient = prefs.alertRecipient ?: prefs.smtpUsername
            ?: return@withContext Result.failure(Exception("No recipient configured"))
        
        val testAlert = AlertHistoryEntity(
            recipientEmail = recipient,
            subject = "✅ [SentinelGuard] Test Alert - Configuration Verified",
            body = """
                This is a test email from SentinelGuard.
                
                If you received this, your email alert configuration is working correctly!
                
                You will now receive:
                • Security alerts when threats are detected
                • Intruder photos when unauthorized access occurs
                • Password recovery codes
            """.trimIndent() + getEmailFooter(),
            status = AlertStatus.SENDING
        )
        
        try {
            withTimeout(EMAIL_TIMEOUT_MS) {
                sendEmail(testAlert)
                android.util.Log.i(TAG, "Test email sent successfully")
                Result.success(Unit)
            }
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            android.util.Log.e(TAG, "Test email timeout")
            Result.failure(Exception("Email send timed out after ${EMAIL_TIMEOUT_MS / 1000} seconds"))
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Test email failed", e)
            Result.failure(e)
        }
    }

    /**
     * Sends a custom alert email with specified subject and body.
     * Used for security alerts like cell tower warnings.
     */
    suspend fun sendAlert(subject: String, body: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (!prefs.isSmtpConfigured()) {
            return@withContext Result.failure(Exception("SMTP not configured"))
        }
        
        if (!networkDetector.isConnected()) {
            return@withContext Result.failure(Exception("No network connection"))
        }
        
        val recipient = prefs.alertRecipient ?: prefs.smtpUsername
            ?: return@withContext Result.failure(Exception("No recipient configured"))
        
        val customAlert = AlertHistoryEntity(
            recipientEmail = recipient,
            subject = "[SentinelGuard] $subject",
            body = body + getEmailFooter(),
            status = AlertStatus.SENDING
        )
        
        try {
            withTimeout(EMAIL_TIMEOUT_MS) {
                sendEmail(customAlert)
                android.util.Log.i(TAG, "Custom alert sent: $subject")
                Result.success(Unit)
            }
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            android.util.Log.e(TAG, "Custom alert timeout")
            Result.failure(Exception("Email send timed out"))
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Custom alert failed", e)
            Result.failure(e)
        }
    }

    /**
     * Sends email via SMTP with timeout configuration.
     */
    private fun sendEmail(alert: AlertHistoryEntity) {
        val host = prefs.smtpHost ?: throw Exception("SMTP host not configured")
        val port = prefs.smtpPort
        val username = prefs.smtpUsername ?: throw Exception("SMTP username not configured")
        val password = prefs.smtpPassword ?: throw Exception("SMTP password not configured")
        val useTls = prefs.smtpUseTls

        val props = Properties().apply {
            put("mail.smtp.host", host)
            put("mail.smtp.port", port.toString())
            put("mail.smtp.auth", "true")
            
            if (useTls) {
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.ssl.trust", host)
                put("mail.smtp.ssl.protocols", "TLSv1.2")
            }
            
            // Timeout configurations
            put("mail.smtp.connectiontimeout", SMTP_CONNECT_TIMEOUT_MS)
            put("mail.smtp.timeout", SMTP_READ_TIMEOUT_MS)
            put("mail.smtp.writetimeout", SMTP_WRITE_TIMEOUT_MS)
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })

        val message = MimeMessage(session).apply {
            setFrom(InternetAddress(username, "SentinelGuard Security"))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(alert.recipientEmail))
            subject = alert.subject
            setText(alert.body)
        }

        Transport.send(message)
    }

    /**
     * Handles send failure with retry scheduling.
     */
    private suspend fun handleSendFailure(alert: AlertHistoryEntity, error: String?) {
        val newRetryCount = alert.retryCount + 1
        
        if (newRetryCount >= MAX_RETRY_COUNT) {
            // Permanent failure
            alertHistoryDao.updateStatusFailed(
                id = alert.id,
                status = AlertStatus.FAILED_PERMANENT,
                error = error,
                nextRetry = null
            )
            android.util.Log.w(TAG, "Alert permanently failed after $MAX_RETRY_COUNT attempts")
        } else {
            // Schedule retry
            val delayIndex = (newRetryCount - 1).coerceIn(0, RETRY_DELAYS_MS.size - 1)
            val nextRetry = System.currentTimeMillis() + RETRY_DELAYS_MS[delayIndex]
            
            alertHistoryDao.updateStatusFailed(
                id = alert.id,
                status = AlertStatus.FAILED,
                error = error,
                nextRetry = nextRetry
            )
            android.util.Log.i(TAG, "Alert scheduled for retry at ${Date(nextRetry)}")
        }
    }

    /**
     * Builds email subject for incident.
     */
    private fun buildSubject(incident: IncidentEntity): String {
        val emoji = when (incident.severity.name) {
            "CRITICAL" -> "🚨"
            "HIGH" -> "⚠️"
            else -> "📢"
        }
        return "$emoji [SentinelGuard] ${incident.severity.name} Security Alert"
    }

    /**
     * Builds email body for incident with app version footer.
     */
    private fun buildBody(incident: IncidentEntity): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US)
        val timestamp = dateFormat.format(Date(incident.timestamp))
        
        val locationInfo = incident.location?.let { loc ->
            try {
                val json = org.json.JSONObject(loc)
                val lat = json.getDouble("lat")
                val lng = json.getDouble("lng")
                """
                📍 Location:
                   Latitude: $lat
                   Longitude: $lng
                   Maps: https://maps.google.com/?q=$lat,$lng
                """.trimIndent()
            } catch (e: Exception) {
                "📍 Location: Unknown"
            }
        } ?: "📍 Location: Not available"

        return """
            ══════════════════════════════════════
            🔐 SENTINELGUARD SECURITY ALERT
            ══════════════════════════════════════
            
            Severity: ${incident.severity.name}
            Time: $timestamp
            Risk Score: ${incident.riskScore}/100
            
            $locationInfo
            
            ───────────────────────────────────────
            SUMMARY
            ───────────────────────────────────────
            ${incident.summary}
            
            ───────────────────────────────────────
            ACTIONS TAKEN
            ───────────────────────────────────────
            ${parseActions(incident.actionsTaken)}
            
            ══════════════════════════════════════
            WHAT TO DO
            ══════════════════════════════════════
            
            ✅ If this was YOU:
            1. Open the SentinelGuard app
            2. Authenticate with your biometric or password
            3. Review the incident in the Timeline
            
            ❌ If this was NOT you:
            1. Your device may be compromised
            2. Change important passwords immediately
            3. Check for unauthorized access to accounts
            4. Consider reporting the device as stolen
        """.trimIndent() + getEmailFooter()
    }

    private fun parseActions(actionsJson: String): String {
        return try {
            val array = org.json.JSONArray(actionsJson)
            (0 until array.length()).joinToString("\n") { i ->
                "  • ${array.getString(i).replace("_", " ")}"
            }
        } catch (e: Exception) {
            "  • Unknown actions"
        }
    }
}
