package com.sentinelguard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinelguard.auth.AuthRepository
import com.sentinelguard.domain.model.RiskLevel
import com.sentinelguard.data.preferences.SecurePreferencesManager
import com.sentinelguard.security.baseline.BaselineEngine
import com.sentinelguard.security.risk.RiskScoringEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val isLoading: Boolean = true,
    val isSetupComplete: Boolean = false,
    val isLoggedIn: Boolean = false,
    val hasCompletedOnboarding: Boolean = true, // Default true so we don't show onboarding prematurely
    val riskScore: Int = 0,
    val riskLevel: RiskLevel = RiskLevel.NORMAL,
    val learningProgress: Float = 0f,
    val isLearningComplete: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val riskScoringEngine: RiskScoringEngine,
    private val baselineEngine: BaselineEngine,
    private val securePrefs: SecurePreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        initialize()
    }

    private fun initialize() {
        viewModelScope.launch {
            val hasCompletedOnboarding = securePrefs.hasCompletedOnboarding
            val isSetupComplete = securePrefs.isSetupComplete
            val hasAccount = authRepository.hasAccount()
            val isSessionActive = securePrefs.isSessionActive

            _uiState.update { it.copy(
                isLoading = false,
                hasCompletedOnboarding = hasCompletedOnboarding,
                isSetupComplete = isSetupComplete && hasAccount,
                isLoggedIn = isSessionActive
            )}

            if (isSessionActive) {
                assessRisk()
            }
        }
    }

    fun onAppOpen() {
        viewModelScope.launch {
            if (_uiState.value.isLoggedIn) {
                assessRisk()
            }
        }
    }

    private suspend fun assessRisk() {
        baselineEngine.updateBaselines()
        val riskScore = riskScoringEngine.calculateRiskScore()
        val learningProgress = baselineEngine.getLearningProgress()
        val isLearningComplete = baselineEngine.isLearningComplete()

        _uiState.update { it.copy(
            riskScore = riskScore.totalScore,
            riskLevel = riskScore.level,
            learningProgress = learningProgress,
            isLearningComplete = isLearningComplete
        )}
    }

    fun getStartDestination(): String {
        return when {
            !securePrefs.hasCompletedOnboarding -> "onboarding"
            !securePrefs.isSetupComplete -> "setup"
            !securePrefs.isSessionActive -> "login"
            securePrefs.isAppLocked -> "lock"
            else -> "dashboard"
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { it.copy(isLoggedIn = false) }
        }
    }
}
