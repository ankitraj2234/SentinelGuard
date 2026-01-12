package com.sentinelguard.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result of biometric authentication attempt.
 */
sealed class BiometricResult {
    data object Success : BiometricResult()
    data object Cancelled : BiometricResult()
    data class Failed(val errorCode: Int, val errorMessage: String) : BiometricResult()
    data object NotAvailable : BiometricResult()
    data object NotEnrolled : BiometricResult()
}

/**
 * Biometric capabilities of the device.
 */
enum class BiometricCapability {
    AVAILABLE,
    NOT_AVAILABLE,
    HARDWARE_NOT_PRESENT,
    NOT_ENROLLED
}

/**
 * Manages biometric authentication operations.
 * 
 * Supports:
 * - Fingerprint
 * - Face recognition
 * - Device credential fallback (optional)
 */
@Singleton
class BiometricAuthManager @Inject constructor(
    private val context: Context
) {

    private val biometricManager = BiometricManager.from(context)

    /**
     * Checks if biometric authentication is available on this device.
     */
    fun checkBiometricCapability(): BiometricCapability {
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricCapability.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricCapability.HARDWARE_NOT_PRESENT
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricCapability.NOT_ENROLLED
            else -> BiometricCapability.NOT_AVAILABLE
        }
    }

    /**
     * Returns true if biometric authentication can be used.
     */
    fun isBiometricAvailable(): Boolean {
        return checkBiometricCapability() == BiometricCapability.AVAILABLE
    }

    /**
     * Shows biometric prompt and returns result.
     * 
     * @param activity Fragment activity to show prompt
     * @param title Prompt title
     * @param subtitle Prompt subtitle
     * @param negativeButtonText Text for fallback button
     * @return BiometricResult via callback
     */
    fun authenticate(
        activity: FragmentActivity,
        title: String = "Authenticate",
        subtitle: String = "Confirm your identity",
        negativeButtonText: String = "Use Password",
        onResult: (BiometricResult) -> Unit
    ) {
        if (!isBiometricAvailable()) {
            onResult(BiometricResult.NotAvailable)
            return
        }

        val executor = ContextCompat.getMainExecutor(context)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onResult(BiometricResult.Success)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // This is called for each failed attempt, not the final result
                // Don't report failure here as user may retry
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                        onResult(BiometricResult.Cancelled)
                    }
                    BiometricPrompt.ERROR_LOCKOUT,
                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                        onResult(BiometricResult.Failed(errorCode, errString.toString()))
                    }
                    else -> {
                        onResult(BiometricResult.Failed(errorCode, errString.toString()))
                    }
                }
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .setConfirmationRequired(false)
            .build()

        val biometricPrompt = BiometricPrompt(activity, executor, callback)
        biometricPrompt.authenticate(promptInfo)
    }

    /**
     * Shows biometric prompt for strong authentication (e.g., during high-risk events).
     * Does not allow device credential fallback.
     */
    fun authenticateStrong(
        activity: FragmentActivity,
        title: String = "Security Verification Required",
        subtitle: String = "High-risk activity detected. Please verify your identity.",
        onResult: (BiometricResult) -> Unit
    ) {
        if (!isBiometricAvailable()) {
            onResult(BiometricResult.NotAvailable)
            return
        }

        val executor = ContextCompat.getMainExecutor(context)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onResult(BiometricResult.Success)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                        onResult(BiometricResult.Cancelled)
                    }
                    else -> {
                        onResult(BiometricResult.Failed(errorCode, errString.toString()))
                    }
                }
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText("Cancel")
            .setConfirmationRequired(true)
            .build()

        val biometricPrompt = BiometricPrompt(activity, executor, callback)
        biometricPrompt.authenticate(promptInfo)
    }
}
