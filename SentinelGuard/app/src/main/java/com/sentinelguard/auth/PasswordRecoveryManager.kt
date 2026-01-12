package com.sentinelguard.auth

import com.sentinelguard.data.local.preferences.SecurePreferences
import com.sentinelguard.domain.util.SecureIdGenerator
import java.security.SecureRandom

/**
 * PasswordRecoveryManager: Handles Password Reset Flow
 * 
 * WHY THIS EXISTS:
 * Prevents permanent lockout if user forgets password.
 * Uses email-based recovery codes with time expiry.
 * 
 * FLOW:
 * 1. User requests recovery
 * 2. 6-digit code generated, stored with 15-min expiry
 * 3. Code emailed to registered address
 * 4. User enters code
 * 5. If valid, user sets new password
 * 
 * SECURITY:
 * - Code expires in 15 minutes
 * - Max 3 attempts per code
 * - Rate limited (1 request per 5 minutes)
 */
class PasswordRecoveryManager(
    private val securePreferences: SecurePreferences
) {
    companion object {
        const val CODE_LENGTH = 6
        const val CODE_EXPIRY_MS = 15 * 60 * 1000L // 15 minutes
        const val RATE_LIMIT_MS = 5 * 60 * 1000L   // 5 minutes between requests
        const val MAX_ATTEMPTS = 3
    }

    private var currentCode: String? = null
    private var codeExpiry: Long = 0
    private var lastRequestTime: Long = 0
    private var attemptCount: Int = 0

    /**
     * Generates a new recovery code.
     * Returns null if rate limited.
     */
    fun generateRecoveryCode(): RecoveryCodeResult {
        val now = System.currentTimeMillis()

        // Rate limit check
        if (now - lastRequestTime < RATE_LIMIT_MS) {
            val waitSeconds = ((RATE_LIMIT_MS - (now - lastRequestTime)) / 1000).toInt()
            return RecoveryCodeResult.RateLimited(waitSeconds)
        }

        // Generate 6-digit numeric code
        val random = SecureRandom()
        val code = (0 until CODE_LENGTH)
            .map { random.nextInt(10) }
            .joinToString("")

        // Store with expiry
        currentCode = code
        codeExpiry = now + CODE_EXPIRY_MS
        lastRequestTime = now
        attemptCount = 0

        // Persist for crash recovery
        securePreferences.recoveryCode = code
        securePreferences.recoveryCodeExpiry = codeExpiry

        return RecoveryCodeResult.Success(code)
    }

    /**
     * Validates a recovery code entered by user.
     */
    fun validateCode(enteredCode: String): CodeValidationResult {
        val now = System.currentTimeMillis()

        // Load from preferences if needed
        if (currentCode == null) {
            currentCode = securePreferences.recoveryCode
            codeExpiry = securePreferences.recoveryCodeExpiry
        }

        // No active code
        if (currentCode == null) {
            return CodeValidationResult.NoActiveCode
        }

        // Expired
        if (now > codeExpiry) {
            clearCode()
            return CodeValidationResult.Expired
        }

        // Too many attempts
        attemptCount++
        if (attemptCount > MAX_ATTEMPTS) {
            clearCode()
            return CodeValidationResult.TooManyAttempts
        }

        // Validate
        return if (enteredCode == currentCode) {
            CodeValidationResult.Valid
        } else {
            CodeValidationResult.Invalid(MAX_ATTEMPTS - attemptCount)
        }
    }

    /**
     * Clears the current recovery code (after success or expiry).
     */
    fun clearCode() {
        currentCode = null
        codeExpiry = 0
        attemptCount = 0
        securePreferences.recoveryCode = null
        securePreferences.recoveryCodeExpiry = 0
    }

    /**
     * Checks if a recovery is in progress.
     */
    fun hasActiveRecovery(): Boolean {
        val code = currentCode ?: securePreferences.recoveryCode
        val expiry = if (codeExpiry > 0) codeExpiry else securePreferences.recoveryCodeExpiry
        return code != null && System.currentTimeMillis() < expiry
    }

    /**
     * Gets remaining time for current code in seconds.
     */
    fun getRemainingTimeSeconds(): Int {
        val expiry = if (codeExpiry > 0) codeExpiry else securePreferences.recoveryCodeExpiry
        val remaining = expiry - System.currentTimeMillis()
        return if (remaining > 0) (remaining / 1000).toInt() else 0
    }

    /**
     * Gets the registered recovery email.
     */
    fun getRecoveryEmail(): String? {
        return securePreferences.alertRecipient
    }

    /**
     * Builds recovery email content.
     */
    fun buildRecoveryEmailBody(code: String): String {
        return """
            SentinelGuard Password Recovery
            
            Your recovery code is: $code
            
            This code expires in 15 minutes.
            
            If you did not request this, ignore this email.
            Your password has NOT been changed.
            
            - SentinelGuard Security
        """.trimIndent()
    }
}

sealed class RecoveryCodeResult {
    data class Success(val code: String) : RecoveryCodeResult()
    data class RateLimited(val waitSeconds: Int) : RecoveryCodeResult()
}

sealed class CodeValidationResult {
    object Valid : CodeValidationResult()
    object NoActiveCode : CodeValidationResult()
    object Expired : CodeValidationResult()
    object TooManyAttempts : CodeValidationResult()
    data class Invalid(val remainingAttempts: Int) : CodeValidationResult()
}
