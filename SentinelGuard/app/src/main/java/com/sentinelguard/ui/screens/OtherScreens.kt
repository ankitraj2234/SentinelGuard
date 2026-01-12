package com.sentinelguard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sentinelguard.ui.components.ElevatedGlassCard
import com.sentinelguard.ui.theme.*
import com.sentinelguard.ui.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(onNavigateBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Behavioral Insights") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
            
            Column(modifier = Modifier.padding(16.dp)) {
                ElevatedGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Usage Patterns", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Your behavioral baseline is being built. Insights will appear after the learning period.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertHistoryScreen(onNavigateBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Alert History") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.MailOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No alerts sent",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToUsageStats: () -> Unit,
    onNavigateToForensicReport: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("Settings saved!")
            viewModel.clearSuccess()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Alert Settings
            SettingsSection(title = "Alert Settings", icon = Icons.Default.Notifications) {
                OutlinedTextField(
                    value = uiState.alertEmail,
                    onValueChange = viewModel::updateAlertEmail,
                    label = { Text("Alert Email") },
                    placeholder = { Text("Email for security alerts") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Cooldown Period", style = MaterialTheme.typography.bodyMedium)
                Slider(
                    value = uiState.cooldownMinutes.toFloat(),
                    onValueChange = { viewModel.updateCooldownMinutes(it.toInt()) },
                    valueRange = 5f..120f,
                    steps = 22
                )
                Text(
                    "${uiState.cooldownMinutes} minutes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Security Settings
            SettingsSection(title = "Security", icon = Icons.Default.Security) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Biometric Authentication")
                    Switch(
                        checked = uiState.biometricEnabled,
                        onCheckedChange = viewModel::updateBiometricEnabled
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Intruder Selfie")
                        Text(
                            "Capture photo on unauthorized access",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.intruderCaptureEnabled,
                        onCheckedChange = viewModel::updateIntruderCaptureEnabled
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Data Settings
            SettingsSection(title = "Data & Privacy", icon = Icons.Default.Storage) {
                Text("Data Retention", style = MaterialTheme.typography.bodyMedium)
                Slider(
                    value = uiState.dataRetentionDays.toFloat(),
                    onValueChange = { viewModel.updateDataRetentionDays(it.toInt()) },
                    valueRange = 30f..365f,
                    steps = 10
                )
                Text(
                    "${uiState.dataRetentionDays} days",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Learning Period", style = MaterialTheme.typography.bodyMedium)
                Slider(
                    value = uiState.learningPeriodDays.toFloat(),
                    onValueChange = { viewModel.updateLearningPeriodDays(it.toInt()) },
                    valueRange = 7f..30f,
                    steps = 22
                )
                Text(
                    "${uiState.learningPeriodDays} days",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // ============ APPEARANCE SETTINGS ============
            SettingsSection(title = "Appearance", icon = Icons.Default.Palette) {
                // Theme Toggle
                var themeExpanded by remember { mutableStateOf(false) }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Theme", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Choose your preferred appearance",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Box {
                        OutlinedButton(
                            onClick = { themeExpanded = true },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                when (uiState.themeMode) {
                                    "light" -> Icons.Default.LightMode
                                    "dark" -> Icons.Default.DarkMode
                                    else -> Icons.Default.BrightnessAuto
                                },
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                when (uiState.themeMode) {
                                    "light" -> "Light"
                                    "dark" -> "Dark"
                                    else -> "System"
                                }
                            )
                            Icon(Icons.Default.ArrowDropDown, null)
                        }
                        
                        DropdownMenu(
                            expanded = themeExpanded,
                            onDismissRequest = { themeExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.DarkMode, null, tint = Primary, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Dark", color = MaterialTheme.colorScheme.onBackground)
                                    }
                                },
                                onClick = { 
                                    viewModel.updateThemeMode("dark")
                                    themeExpanded = false 
                                }
                            )
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LightMode, null, tint = StatusWarning, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Light", color = MaterialTheme.colorScheme.onBackground)
                                    }
                                },
                                onClick = { 
                                    viewModel.updateThemeMode("light")
                                    themeExpanded = false 
                                }
                            )
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.BrightnessAuto, null, tint = Accent, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("System", color = MaterialTheme.colorScheme.onBackground)
                                    }
                                },
                                onClick = { 
                                    viewModel.updateThemeMode("system")
                                    themeExpanded = false 
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))
                
                // Auto-Lock Timeout
                var autoLockExpanded by remember { mutableStateOf(false) }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-Lock", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Lock app after inactivity",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Box {
                        OutlinedButton(
                            onClick = { autoLockExpanded = true },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = StatusSecure
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                when (uiState.autoLockMinutes) {
                                    0 -> "Disabled"
                                    1 -> "1 min"
                                    else -> "${uiState.autoLockMinutes} min"
                                }
                            )
                            Icon(Icons.Default.ArrowDropDown, null)
                        }
                        
                        DropdownMenu(
                            expanded = autoLockExpanded,
                            onDismissRequest = { autoLockExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            listOf(0, 1, 5, 15, 30).forEach { minutes ->
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            when (minutes) {
                                                0 -> "Disabled"
                                                1 -> "1 minute"
                                                else -> "$minutes minutes"
                                            },
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    },
                                    onClick = { 
                                        viewModel.updateAutoLockMinutes(minutes)
                                        autoLockExpanded = false 
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // ============ BACKUP & RESTORE ============
            SettingsSection(title = "Backup & Restore", icon = Icons.Default.CloudUpload) {
                Text(
                    "Backup your settings to a file. Restore after reinstall.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Backup Button
                    OutlinedButton(
                        onClick = { viewModel.backupSettings() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !uiState.isBackingUp
                    ) {
                        if (uiState.isBackingUp) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Upload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Backup")
                    }
                    
                    // Restore Button
                    OutlinedButton(
                        onClick = { viewModel.triggerRestore() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !uiState.isRestoring
                    ) {
                        if (uiState.isRestoring) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Restore")
                    }
                }
                
                // Last backup info
                uiState.lastBackupDate?.let { date ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusSecure,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Last backup: $date",
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusSecure
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Usage Statistics Navigation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .clickable { onNavigateToUsageStats() }
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BarChart, null, tint = Primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Usage Statistics", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "View detailed app usage analytics",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        Icons.Default.ChevronRight, 
                        contentDescription = "Navigate",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Forensic Report Export
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .clickable { onNavigateToForensicReport() }
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Description, null, tint = StatusWarning)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Export Forensic Report", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Generate PDF for investigation",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        Icons.Default.ChevronRight, 
                        contentDescription = "Navigate",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Privacy Info
            ElevatedGlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PrivacyTip, null, tint = StatusSecure)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Privacy", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "• All data stored locally\n• No cloud sync\n• No third-party SDKs\n• Encrypted database",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Save Button
            Button(
                onClick = viewModel::saveSettings,
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Save, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Settings")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Logout Button
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                @Suppress("DEPRECATION")
                Icon(Icons.Default.ExitToApp, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedGlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = AccentCyan)
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}

@Composable
fun LockScreen(onUnlock: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                tint = AccentRed,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "App Locked",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Suspicious activity detected",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onUnlock) {
                Text("Authenticate")
            }
        }
    }
}

@Composable
fun RecoveryScreen(onVerified: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Security,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Verify Your Identity",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onVerified) {
                Text("Verify")
            }
        }
    }
}
