package com.sentinelguard.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.sentinelguard.ui.components.ElevatedGlassCard
import com.sentinelguard.ui.theme.*
import com.sentinelguard.ui.viewmodels.ExportMethod
import com.sentinelguard.ui.viewmodels.ForensicReportViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForensicReportScreen(
    onNavigateBack: () -> Unit,
    viewModel: ForensicReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Dialog state
    var showExportDialog by remember { mutableStateOf(false) }
    var fileName by remember { 
        mutableStateOf("SentinelGuard_Report_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}")
    }
    var watermarkOpacity by remember { mutableStateOf(8f) } // 8% default
    var exportMethod by remember { mutableStateOf(ExportMethod.SAVE_TO_DEVICE) }
    var recipientEmail by remember { mutableStateOf("") }
    
    // Pre-populate recipient email from user's account email
    LaunchedEffect(uiState.userEmail) {
        if (recipientEmail.isEmpty() && uiState.userEmail.isNotEmpty()) {
            recipientEmail = uiState.userEmail
        }
    }
    
    // Track pending file for save
    var pendingFileToSave by remember { mutableStateOf<File?>(null) }
    
    // Document picker launcher - lets user choose where to save
    val saveDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        uri?.let { destinationUri ->
            pendingFileToSave?.let { sourceFile ->
                try {
                    // Copy generated PDF to user's chosen location
                    context.contentResolver.openOutputStream(destinationUri)?.use { outputStream ->
                        sourceFile.inputStream().use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    // Clean up temp file
                    sourceFile.delete()
                    pendingFileToSave = null
                    
                    // Show success toast
                    android.widget.Toast.makeText(
                        context,
                        "Report saved successfully!",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    
                    // Navigate back to main screen after successful save
                    onNavigateBack()
                    
                } catch (e: Exception) {
                    android.widget.Toast.makeText(
                        context,
                        "Failed to save: ${e.message}",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        viewModel.clearGeneratedFile()
    }
    
    // Handle file ready - prompt user where to save
    LaunchedEffect(uiState.generatedFile) {
        uiState.generatedFile?.let { file ->
            pendingFileToSave = file
            // Launch document picker with suggested filename
            saveDocumentLauncher.launch("$fileName.pdf")
        }
    }
    
    // Handle email sent success
    LaunchedEffect(uiState.emailSent) {
        if (uiState.emailSent) {
            android.widget.Toast.makeText(
                context,
                "Report sent to email successfully!",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            viewModel.clearEmailSent()
            onNavigateBack()
        }
    }
    
    // Export Options Dialog
    if (showExportDialog) {
        ExportOptionsDialog(
            fileName = fileName,
            onFileNameChange = { fileName = it },
            watermarkOpacity = watermarkOpacity,
            onOpacityChange = { watermarkOpacity = it },
            exportMethod = exportMethod,
            onExportMethodChange = { exportMethod = it },
            recipientEmail = recipientEmail,
            onRecipientEmailChange = { recipientEmail = it },
            emailConfigured = uiState.emailConfigured,
            onDismiss = { showExportDialog = false },
            onConfirm = {
                showExportDialog = false
                when (exportMethod) {
                    ExportMethod.SAVE_TO_DEVICE -> {
                        viewModel.generateReport(fileName, watermarkOpacity / 100f)
                    }
                    ExportMethod.SEND_EMAIL -> {
                        viewModel.generateAndEmailReport(
                            fileName, 
                            watermarkOpacity / 100f, 
                            recipientEmail
                        )
                    }
                }
            }
        )
    }
    
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
                title = { Text("Forensic Report") },
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
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Card
                ElevatedGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Security Forensic Report",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Export comprehensive security data for investigation by cyber security experts.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                // Report Contents Preview
                Text(
                    "📋 Report Contains",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Primary
                )
                
                ElevatedGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        ReportSection(Icons.Default.PhoneAndroid, "Device Information", "Model, Android version, security patch")
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                        ReportSection(Icons.Default.CellTower, "Network History", "All cell tower connections with locations")
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                        ReportSection(Icons.Default.Warning, "Security Incidents", "All alerts, threat levels, timestamps")
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                        ReportSection(Icons.Default.Psychology, "Behavioral Baseline", "Learned patterns and settings")
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                        ReportSection(Icons.Default.Apps, "App Usage Analytics", "Top apps and usage patterns")
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                        ReportSection(Icons.Default.Security, "Security Event Logs", "All logged security events")
                    }
                }
                
                // Professional Features
                Text(
                    "✨ Professional Features",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Primary
                )
                
                ElevatedGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        FeatureItem("🔐 Watermark", "Customizable opacity on every page")
                        FeatureItem("📱 Device Footer", "Device name and email on each page")
                        FeatureItem("📊 Formatted Tables", "Professional data presentation")
                        FeatureItem("🎨 Branded Design", "Consistent security theme")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Error message
                uiState.error?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = StatusDanger.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ErrorOutline, null, tint = StatusDanger)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(error, color = StatusDanger)
                        }
                    }
                }
                
                // Export Button
                Button(
                    onClick = { showExportDialog = true },
                    enabled = !uiState.isGenerating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    if (uiState.isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = TextOnPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Generating Report...")
                    } else {
                        Icon(Icons.Default.Save, null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Generate & Save Report", fontWeight = FontWeight.Bold)
                    }
                }
                
                // Info text
                Text(
                    "You will be asked to choose where to save the PDF report.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExportOptionsDialog(
    fileName: String,
    onFileNameChange: (String) -> Unit,
    watermarkOpacity: Float,
    onOpacityChange: (Float) -> Unit,
    exportMethod: ExportMethod,
    onExportMethodChange: (ExportMethod) -> Unit,
    recipientEmail: String,
    onRecipientEmailChange: (String) -> Unit,
    emailConfigured: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val isEmailValid = recipientEmail.contains("@") && recipientEmail.contains(".")
    val canConfirm = when (exportMethod) {
        ExportMethod.SAVE_TO_DEVICE -> true
        ExportMethod.SEND_EMAIL -> emailConfigured && isEmailValid
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onBackground,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Export Options", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Export Method Selection
                Column {
                    Text(
                        "Export Method",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Save to Device Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onExportMethodChange(ExportMethod.SAVE_TO_DEVICE) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = exportMethod == ExportMethod.SAVE_TO_DEVICE,
                            onClick = { onExportMethodChange(ExportMethod.SAVE_TO_DEVICE) },
                            colors = RadioButtonDefaults.colors(selectedColor = Primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Save,
                            contentDescription = null,
                            tint = if (exportMethod == ExportMethod.SAVE_TO_DEVICE) Primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Save to Device",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    
                    // Send to Email Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                if (emailConfigured) onExportMethodChange(ExportMethod.SEND_EMAIL) 
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = exportMethod == ExportMethod.SEND_EMAIL,
                            onClick = { if (emailConfigured) onExportMethodChange(ExportMethod.SEND_EMAIL) },
                            enabled = emailConfigured,
                            colors = RadioButtonDefaults.colors(selectedColor = Primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            tint = if (exportMethod == ExportMethod.SEND_EMAIL) Primary 
                                   else if (!emailConfigured) MaterialTheme.colorScheme.outline
                                   else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "Send to Email",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (emailConfigured) MaterialTheme.colorScheme.onBackground 
                                       else MaterialTheme.colorScheme.outline
                            )
                            if (!emailConfigured) {
                                Text(
                                    "Configure email in Settings first",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                    
                    // Email recipient input (only shown when email selected)
                    if (exportMethod == ExportMethod.SEND_EMAIL) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = recipientEmail,
                            onValueChange = onRecipientEmailChange,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            label = { Text("Recipient Email") },
                            placeholder = { Text("your@email.com") },
                            leadingIcon = {
                                Icon(Icons.Default.AlternateEmail, null, tint = Primary)
                            },
                            isError = recipientEmail.isNotEmpty() && !isEmailValid,
                            supportingText = if (recipientEmail.isNotEmpty() && !isEmailValid) {
                                { Text("Enter a valid email address") }
                            } else null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                }
                
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                
                // File Name Input
                Column {
                    Text(
                        "PDF File Name",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { 
                            // Remove invalid characters
                            onFileNameChange(it.replace(Regex("[^a-zA-Z0-9_\\-]"), "_"))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Enter file name") },
                        leadingIcon = {
                            Icon(Icons.Default.Description, null, tint = Primary)
                        },
                        trailingIcon = {
                            Text(
                                ".pdf",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
                
                // Watermark Opacity Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Watermark Opacity",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${watermarkOpacity.toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = watermarkOpacity,
                        onValueChange = onOpacityChange,
                        valueRange = 0f..30f,
                        steps = 29,
                        colors = SliderDefaults.colors(
                            thumbColor = Primary,
                            activeTrackColor = Primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                    Text(
                        "Adjust the visibility of the SENTINELGUARD watermark. 0% = invisible, 30% = very visible.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = canConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Icon(
                    if (exportMethod == ExportMethod.SEND_EMAIL) Icons.Default.Send else Icons.Default.Save, 
                    null, 
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (exportMethod == ExportMethod.SEND_EMAIL) "Send" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
private fun ReportSection(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Icon(
            Icons.Default.Check,
            contentDescription = null,
            tint = StatusSecure,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun FeatureItem(emoji: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
