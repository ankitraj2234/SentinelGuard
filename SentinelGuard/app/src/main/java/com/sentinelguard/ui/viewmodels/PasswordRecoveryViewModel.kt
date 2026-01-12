package com.sentinelguard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinelguard.auth.AuthRepository
import com.sentinelguard.email.EmailService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.SecureRandom
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

/**
 * Password recovery UI state
 */
data class PasswordRecoveryUiState(
    val step: RecoveryStep = RecoveryStep.EMAIL_INPUT,
    val email: String = "",
    val code: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val codeExpiresAt: Long = 0
)

enum class RecoveryStep {
    EMAIL_INPUT,
    CODE_VERIFICATION,
    NEW_PASSWORD,
    SUCCESS
}

/**
 * PasswordRecoveryViewModel: Handles the password recovery flow.
 * 
 * Flow:
 * 1. User enters email
 * 2. System sends 6-digit code to email
 * 3. User enters code
 * 4. User sets new password
 */
@HiltViewModel
class PasswordRecoveryViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val emailService: EmailService
) : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordRecoveryUiState())
    val uiState: StateFlow<PasswordRecoveryUiState> = _uiState.asStateFlow()

    private var generatedCode: String = ""
    private val codeValidityMs = 10 * 60 * 1000L // 10 minutes

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun updateCode(code: String) {
        _uiState.update { it.copy(code = code, errorMessage = null) }
    }

    fun updateNewPassword(password: String) {
        _uiState.update { it.copy(newPassword = password, errorMessage = null) }
    }

    fun updateConfirmPassword(password: String) {
        _uiState.update { it.copy(confirmPassword = password, errorMessage = null) }
    }

    /**
     * Step 1: Send recovery code to email
     */
    fun sendRecoveryCode() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val email = _uiState.value.email.trim()
            
            // Validate email exists
            if (!authRepository.isEmailRegistered(email)) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "No account found with this email"
                    ) 
                }
                return@launch
            }
            
            // Check if email service is configured
            if (!emailService.hasCredentials()) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Email service not configured"
                    ) 
                }
                return@launch
            }
            
            // Generate 6-digit code
            generatedCode = generateSecureCode()
            val expiresAt = System.currentTimeMillis() + codeValidityMs
            
            // Send email
            val result = emailService.sendRecoveryCode(email, generatedCode)
            
            if (result.isSuccess) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        step = RecoveryStep.CODE_VERIFICATION,
                        codeExpiresAt = expiresAt,
                        successMessage = "Recovery code sent to your email"
                    ) 
                }
            } else {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Failed to send email: ${result.exceptionOrNull()?.message}"
                    ) 
                }
            }
        }
    }

    /**
     * Step 2: Verify the code
     */
    fun verifyCode() {
        val enteredCode = _uiState.value.code.trim()
        
        // Check expiration
        if (System.currentTimeMillis() > _uiState.value.codeExpiresAt) {
            _uiState.update { 
                it.copy(errorMessage = "Code has expired. Please request a new one.") 
            }
            return
        }
        
        // Verify code
        if (enteredCode == generatedCode) {
            _uiState.update { 
                it.copy(
                    step = RecoveryStep.NEW_PASSWORD,
                    errorMessage = null
                ) 
            }
        } else {
            _uiState.update { 
                it.copy(errorMessage = "Invalid code. Please try again.") 
            }
        }
    }

    /**
     * Step 3: Reset password
     */
    fun resetPassword() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val newPassword = _uiState.value.newPassword
            val confirmPassword = _uiState.value.confirmPassword
            
            // Validation
            if (newPassword.length < 8) {
                _uiState.update { 
                    it.copy(isLoading = false, errorMessage = "Password must be at least 8 characters") 
                }
                return@launch
            }
            
            if (newPassword != confirmPassword) {
                _uiState.update { 
                    it.copy(isLoading = false, errorMessage = "Passwords do not match") 
                }
                return@launch
            }
            
            // Reset password
            val result = authRepository.resetPassword(_uiState.value.email, newPassword)
            
            if (result.isSuccess) {
                // Clear the code for security
                generatedCode = ""
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        step = RecoveryStep.SUCCESS,
                        successMessage = "Password reset successfully!"
                    ) 
                }
            } else {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to reset password: ${result.exceptionOrNull()?.message}"
                    ) 
                }
            }
        }
    }

    /**
     * Go back to previous step
     */
    fun goBack() {
        val currentStep = _uiState.value.step
        val newStep = when (currentStep) {
            RecoveryStep.CODE_VERIFICATION -> RecoveryStep.EMAIL_INPUT
            RecoveryStep.NEW_PASSWORD -> RecoveryStep.CODE_VERIFICATION
            else -> currentStep
        }
        _uiState.update { it.copy(step = newStep, errorMessage = null) }
    }

    /**
     * Resend the recovery code
     */
    fun resendCode() {
        generatedCode = ""
        _uiState.update { it.copy(code = "") }
        sendRecoveryCode()
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    private fun generateSecureCode(): String {
        val random = SecureRandom()
        return (100000 + random.nextInt(900000)).toString()
    }
}
