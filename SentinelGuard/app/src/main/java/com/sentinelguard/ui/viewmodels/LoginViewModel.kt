package com.sentinelguard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinelguard.auth.AuthRepository
import com.sentinelguard.auth.AuthResult
import com.sentinelguard.auth.BiometricAuthManager
import com.sentinelguard.security.collector.SignalCollector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val lockoutUntil: Long? = null,
    val biometricAvailable: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val biometricAuthManager: BiometricAuthManager,
    private val signalCollector: SignalCollector
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        checkBiometricAvailability()
        loadUserEmail()
    }

    private fun checkBiometricAvailability() {
        viewModelScope.launch {
            // Check BOTH device capability AND user setting
            val deviceSupports = biometricAuthManager.isBiometricAvailable()
            val userEnabled = authRepository.getCurrentUser()?.biometricEnabled ?: false
            
            _uiState.update { it.copy(
                biometricAvailable = deviceSupports && userEnabled
            )}
        }
    }

    private fun loadUserEmail() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            user?.let { 
                _uiState.update { it.copy(email = user.email) }
            }
        }
    }

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.authenticate(
                _uiState.value.email,
                _uiState.value.password
            )

            when (result) {
                is AuthResult.Success -> {
                    signalCollector.recordLoginAttempt(true)
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                is AuthResult.InvalidCredentials -> {
                    signalCollector.recordLoginAttempt(false)
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = "Invalid email or password"
                    )}
                }
                is AuthResult.LockedOut -> {
                    signalCollector.recordLoginAttempt(false)
                    _uiState.update { it.copy(
                        isLoading = false,
                        lockoutUntil = result.unlockTime,
                        errorMessage = "Account temporarily locked"
                    )}
                }
                is AuthResult.UserNotFound -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = "Account not found"
                    )}
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )}
                }
            }
        }
    }

    fun onBiometricSuccess(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // Check if user has biometric enabled
            val user = authRepository.getCurrentUser()
            if (user?.biometricEnabled == true) {
                // Auto-login the user
                authRepository.startSession(user.id)
                signalCollector.recordLoginAttempt(true)
                onSuccess()
            } else {
                _uiState.update { it.copy(
                    errorMessage = "Biometric login not enabled for this account"
                )}
            }
        }
    }
    
    fun onBiometricFailed(errorMessage: String) {
        viewModelScope.launch {
            signalCollector.recordLoginAttempt(false)
        }
        _uiState.update { it.copy(
            errorMessage = "Biometric authentication failed: $errorMessage"
        )}
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
