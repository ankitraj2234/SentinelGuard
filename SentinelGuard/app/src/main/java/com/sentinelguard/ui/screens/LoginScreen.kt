package com.sentinelguard.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.sentinelguard.auth.BiometricAuthManager
import com.sentinelguard.auth.BiometricResult
import com.sentinelguard.ui.components.AppLogo
import com.sentinelguard.ui.components.ElevatedGlassCard
import com.sentinelguard.ui.theme.*
import com.sentinelguard.ui.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onForgotPassword: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel(),
    biometricAuthManager: BiometricAuthManager? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // Get FragmentActivity for biometric prompt
    val activity = context as? FragmentActivity
    
    // Function to trigger biometric auth
    fun triggerBiometricAuth() {
        if (activity == null) return
        
        biometricAuthManager?.authenticate(
            activity = activity,
            title = "Unlock SentinelGuard",
            subtitle = "Use your fingerprint or face to login",
            negativeButtonText = "Use Password"
        ) { result ->
            when (result) {
                is BiometricResult.Success -> {
                    viewModel.onBiometricSuccess(onLoginSuccess)
                }
                is BiometricResult.Cancelled -> {
                    // User cancelled, do nothing
                }
                is BiometricResult.Failed -> {
                    viewModel.onBiometricFailed(result.errorMessage)
                }
                is BiometricResult.NotAvailable,
                is BiometricResult.NotEnrolled -> {
                    // Should not happen since we check availability
                }
            }
        }
    }
    
    // Auto-trigger biometric on launch if available
    LaunchedEffect(uiState.biometricAvailable) {
        if (uiState.biometricAvailable && activity != null && biometricAuthManager != null) {
            triggerBiometricAuth()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding() // Handle keyboard insets
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()), // Enable scrolling
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppLogo(size = 72.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(32.dp))

            ElevatedGlassCard(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = viewModel::updateEmail,
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = BorderGlass
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = viewModel::updatePassword,
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff 
                                else Icons.Default.Visibility,
                                null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) 
                        VisualTransformation.None 
                    else 
                        PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = BorderGlass
                    )
                )

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = uiState.errorMessage!!,
                        color = AccentRed,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (uiState.lockoutUntil != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Too many attempts. Try again later.",
                        color = AccentRed,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.login(onLoginSuccess) },
                    enabled = !uiState.isLoading && uiState.lockoutUntil == null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = TextOnPrimary
                        )
                    } else {
                        Text("Login", modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                if (uiState.biometricAvailable && biometricAuthManager != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedButton(
                        onClick = { triggerBiometricAuth() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Fingerprint, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Use Biometric")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(onClick = onForgotPassword) {
                    Text("Forgot Password?", color = Primary)
                }
            }
        }
    }
}
