package com.sentinelguard.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sentinelguard.ui.theme.*
import com.sentinelguard.ui.viewmodels.PasswordRecoveryViewModel
import com.sentinelguard.ui.viewmodels.RecoveryStep

/**
 * Password Recovery Screen
 * 
 * Multi-step flow:
 * 1. Enter email
 * 2. Enter verification code
 * 3. Set new password
 * 4. Success
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordRecoveryScreen(
    onNavigateBack: () -> Unit,
    onRecoveryComplete: () -> Unit,
    viewModel: PasswordRecoveryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPassword by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Password Recovery") },
                navigationIcon = {
                    if (uiState.step != RecoveryStep.SUCCESS) {
                        IconButton(onClick = {
                            if (uiState.step == RecoveryStep.EMAIL_INPUT) {
                                onNavigateBack()
                            } else {
                                viewModel.goBack()
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceCard
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(BackgroundDark, SurfaceDark)
                    )
                )
                .padding(padding)
                .imePadding() // Handle keyboard insets
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress indicator
            RecoveryProgressIndicator(currentStep = uiState.step)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Error message
            AnimatedVisibility(visible = uiState.errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AccentRed.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, null, tint = AccentRed)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(uiState.errorMessage ?: "", color = AccentRed)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Success message
            AnimatedVisibility(visible = uiState.successMessage != null && uiState.step != RecoveryStep.SUCCESS) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = StatusSecure.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = StatusSecure)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(uiState.successMessage ?: "", color = StatusSecure)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Step content
            AnimatedContent(
                targetState = uiState.step,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() togetherWith 
                    slideOutHorizontally { -it } + fadeOut()
                },
                label = "stepTransition"
            ) { step ->
                when (step) {
                    RecoveryStep.EMAIL_INPUT -> EmailInputStep(
                        email = uiState.email,
                        onEmailChange = viewModel::updateEmail,
                        onSubmit = viewModel::sendRecoveryCode,
                        isLoading = uiState.isLoading
                    )
                    RecoveryStep.CODE_VERIFICATION -> CodeVerificationStep(
                        code = uiState.code,
                        onCodeChange = viewModel::updateCode,
                        onVerify = viewModel::verifyCode,
                        onResend = viewModel::resendCode,
                        isLoading = uiState.isLoading
                    )
                    RecoveryStep.NEW_PASSWORD -> NewPasswordStep(
                        newPassword = uiState.newPassword,
                        confirmPassword = uiState.confirmPassword,
                        showPassword = showPassword,
                        onNewPasswordChange = viewModel::updateNewPassword,
                        onConfirmPasswordChange = viewModel::updateConfirmPassword,
                        onTogglePasswordVisibility = { showPassword = !showPassword },
                        onSubmit = viewModel::resetPassword,
                        isLoading = uiState.isLoading
                    )
                    RecoveryStep.SUCCESS -> SuccessStep(
                        onContinue = onRecoveryComplete
                    )
                }
            }
        }
    }
}

@Composable
private fun RecoveryProgressIndicator(currentStep: RecoveryStep) {
    val steps = listOf("Email", "Verify", "Password", "Done")
    val currentIndex = currentStep.ordinal
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, label ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (index <= currentIndex) AccentCyan else SurfaceCard
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (index < currentIndex) {
                        Icon(
                            Icons.Default.Check, 
                            null, 
                            tint = BackgroundDark,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(
                            "${index + 1}",
                            color = if (index == currentIndex) BackgroundDark else TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (index <= currentIndex) AccentCyan else TextSecondary
                )
            }
        }
    }
}

@Composable
private fun EmailInputStep(
    email: String,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Email,
            null,
            modifier = Modifier.size(64.dp),
            tint = AccentCyan
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Enter your email address",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            "We'll send you a verification code to reset your password.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email Address") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onSubmit,
            enabled = email.isNotBlank() && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Send Recovery Code")
            }
        }
    }
}

@Composable
private fun CodeVerificationStep(
    code: String,
    onCodeChange: (String) -> Unit,
    onVerify: () -> Unit,
    onResend: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Lock,
            null,
            modifier = Modifier.size(64.dp),
            tint = AccentCyan
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Enter verification code",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            "Enter the 6-digit code sent to your email.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = code,
            onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) onCodeChange(it) },
            label = { Text("Verification Code") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onVerify,
            enabled = code.length == 6 && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Verify Code")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onResend, enabled = !isLoading) {
            Text("Resend Code")
        }
    }
}

@Composable
private fun NewPasswordStep(
    newPassword: String,
    confirmPassword: String,
    showPassword: Boolean,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSubmit: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Password,
            null,
            modifier = Modifier.size(64.dp),
            tint = AccentCyan
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Create new password",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            "Must be at least 8 characters.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            label = { Text("New Password") },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        "Toggle password visibility"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onSubmit,
            enabled = newPassword.length >= 8 && confirmPassword.isNotBlank() && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Reset Password")
            }
        }
    }
}

@Composable
private fun SuccessStep(
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.CheckCircle,
            null,
            modifier = Modifier.size(80.dp),
            tint = StatusSecure
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            "Password Reset!",
            style = MaterialTheme.typography.headlineMedium,
            color = StatusSecure,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            "Your password has been successfully reset. You can now log in with your new password.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Continue to Login")
        }
    }
}
