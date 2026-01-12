package com.sentinelguard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sentinelguard.ui.components.AppLogo
import com.sentinelguard.ui.components.ElevatedGlassCard
import com.sentinelguard.ui.components.GradientCard
import com.sentinelguard.ui.theme.*
import com.sentinelguard.ui.viewmodels.SetupViewModel

@Composable
fun SetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: SetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentStep by remember { mutableIntStateOf(0) }

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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Logo/Title
            AppLogo(size = 80.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SentinelGuard",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "On-Device Security",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Step indicators
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(3) { step ->
                    Box(
                        modifier = Modifier
                            .size(if (step == currentStep) 12.dp else 8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (step <= currentStep) Primary 
                                else TextTertiary.copy(alpha = 0.3f)
                            )
                    )
                    if (step < 2) Spacer(modifier = Modifier.width(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            when (currentStep) {
                0 -> AccountStep(
                    email = uiState.email,
                    password = uiState.password,
                    confirmPassword = uiState.confirmPassword,
                    onEmailChange = viewModel::updateEmail,
                    onPasswordChange = viewModel::updatePassword,
                    onConfirmPasswordChange = viewModel::updateConfirmPassword,
                    errorMessage = uiState.errorMessage,
                    onNext = {
                        if (viewModel.validateAccountStep()) {
                            currentStep = 1
                        }
                    }
                )
                1 -> EmailConfigStep(
                    smtpHost = uiState.smtpHost,
                    smtpPort = uiState.smtpPort,
                    smtpUsername = uiState.smtpUsername,
                    smtpPassword = uiState.smtpPassword,
                    onSmtpHostChange = viewModel::updateSmtpHost,
                    onSmtpPortChange = viewModel::updateSmtpPort,
                    onSmtpUsernameChange = viewModel::updateSmtpUsername,
                    onSmtpPasswordChange = viewModel::updateSmtpPassword,
                    onSkip = { currentStep = 2 },
                    onNext = { currentStep = 2 }
                )
                2 -> FinalStep(
                    isLoading = uiState.isLoading,
                    onComplete = {
                        viewModel.createAccount(onSetupComplete)
                    }
                )
            }
        }
    }
}

@Composable
private fun AccountStep(
    email: String,
    password: String,
    confirmPassword: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    errorMessage: String?,
    onNext: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    ElevatedGlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Create Your Account",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your credentials are stored locally and encrypted.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email Address") },
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
            value = password,
            onValueChange = onPasswordChange,
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

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Confirm Password") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = BorderGlass
            )
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage,
                color = AccentRed,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Continue", modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
private fun EmailConfigStep(
    smtpHost: String,
    smtpPort: String,
    smtpUsername: String,
    smtpPassword: String,
    onSmtpHostChange: (String) -> Unit,
    onSmtpPortChange: (String) -> Unit,
    onSmtpUsernameChange: (String) -> Unit,
    onSmtpPasswordChange: (String) -> Unit,
    onSkip: () -> Unit,
    onNext: () -> Unit
) {
    ElevatedGlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Email Alerts (Optional)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Configure SMTP to receive security alerts via email.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = smtpHost,
            onValueChange = onSmtpHostChange,
            label = { Text("SMTP Server") },
            placeholder = { Text("smtp.gmail.com") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = BorderGlass
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = smtpPort,
            onValueChange = onSmtpPortChange,
            label = { Text("SMTP Port") },
            placeholder = { Text("587") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = BorderGlass
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = smtpUsername,
            onValueChange = onSmtpUsernameChange,
            label = { Text("Email/Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = BorderGlass
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = smtpPassword,
            onValueChange = onSmtpPasswordChange,
            label = { Text("App Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = BorderGlass
            )
        )
        
        // App Password Help Link
        val context = androidx.compose.ui.platform.LocalContext.current
        val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { 
                    uriHandler.openUri("https://support.google.com/accounts/answer/185833?hl=en")
                }
                .padding(vertical = 8.dp)
        ) {
            Icon(
                Icons.Default.Help,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "How to create an App Password?",
                style = MaterialTheme.typography.bodyMedium,
                color = Primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = onSkip,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Skip")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Continue")
            }
        }
    }
}

@Composable
private fun FinalStep(
    isLoading: Boolean,
    onComplete: () -> Unit
) {
    GradientCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = TextOnPrimary,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ready to Protect",
                style = MaterialTheme.typography.headlineSmall,
                color = TextOnPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "SentinelGuard will learn your usage patterns over the next 7-14 days to build a behavioral baseline.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextOnPrimary.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onComplete,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Primary
                    )
                } else {
                    Text("Activate Protection", modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}
