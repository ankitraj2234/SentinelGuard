package com.sentinelguard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinelguard.auth.AuthRepository
import com.sentinelguard.data.preferences.SecurePreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SetupUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val smtpHost: String = "smtp.gmail.com",  // Gmail default (most common)
    val smtpPort: String = "587",
    val smtpUsername: String = "",
    val smtpPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val securePrefs: SecurePreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, errorMessage = null) }
    }

    fun updateSmtpHost(host: String) {
        _uiState.update { it.copy(smtpHost = host) }
    }

    fun updateSmtpPort(port: String) {
        _uiState.update { it.copy(smtpPort = port) }
    }

    fun updateSmtpUsername(username: String) {
        _uiState.update { it.copy(smtpUsername = username) }
    }

    fun updateSmtpPassword(password: String) {
        _uiState.update { it.copy(smtpPassword = password) }
    }

    /**
     * Validate account creation inputs using OWASP-compliant InputValidator
     */
    fun validateAccountStep(): Boolean {
        val state = _uiState.value

        // SECURITY: Validate email with stricter RFC 5322 pattern
        val emailResult = com.sentinelguard.security.validation.InputValidator.validateEmail(state.email)
        if (!emailResult.isValid) {
            _uiState.update { it.copy(errorMessage = emailResult.error) }
            return false
        }

        // SECURITY: Validate password with complexity requirements
        // Requires: 8+ chars, uppercase, lowercase, digit
        val passwordResult = com.sentinelguard.security.validation.InputValidator.validatePassword(state.password)
        if (!passwordResult.isValid) {
            _uiState.update { it.copy(errorMessage = passwordResult.error) }
            return false
        }

        // Validate password match
        if (!com.sentinelguard.security.validation.InputValidator.validatePasswordMatch(
                state.password, 
                state.confirmPassword
            )) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match") }
            return false
        }

        return true
    }

    fun createAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val state = _uiState.value

                // Create account
                val result = authRepository.createAccount(state.email, state.password)
                
                result.onSuccess {
                    // Save SMTP config if provided
                    if (state.smtpHost.isNotBlank()) {
                        securePrefs.smtpHost = state.smtpHost
                        securePrefs.smtpPort = state.smtpPort.toIntOrNull() ?: 587
                        securePrefs.smtpUsername = state.smtpUsername
                        securePrefs.smtpPassword = state.smtpPassword
                    }

                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }.onFailure { error ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to create account"
                    )}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "An error occurred"
                )}
            }
        }
    }
}
