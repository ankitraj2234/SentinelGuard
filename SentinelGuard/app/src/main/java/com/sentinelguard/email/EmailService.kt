package com.sentinelguard.email

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.security.KeyStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Properties
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
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
 * EmailService: Sends emails using JavaMail with Gmail SMTP.
 * 
 * Production Features:
 * - Network connectivity check before sending
 * - Configurable timeout for operations
 * - App version included in all emails for debugging
 * - Encrypted credential storage using Android Keystore
 */
@Singleton
class EmailService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "EmailService"
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "SentinelGuardEmailKey"
        private const val PREFS_NAME = "email_service_prefs"
        private const val PREF_EMAIL = "encrypted_email"
        private const val PREF_PASSWORD = "encrypted_password"
        private const val PREF_IV_EMAIL = "iv_email"
        private const val PREF_IV_PASSWORD = "iv_password"
        
        // SMTP Configuration for Gmail
        private const val SMTP_HOST = "smtp.gmail.com"
        private const val SMTP_PORT = "587"
        
        // Timeout for email operations (30 seconds)
        private const val EMAIL_TIMEOUT_MS = 30_000L
        
        // Connection timeout for SMTP (15 seconds)
        private const val SMTP_CONNECT_TIMEOUT_MS = "15000"
        private const val SMTP_READ_TIMEOUT_MS = "15000"
        private const val SMTP_WRITE_TIMEOUT_MS = "15000"
    }
    
    private val keyStore: KeyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
        load(null)
    }
    
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
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
     * Checks if network is available.
     */
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected == true
        }
    }
    
    /**
     * Stores email credentials securely encrypted with Keystore.
     */
    fun storeCredentials(email: String, appPassword: String) {
        ensureKeyExists()
        
        val (encEmail, ivEmail) = encrypt(email.toByteArray())
        val (encPass, ivPass) = encrypt(appPassword.toByteArray())
        
        prefs.edit()
            .putString(PREF_EMAIL, Base64.encodeToString(encEmail, Base64.NO_WRAP))
            .putString(PREF_PASSWORD, Base64.encodeToString(encPass, Base64.NO_WRAP))
            .putString(PREF_IV_EMAIL, Base64.encodeToString(ivEmail, Base64.NO_WRAP))
            .putString(PREF_IV_PASSWORD, Base64.encodeToString(ivPass, Base64.NO_WRAP))
            .apply()
        
        Log.i(TAG, "Email credentials stored securely")
    }
    
    /**
     * Checks if credentials are configured.
     */
    fun hasCredentials(): Boolean {
        return prefs.getString(PREF_EMAIL, null) != null
    }
    
    /**
     * Clears stored credentials.
     */
    fun clearCredentials() {
        prefs.edit()
            .remove(PREF_EMAIL)
            .remove(PREF_PASSWORD)
            .remove(PREF_IV_EMAIL)
            .remove(PREF_IV_PASSWORD)
            .apply()
        
        Log.i(TAG, "Email credentials cleared")
    }
    
    /**
     * Creates SMTP properties with timeout configuration.
     */
    private fun createSmtpProperties(): Properties {
        return Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", SMTP_HOST)
            put("mail.smtp.port", SMTP_PORT)
            put("mail.smtp.ssl.protocols", "TLSv1.2")
            
            // Timeout configurations
            put("mail.smtp.connectiontimeout", SMTP_CONNECT_TIMEOUT_MS)
            put("mail.smtp.timeout", SMTP_READ_TIMEOUT_MS)
            put("mail.smtp.writetimeout", SMTP_WRITE_TIMEOUT_MS)
        }
    }
    
    /**
     * Sends an email asynchronously with network check and timeout.
     */
    suspend fun sendEmail(
        recipientEmail: String,
        subject: String,
        body: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        // Check network connectivity first
        if (!isNetworkAvailable()) {
            Log.w(TAG, "Cannot send email: No network connection")
            return@withContext Result.failure(EmailException.NoNetwork("No network connection available"))
        }
        
        try {
            // Apply timeout to entire operation
            withTimeout(EMAIL_TIMEOUT_MS) {
                val (senderEmail, password) = getCredentials()
                    ?: return@withTimeout Result.failure<Unit>(
                        EmailException.NoCredentials("Email credentials not configured")
                    )
                
                val props = createSmtpProperties()
                
                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(senderEmail, password)
                    }
                })
                
                // Add footer with app version and device info
                val fullBody = body + getEmailFooter()
                
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(senderEmail, "SentinelGuard Security"))
                    addRecipient(Message.RecipientType.TO, InternetAddress(recipientEmail))
                    setSubject(subject)
                    setText(fullBody)
                }
                
                Transport.send(message)
                Log.i(TAG, "Email sent successfully to $recipientEmail")
                Result.success(Unit)
            }
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            Log.e(TAG, "Email send timeout after ${EMAIL_TIMEOUT_MS}ms", e)
            Result.failure(EmailException.Timeout("Email send timed out after ${EMAIL_TIMEOUT_MS / 1000} seconds"))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send email", e)
            Result.failure(EmailException.SendFailed("Failed to send email: ${e.message}", e))
        }
    }
    
    /**
     * Sends an email with a file attachment (for intruder photos).
     * Includes network check, timeout, and app version.
     */
    suspend fun sendEmailWithAttachment(
        recipientEmail: String,
        subject: String,
        body: String,
        attachmentFile: java.io.File,
        attachmentName: String
    ): Boolean = withContext(Dispatchers.IO) {
        // Check network connectivity first
        if (!isNetworkAvailable()) {
            Log.w(TAG, "Cannot send email with attachment: No network connection")
            return@withContext false
        }
        
        try {
            // Apply timeout to entire operation
            withTimeout(EMAIL_TIMEOUT_MS) {
                val (senderEmail, password) = getCredentials() ?: return@withTimeout false
                
                val props = createSmtpProperties()
                
                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(senderEmail, password)
                    }
                })
                
                val message = MimeMessage(session)
                message.setFrom(InternetAddress(senderEmail, "SentinelGuard Security"))
                message.addRecipient(Message.RecipientType.TO, InternetAddress(recipientEmail))
                message.subject = subject
                
                // Create multipart message
                val multipart = javax.mail.internet.MimeMultipart()
                
                // Text part with footer
                val fullBody = body + getEmailFooter()
                val textPart = javax.mail.internet.MimeBodyPart()
                textPart.setText(fullBody)
                multipart.addBodyPart(textPart)
                
                // Attachment part
                val attachmentPart = javax.mail.internet.MimeBodyPart()
                val source = javax.activation.FileDataSource(attachmentFile)
                attachmentPart.dataHandler = javax.activation.DataHandler(source)
                attachmentPart.fileName = attachmentName
                multipart.addBodyPart(attachmentPart)
                
                message.setContent(multipart)
                
                Transport.send(message)
                Log.i(TAG, "Email with attachment sent successfully to $recipientEmail")
                true
            }
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            Log.e(TAG, "Email with attachment timeout after ${EMAIL_TIMEOUT_MS}ms", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send email with attachment", e)
            false
        }
    }
    
    /**
     * Sends a password recovery code to the user's email.
     * Includes network check, timeout, and app version.
     */
    suspend fun sendRecoveryCode(userEmail: String, code: String): Result<Unit> {
        val subject = "🔐 SentinelGuard - Password Recovery Code"
        val body = """
            |Your SentinelGuard password recovery code is:
            |
            |    $code
            |
            |⏳ This code expires in 10 minutes.
            |
            |⚠️ If you did not request this code, please ignore this email
            |   and consider changing your password immediately.
        """.trimMargin()
        
        return sendEmail(userEmail, subject, body)
    }
    
    /**
     * Sends a test email to verify configuration.
     */
    suspend fun sendTestEmail(recipientEmail: String): Result<Unit> {
        val subject = "✅ SentinelGuard - Email Configuration Test"
        val body = """
            |Your SentinelGuard email configuration is working correctly!
            |
            |This is a test email to confirm that security alerts
            |will be delivered to this address.
            |
            |You can now receive:
            |• Security alerts when threats are detected
            |• Intruder photos when unauthorized access occurs
            |• Password recovery codes
        """.trimMargin()
        
        return sendEmail(recipientEmail, subject, body)
    }
    
    private fun getCredentials(): Pair<String, String>? {
        val encEmail = prefs.getString(PREF_EMAIL, null) ?: return null
        val encPass = prefs.getString(PREF_PASSWORD, null) ?: return null
        val ivEmail = prefs.getString(PREF_IV_EMAIL, null) ?: return null
        val ivPass = prefs.getString(PREF_IV_PASSWORD, null) ?: return null
        
        return try {
            val email = String(decrypt(
                Base64.decode(encEmail, Base64.NO_WRAP),
                Base64.decode(ivEmail, Base64.NO_WRAP)
            ))
            val password = String(decrypt(
                Base64.decode(encPass, Base64.NO_WRAP),
                Base64.decode(ivPass, Base64.NO_WRAP)
            ))
            Pair(email, password)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decrypt credentials", e)
            null
        }
    }
    
    private fun ensureKeyExists() {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEYSTORE_PROVIDER
            )
            val keySpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
            
            keyGenerator.init(keySpec)
            keyGenerator.generateKey()
        }
    }
    
    private fun encrypt(data: ByteArray): Pair<ByteArray, ByteArray> {
        val key = keyStore.getKey(KEY_ALIAS, null) as SecretKey
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return Pair(cipher.doFinal(data), cipher.iv)
    }
    
    private fun decrypt(data: ByteArray, iv: ByteArray): ByteArray {
        val key = keyStore.getKey(KEY_ALIAS, null) as SecretKey
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        return cipher.doFinal(data)
    }
}

/**
 * Custom email exceptions for better error handling.
 */
sealed class EmailException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NoNetwork(message: String) : EmailException(message)
    class NoCredentials(message: String) : EmailException(message)
    class Timeout(message: String) : EmailException(message)
    class SendFailed(message: String, cause: Throwable) : EmailException(message, cause)
}
