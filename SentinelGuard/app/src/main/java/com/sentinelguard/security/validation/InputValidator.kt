package com.sentinelguard.security.validation

import java.util.regex.Pattern

/**
 * InputValidator: Comprehensive input validation following OWASP best practices.
 * 
 * Provides schema-based validation for all user inputs:
 * - Email validation (RFC 5322 compliant)
 * - Password strength (complexity requirements)
 * - Filename sanitization
 * - API key format validation
 * - Recovery code validation
 * - Cell tower ID validation
 */
object InputValidator {
    
    // ============ Email Validation ============
    
    /**
     * RFC 5322 compliant email pattern
     * More strict than basic patterns to prevent injection attacks
     */
    private val EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9.!#\$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*\$"
    )
    
    private const val MAX_EMAIL_LENGTH = 254 // RFC 5321 limit
    private const val MIN_EMAIL_LENGTH = 5   // a@b.c minimum
    
    data class EmailValidationResult(
        val isValid: Boolean,
        val error: String? = null
    )
    
    /**
     * Validates email format with comprehensive checks
     */
    fun validateEmail(email: String): EmailValidationResult {
        val trimmed = email.trim()
        
        // Length checks
        if (trimmed.isEmpty()) {
            return EmailValidationResult(false, "Email is required")
        }
        if (trimmed.length < MIN_EMAIL_LENGTH) {
            return EmailValidationResult(false, "Email is too short")
        }
        if (trimmed.length > MAX_EMAIL_LENGTH) {
            return EmailValidationResult(false, "Email exceeds maximum length of $MAX_EMAIL_LENGTH characters")
        }
        
        // Format check
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            return EmailValidationResult(false, "Invalid email format")
        }
        
        // Check for suspicious patterns (potential injection)
        if (trimmed.contains("..") || trimmed.startsWith(".") || trimmed.contains("@.")) {
            return EmailValidationResult(false, "Invalid email format")
        }
        
        return EmailValidationResult(true)
    }
    
    // ============ Password Validation ============
    
    private const val MIN_PASSWORD_LENGTH = 8
    private const val MAX_PASSWORD_LENGTH = 128
    
    data class PasswordValidationResult(
        val isValid: Boolean,
        val error: String? = null,
        val strength: PasswordStrength = PasswordStrength.WEAK
    )
    
    enum class PasswordStrength {
        WEAK, FAIR, GOOD, STRONG
    }
    
    /**
     * Validates password with complexity requirements
     * 
     * Requirements:
     * - Minimum 8 characters
     * - At least 1 uppercase letter
     * - At least 1 lowercase letter  
     * - At least 1 digit
     * - At least 1 special character (recommended but not required)
     */
    fun validatePassword(password: String): PasswordValidationResult {
        // Length checks
        if (password.length < MIN_PASSWORD_LENGTH) {
            return PasswordValidationResult(
                false, 
                "Password must be at least $MIN_PASSWORD_LENGTH characters"
            )
        }
        if (password.length > MAX_PASSWORD_LENGTH) {
            return PasswordValidationResult(
                false,
                "Password exceeds maximum length of $MAX_PASSWORD_LENGTH characters"
            )
        }
        
        // Complexity checks
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }
        
        // Calculate strength
        var strengthScore = 0
        if (hasUppercase) strengthScore++
        if (hasLowercase) strengthScore++
        if (hasDigit) strengthScore++
        if (hasSpecial) strengthScore++
        if (password.length >= 12) strengthScore++
        if (password.length >= 16) strengthScore++
        
        val strength = when {
            strengthScore >= 5 -> PasswordStrength.STRONG
            strengthScore >= 4 -> PasswordStrength.GOOD
            strengthScore >= 3 -> PasswordStrength.FAIR
            else -> PasswordStrength.WEAK
        }
        
        // Require at least uppercase, lowercase, and digit
        if (!hasUppercase) {
            return PasswordValidationResult(
                false,
                "Password must contain at least one uppercase letter",
                strength
            )
        }
        if (!hasLowercase) {
            return PasswordValidationResult(
                false,
                "Password must contain at least one lowercase letter",
                strength
            )
        }
        if (!hasDigit) {
            return PasswordValidationResult(
                false,
                "Password must contain at least one digit",
                strength
            )
        }
        
        // Check for common weak patterns
        val lowerPassword = password.lowercase()
        val commonPatterns = listOf("password", "123456", "qwerty", "abc123", "admin")
        if (commonPatterns.any { lowerPassword.contains(it) }) {
            return PasswordValidationResult(
                false,
                "Password contains a common weak pattern",
                PasswordStrength.WEAK
            )
        }
        
        return PasswordValidationResult(true, null, strength)
    }
    
    /**
     * Validates that two passwords match
     */
    fun validatePasswordMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }
    
    // ============ Filename Sanitization ============
    
    private const val MAX_FILENAME_LENGTH = 200
    private val UNSAFE_FILENAME_CHARS = Regex("[^a-zA-Z0-9._\\-]")
    
    data class FilenameValidationResult(
        val isValid: Boolean,
        val sanitizedName: String,
        val error: String? = null
    )
    
    /**
     * Sanitizes filename to prevent path traversal and injection attacks
     */
    fun sanitizeFilename(filename: String): FilenameValidationResult {
        if (filename.isBlank()) {
            return FilenameValidationResult(false, "file", "Filename cannot be empty")
        }
        
        // Remove path separators (prevent path traversal)
        var sanitized = filename
            .replace("/", "_")
            .replace("\\", "_")
            .replace("..", "_") // Prevent directory traversal
        
        // Replace unsafe characters
        sanitized = UNSAFE_FILENAME_CHARS.replace(sanitized, "_")
        
        // Collapse multiple underscores
        sanitized = sanitized.replace(Regex("_+"), "_")
        
        // Trim underscores from start/end
        sanitized = sanitized.trim('_')
        
        // Truncate if too long
        if (sanitized.length > MAX_FILENAME_LENGTH) {
            sanitized = sanitized.take(MAX_FILENAME_LENGTH)
        }
        
        // Ensure not empty after sanitization
        if (sanitized.isEmpty()) {
            sanitized = "file_${System.currentTimeMillis()}"
        }
        
        return FilenameValidationResult(true, sanitized)
    }
    
    // ============ Recovery Code Validation ============
    
    private const val RECOVERY_CODE_LENGTH = 16
    private val RECOVERY_CODE_PATTERN = Regex("^[A-Z0-9]{16}\$")
    
    /**
     * Validates recovery code format
     */
    fun validateRecoveryCode(code: String): Boolean {
        val cleaned = code.uppercase().replace("-", "").replace(" ", "")
        return cleaned.length == RECOVERY_CODE_LENGTH && 
               RECOVERY_CODE_PATTERN.matches(cleaned)
    }
    
    /**
     * Cleans recovery code input (removes dashes and spaces, uppercase)
     */
    fun cleanRecoveryCode(code: String): String {
        return code.uppercase().replace("-", "").replace(" ", "")
    }
    
    // ============ API Key Validation ============
    
    /**
     * Validates Google API key format (starts with AIza, 39 chars)
     */
    fun validateGoogleApiKey(key: String?): Boolean {
        if (key.isNullOrBlank()) return false
        return key.startsWith("AIza") && key.length == 39
    }
    
    // ============ Cell Tower ID Validation ============
    
    /**
     * Validates Cell ID is within reasonable range
     */
    fun validateCellId(cellId: Int): Boolean {
        // Cell IDs should be positive and within typical ranges
        return cellId > 0 && cellId <= Int.MAX_VALUE
    }
    
    /**
     * Validates LAC (Location Area Code)
     */
    fun validateLac(lac: Int): Boolean {
        return lac in 0..65535 // 16-bit unsigned
    }
    
    /**
     * Validates MCC (Mobile Country Code)
     */
    fun validateMcc(mcc: Int): Boolean {
        return mcc in 1..999 // 3 digits
    }
    
    /**
     * Validates MNC (Mobile Network Code)
     */
    fun validateMnc(mnc: Int): Boolean {
        return mnc in 0..999 // 2-3 digits
    }
    
    // ============ Generic Sanitization ============
    
    /**
     * Strips HTML/script tags from input (XSS prevention)
     */
    fun stripHtml(input: String): String {
        return input.replace(Regex("<[^>]*>"), "")
    }
    
    /**
     * Validates string length within bounds
     */
    fun validateLength(input: String, min: Int = 0, max: Int = Int.MAX_VALUE): Boolean {
        return input.length in min..max
    }
    
    /**
     * Checks if input contains only alphanumeric characters
     */
    fun isAlphanumeric(input: String): Boolean {
        return input.all { it.isLetterOrDigit() }
    }
}
