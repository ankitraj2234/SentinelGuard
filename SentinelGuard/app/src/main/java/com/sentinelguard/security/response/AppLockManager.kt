package com.sentinelguard.security.response

import com.sentinelguard.data.local.preferences.SecurePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * AppLockManager: Manages App Lock State and Cooldowns
 * 
 * WHY THIS EXISTS:
 * When risk is elevated, the app locks and requires re-authentication.
 * Progressive cooldowns prevent brute-force attacks.
 * 
 * COOLDOWN PROGRESSION:
 * Attempt 1: 30 seconds
 * Attempt 2: 1 minute
 * Attempt 3: 5 minutes
 * Attempt 4: 15 minutes
 * Attempt 5+: 1 hour
 */
class AppLockManager(
    private val securePreferences: SecurePreferences
) {
    companion object {
        private val COOLDOWN_DURATIONS_MS = listOf(
            30 * 1000L,       // 30 seconds
            60 * 1000L,       // 1 minute
            5 * 60 * 1000L,   // 5 minutes
            15 * 60 * 1000L,  // 15 minutes
            60 * 60 * 1000L   // 1 hour
        )
        const val MAX_ATTEMPTS_BEFORE_EXTENDED = 5
    }

    private val _isLocked = MutableStateFlow(false)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private val _cooldownUntil = MutableStateFlow(0L)
    val cooldownUntil: StateFlow<Long> = _cooldownUntil.asStateFlow()

    private var failedAttempts = 0

    init {
        // Restore state from preferences
        _isLocked.value = securePreferences.isAppLocked
        _cooldownUntil.value = securePreferences.cooldownUntil
    }

    /**
     * Locks the app.
     */
    fun lock() {
        _isLocked.value = true
        securePreferences.isAppLocked = true
    }

    /**
     * Unlocks the app (after successful auth).
     */
    fun unlock() {
        _isLocked.value = false
        securePreferences.isAppLocked = false
        failedAttempts = 0
        clearCooldown()
    }

    /**
     * Records a failed unlock attempt and applies cooldown.
     * @return the total number of failed attempts
     */
    fun recordFailedAttempt(): Int {
        failedAttempts++
        applyCooldown()
        return failedAttempts
    }

    /**
     * Checks if currently in cooldown.
     */
    fun isInCooldown(): Boolean {
        val until = _cooldownUntil.value
        return until > 0 && System.currentTimeMillis() < until
    }

    /**
     * Gets remaining cooldown time in milliseconds.
     */
    fun getRemainingCooldownMs(): Long {
        val until = _cooldownUntil.value
        val remaining = until - System.currentTimeMillis()
        return if (remaining > 0) remaining else 0
    }

    /**
     * Gets remaining cooldown formatted as string.
     */
    fun getRemainingCooldownFormatted(): String {
        val remainingMs = getRemainingCooldownMs()
        if (remainingMs <= 0) return ""

        val seconds = (remainingMs / 1000) % 60
        val minutes = (remainingMs / (1000 * 60)) % 60
        val hours = remainingMs / (1000 * 60 * 60)

        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }

    private fun applyCooldown() {
        val index = (failedAttempts - 1).coerceIn(0, COOLDOWN_DURATIONS_MS.lastIndex)
        val duration = COOLDOWN_DURATIONS_MS[index]
        val until = System.currentTimeMillis() + duration

        _cooldownUntil.value = until
        securePreferences.cooldownUntil = until
    }

    private fun clearCooldown() {
        _cooldownUntil.value = 0
        securePreferences.cooldownUntil = 0
    }

    /**
     * Checks if auth can be attempted (not in cooldown).
     */
    fun canAttemptAuth(): Boolean {
        return !isInCooldown()
    }

    /**
     * Gets current failed attempt count.
     */
    fun getFailedAttemptCount(): Int = failedAttempts

    /**
     * Forces extended lockout (for critical risk).
     */
    fun forceExtendedLockout() {
        val extendedDuration = COOLDOWN_DURATIONS_MS.last() // 1 hour
        val until = System.currentTimeMillis() + extendedDuration
        _cooldownUntil.value = until
        securePreferences.cooldownUntil = until
        lock()
    }
}
