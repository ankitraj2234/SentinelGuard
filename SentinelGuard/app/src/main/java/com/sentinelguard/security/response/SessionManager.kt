package com.sentinelguard.security.response

import com.sentinelguard.data.local.preferences.SecurePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * SessionManager: Manages User Session State
 * 
 * WHY THIS EXISTS:
 * Tracks whether user is authenticated and manages session lifecycle.
 * On critical risk, session can be wiped requiring full re-auth.
 */
class SessionManager(
    private val securePreferences: SecurePreferences
) {
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _requiresBiometric = MutableStateFlow(false)
    val requiresBiometric: StateFlow<Boolean> = _requiresBiometric.asStateFlow()

    init {
        _isAuthenticated.value = securePreferences.isSessionActive
    }

    /**
     * Starts a new session after successful login.
     */
    fun startSession(userId: String) {
        securePreferences.startSession(userId)
        _isAuthenticated.value = true
        _requiresBiometric.value = false
    }

    /**
     * Ends the current session (logout).
     */
    fun endSession() {
        securePreferences.clearSession()
        _isAuthenticated.value = false
        _requiresBiometric.value = false
    }

    /**
     * Wipes session due to security threat.
     * User must re-authenticate.
     */
    fun wipeSession() {
        securePreferences.clearSession()
        _isAuthenticated.value = false
        _requiresBiometric.value = false
    }

    /**
     * Requires biometric verification for continued access.
     */
    fun requireBiometric() {
        _requiresBiometric.value = true
    }

    /**
     * Clears biometric requirement after successful verification.
     */
    fun clearBiometricRequirement() {
        _requiresBiometric.value = false
    }

    /**
     * Checks if user needs to authenticate.
     */
    fun needsAuthentication(): Boolean {
        return !_isAuthenticated.value || _requiresBiometric.value
    }

    /**
     * Gets current user ID if authenticated.
     */
    fun getCurrentUserId(): String? {
        return if (_isAuthenticated.value) {
            securePreferences.currentUserId
        } else null
    }
}
