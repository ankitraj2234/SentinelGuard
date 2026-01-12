package com.sentinelguard.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sentinelguard.scanner.*
import com.sentinelguard.ui.components.GlassCard
import com.sentinelguard.ui.theme.*
import com.sentinelguard.ui.viewmodels.ScanViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onNavigateBack: () -> Unit,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
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
                title = { Text("Security Scan") },
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
            
            when {
                uiState.scanComplete && uiState.result != null -> {
                    ScanResultContent(
                        result = uiState.result!!,
                        onScanAgain = { viewModel.resetScan() }
                    )
                }
                uiState.isScanning -> {
                    ScanProgressContent(
                        uiState = uiState,
                        onCancel = { viewModel.cancelScan() }
                    )
                }
                else -> {
                    ScanStartContent(
                        lastScanTime = uiState.lastScanTime,
                        onFullScan = { viewModel.startFullScan() },
                        onQuickScan = { viewModel.startQuickScan() }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScanStartContent(
    lastScanTime: Long,
    onFullScan: () -> Unit,
    onQuickScan: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Shield icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Shield,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(64.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            "Device Scan",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (lastScanTime > 0) {
            val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
            Text(
                "Last scan: ${dateFormat.format(Date(lastScanTime))}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        } else {
            Text(
                "No previous scans",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Full scan button
        Button(
            onClick = onFullScan,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Icon(Icons.Default.Security, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Full Scan", style = MaterialTheme.typography.titleMedium)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Quick scan button
        OutlinedButton(
            onClick = onQuickScan,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Speed, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Quick Scan", style = MaterialTheme.typography.titleMedium)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = AccentCyan, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Full scan checks all apps and files. Quick scan only checks user-installed apps.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun ScanProgressContent(
    uiState: com.sentinelguard.ui.viewmodels.ScanUiState,
    onCancel: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated scanning icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Radar,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier
                    .size(64.dp)
                    .rotate(rotation)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            when (uiState.phase) {
                ScanPhase.INITIALIZING -> "Initializing..."
                ScanPhase.SCANNING_APPS -> "Scanning Apps"
                ScanPhase.SCANNING_FILES -> "Scanning Files"
                ScanPhase.ANALYZING -> "Analyzing..."
                ScanPhase.COMPLETE -> "Complete"
            },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            uiState.currentItem,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Progress bar
        LinearProgressIndicator(
            progress = { uiState.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Primary,
            trackColor = SurfaceGlass
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "${uiState.scannedCount} / ${uiState.totalCount}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Text(
                "${(uiState.progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Threats found
        if (uiState.threatsFound > 0) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = StatusDanger.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, null, tint = StatusDanger)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "${uiState.threatsFound} threat(s) found",
                        color = StatusDanger,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        TextButton(onClick = onCancel) {
            Text("Cancel Scan", color = TextSecondary)
        }
    }
}

@Composable
private fun ScanResultContent(
    result: ScanResult,
    onScanAgain: () -> Unit
) {
    val hasThreats = result.threats.isNotEmpty()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (hasThreats) StatusDanger.copy(alpha = 0.2f) 
                                     else StatusSecure.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        if (hasThreats) Icons.Default.Warning else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (hasThreats) StatusDanger else StatusSecure,
                        modifier = Modifier.size(64.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        if (hasThreats) "${result.threats.size} Threats Found" else "No Threats Found",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (hasThreats) StatusDanger else StatusSecure
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        "Scanned ${result.appsScanned} apps in ${result.scanDurationMs / 1000}s",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
        
        // Threat list
        if (hasThreats) {
            item {
                Text(
                    "Detected Threats",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            items(result.threats) { threat ->
                ThreatCard(threat = threat)
            }
        }
        
        // Scan again button
        item {
            Button(
                onClick = onScanAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Refresh, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scan Again")
            }
        }
    }
}

@Composable
private fun ThreatCard(threat: DetectedThreat) {
    val severityColor = when (threat.severity) {
        ThreatSeverity.CRITICAL -> StatusDanger
        ThreatSeverity.HIGH -> Color(0xFFFF6B35)
        ThreatSeverity.MEDIUM -> StatusWarning
        ThreatSeverity.LOW -> AccentCyan
    }
    
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Severity indicator
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(severityColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (threat.type) {
                        ThreatType.KNOWN_MALWARE -> Icons.Default.BugReport
                        ThreatType.SUSPICIOUS_PERMISSIONS -> Icons.Default.PrivacyTip
                        ThreatType.SUSPICIOUS_NAME -> Icons.Default.Warning
                        ThreatType.HIDDEN_APP -> Icons.Default.VisibilityOff
                        else -> Icons.Default.Shield
                    },
                    contentDescription = null,
                    tint = severityColor
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        threat.appName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = severityColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            threat.severity.name,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = severityColor
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    threat.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    threat.packageName,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
        }
    }
}
