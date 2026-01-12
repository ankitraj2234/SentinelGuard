package com.sentinelguard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinelguard.backup.SettingsBackupManager
import com.sentinelguard.data.local.preferences.SecurePreferences
import com.sentinelguard.data.preferences.SecurePreferencesManager
import com.sentinelguard.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Settings screen state
 */
data class SettingsUiState(
    val alertEmail: String = "",
    val cooldownMinutes: Int = 30,
    val biometricEnabled: Boolean = false,
    val dataRetentionDays: Int = 90,
    val learningPeriodDays: Int = 7,
    val intruderCaptureEnabled: Boolean = false,
    // Theme & Auto-lock
    val themeMode: String = "dark",
    val autoLockMinutes: Int = 5,
    // Backup state
    val isBackingUp: Boolean = false,
    val isRestoring: Boolean = false,
    val lastBackupDate: String? = null,
    val showRestorePicker: Boolean = false,
    // Save state
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * SettingsViewModel: Manages user settings state and persistence.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val securePreferences: SecurePreferences,
    private val securePrefsManager: SecurePreferencesManager,
    private val userRepository: UserRepository,
    private val backupManager: SettingsBackupManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val user = userRepository.getUser()
            _uiState.update { state ->
                state.copy(
                    alertEmail = securePreferences.alertRecipient ?: "",
                    cooldownMinutes = securePreferences.cooldownMinutes,
                    biometricEnabled = user?.biometricEnabled ?: false,
                    dataRetentionDays = securePrefsManager.dataRetentionDays,
                    learningPeriodDays = securePreferences.learningPeriodDays,
                    intruderCaptureEnabled = securePreferences.isIntruderCaptureEnabled,
                    themeMode = securePrefsManager.themeMode,
                    autoLockMinutes = securePrefsManager.autoLockTimeoutMinutes
                )
            }
        }
    }

    fun updateAlertEmail(email: String) {
        _uiState.update { it.copy(alertEmail = email, saveSuccess = false) }
    }

    fun updateCooldownMinutes(minutes: Int) {
        _uiState.update { it.copy(cooldownMinutes = minutes, saveSuccess = false) }
    }

    fun updateBiometricEnabled(enabled: Boolean) {
        _uiState.update { it.copy(biometricEnabled = enabled, saveSuccess = false) }
    }

    fun updateDataRetentionDays(days: Int) {
        _uiState.update { it.copy(dataRetentionDays = days, saveSuccess = false) }
    }

    fun updateLearningPeriodDays(days: Int) {
        _uiState.update { it.copy(learningPeriodDays = days, saveSuccess = false) }
    }

    fun updateIntruderCaptureEnabled(enabled: Boolean) {
        _uiState.update { it.copy(intruderCaptureEnabled = enabled, saveSuccess = false) }
    }
    
    fun updateThemeMode(mode: String) {
        _uiState.update { it.copy(themeMode = mode, saveSuccess = false) }
        // Save immediately for instant feedback
        securePrefsManager.themeMode = mode
    }
    
    fun updateAutoLockMinutes(minutes: Int) {
        _uiState.update { it.copy(autoLockMinutes = minutes, saveSuccess = false) }
        // Save immediately
        securePrefsManager.autoLockTimeoutMinutes = minutes
    }
    
    // ============ Backup & Restore ============
    
    fun backupSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isBackingUp = true) }
            
            val result = backupManager.saveBackupToDownloads()
            
            when (result) {
                is SettingsBackupManager.BackupResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isBackingUp = false,
                            lastBackupDate = dateFormat.format(Date()),
                            saveSuccess = true
                        )
                    }
                }
                is SettingsBackupManager.BackupResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isBackingUp = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
    
    fun triggerRestore() {
        _uiState.update { it.copy(showRestorePicker = true) }
    }
    
    fun restoreFromUri(uri: android.net.Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRestoring = true, showRestorePicker = false) }
            
            val result = backupManager.restoreFromUri(uri)
            
            when (result) {
                is SettingsBackupManager.BackupResult.Success -> {
                    // Reload settings after restore
                    loadSettings()
                    _uiState.update { 
                        it.copy(
                            isRestoring = false,
                            saveSuccess = true
                        )
                    }
                }
                is SettingsBackupManager.BackupResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isRestoring = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
    
    fun dismissRestorePicker() {
        _uiState.update { it.copy(showRestorePicker = false) }
    }

    fun saveSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            
            try {
                // Save preferences
                securePreferences.alertRecipient = _uiState.value.alertEmail.ifBlank { null }
                securePreferences.alertEmail = _uiState.value.alertEmail.ifBlank { null }
                securePreferences.cooldownMinutes = _uiState.value.cooldownMinutes
                securePreferences.learningPeriodDays = _uiState.value.learningPeriodDays
                securePreferences.isIntruderCaptureEnabled = _uiState.value.intruderCaptureEnabled
                securePrefsManager.dataRetentionDays = _uiState.value.dataRetentionDays
                securePrefsManager.themeMode = _uiState.value.themeMode
                securePrefsManager.autoLockTimeoutMinutes = _uiState.value.autoLockMinutes
                
                // Update biometric for user
                val user = userRepository.getUser()
                if (user != null) {
                    userRepository.enableBiometric(user.id, _uiState.value.biometricEnabled)
                }
                
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(isSaving = false, errorMessage = "Failed to save settings: ${e.message}") 
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}

