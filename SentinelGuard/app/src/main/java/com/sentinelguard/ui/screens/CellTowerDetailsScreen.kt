package com.sentinelguard.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sentinelguard.network.TowerSecurityStatus
import com.sentinelguard.network.TowerType
import com.sentinelguard.security.TowerRiskLevel
import com.sentinelguard.ui.components.ElevatedGlassCard
import com.sentinelguard.ui.theme.*
import com.sentinelguard.ui.viewmodels.CellTowerDetailsViewModel
import com.sentinelguard.ui.viewmodels.EnhancedHistoryItem
import com.sentinelguard.security.VerificationResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CellTowerDetailsScreen(
    onNavigateBack: () -> Unit,
    viewModel: CellTowerDetailsViewModel = hiltViewModel()
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
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            TopAppBar(
                title = { 
                    Text(
                        "Cell Tower Details",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Analyzing cell tower...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else if (uiState.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ElevatedGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = StatusWarning,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                uiState.error!!,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.refresh() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Security Status Card (prominent at top)
                    SecurityStatusCard(
                        riskLevel = uiState.riskLevel,
                        statusTitle = uiState.statusTitle,
                        statusDescription = uiState.statusDescription,
                        recommendation = uiState.recommendation,
                        verification = uiState.verification
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Location Card - show even if no exact coordinates
                    SectionTitle("📍 Location")
                    LocationCard(
                        latitude = uiState.latitude,
                        longitude = uiState.longitude,
                        areaName = uiState.areaName,
                        accuracy = uiState.accuracy,
                        distance = uiState.distanceFromUser
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Coverage Analysis Card
                    SectionTitle("📶 Coverage Analysis")
                    CoverageCard(
                        signalStrength = uiState.signalStrength,
                        expectedSignal = uiState.expectedSignalStrength,
                        towerRange = uiState.towerRange,
                        sectorDirection = uiState.sectorDirection
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Infrastructure Card
                    SectionTitle("🏗️ Infrastructure")
                    InfrastructureCard(
                        cellId = uiState.cellId,
                        lac = uiState.lac,
                        towerTypeDescription = uiState.towerTypeDescription,
                        carrier = uiState.carrierName,
                        carrierFullName = uiState.carrierFullName,
                        technologies = uiState.technologies,
                        samples = uiState.samples
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Security Details Card
                    if (uiState.securityAnalysis != null && uiState.securityAnalysis!!.indicators.isNotEmpty()) {
                        SectionTitle("🛡️ Security Analysis")
                        SecurityDetailsCard(
                            riskScore = uiState.securityAnalysis!!.riskScore,
                            indicators = uiState.securityAnalysis!!.indicators.map { it.description }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // Connection History
                    if (uiState.connectionHistory.isNotEmpty()) {
                        var showClearDialog by remember { mutableStateOf(false) }
                        
                        // Clear History Confirmation Dialog
                        if (showClearDialog) {
                            AlertDialog(
                                onDismissRequest = { showClearDialog = false },
                                title = { Text("Clear History?") },
                                text = { 
                                    Text("Are you sure you want to clear all connection history? This action cannot be undone.")
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            viewModel.clearHistory()
                                            showClearDialog = false
                                        }
                                    ) {
                                        Text("Clear", color = StatusDanger)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showClearDialog = false }) {
                                        Text("Cancel")
                                    }
                                },
                                containerColor = SurfaceCard,
                                titleContentColor = TextPrimary,
                                textContentColor = TextSecondary
                            )
                        }
                        
                        // Section header with Clear button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📜 Recent Connections",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Primary
                            )
                            TextButton(
                                onClick = { showClearDialog = true },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Clear History",
                                    modifier = Modifier.size(16.dp),
                                    tint = TextSecondary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Clear",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        ConnectionHistoryCard(
                            history = uiState.connectionHistory.take(5)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = Primary,
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

@Composable
private fun SecurityStatusCard(
    riskLevel: TowerRiskLevel,
    statusTitle: String,
    statusDescription: String,
    recommendation: String?,
    verification: VerificationResult?
) {
    val (color, icon) = when (riskLevel) {
        TowerRiskLevel.LOW -> Pair(StatusSecure, Icons.Default.VerifiedUser)
        TowerRiskLevel.MEDIUM -> Pair(StatusWarning, Icons.Default.Warning)
        TowerRiskLevel.HIGH -> Pair(StatusDanger, Icons.Default.Error)
        TowerRiskLevel.CRITICAL -> Pair(StatusDanger, Icons.Default.GppBad)
    }
    
    ElevatedGlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                statusTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                statusDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            // Show verification items for LOW risk
            if (riskLevel == TowerRiskLevel.LOW && verification != null) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Verification checkmarks
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    VerificationItem(
                        text = "SIM Verified",
                        isVerified = verification.isSimVerified
                    )
                    VerificationItem(
                        text = "Carrier Match",
                        isVerified = verification.isCarrierMatch
                    )
                    VerificationItem(
                        text = "Connection Active",
                        isVerified = verification.isConnectionActive
                    )
                    VerificationItem(
                        text = "Secure Network (4G/5G)",
                        isVerified = verification.isNetworkTypeSecure
                    )
                    VerificationItem(
                        text = if (verification.isInDatabase) "In Database (${verification.databaseSamples} reports)" else "New Tower (Not in database yet)",
                        isVerified = verification.isInDatabase,
                        isInfo = !verification.isInDatabase
                    )
                }
            }
            
            if (!recommendation.isNullOrBlank() && riskLevel != TowerRiskLevel.LOW) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    recommendation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun VerificationItem(
    text: String,
    isVerified: Boolean,
    isInfo: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (isVerified) Icons.Default.CheckCircle 
            else if (isInfo) Icons.Default.Info 
            else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (isVerified) StatusSecure 
                   else if (isInfo) Primary.copy(alpha = 0.7f) 
                   else StatusDanger,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = if (isVerified) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LocationCard(
    latitude: Double?,
    longitude: Double?,
    areaName: String?,
    accuracy: Int?,
    distance: String?
) {
    ElevatedGlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            DetailRow(Icons.Default.LocationOn, "Area", areaName ?: "Location data unavailable")
            
            if (latitude != null && longitude != null) {
                DetailRow(Icons.Default.MyLocation, "Coordinates", "%.5f, %.5f".format(latitude, longitude))
            } else {
                DetailRow(Icons.Default.MyLocation, "Coordinates", "Fetching from database...")
            }
            
            accuracy?.let {
                DetailRow(Icons.Default.GpsFixed, "Accuracy", "$it meters")
            }
            
            distance?.let {
                DetailRow(Icons.Default.NearMe, "Distance from you", it)
            }
        }
    }
}

@Composable
private fun CoverageCard(
    signalStrength: Int?,
    expectedSignal: String?,
    towerRange: Int?,
    sectorDirection: String
) {
    ElevatedGlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            signalStrength?.let {
                DetailRow(
                    Icons.Default.SignalCellular4Bar, 
                    "Current Signal", 
                    "$it dBm ${getSignalQuality(it)}"
                )
            }
            expectedSignal?.let {
                DetailRow(Icons.Default.Analytics, "Expected Range", it)
            }
            towerRange?.let {
                DetailRow(Icons.Default.RadioButtonChecked, "Tower Range", "${it}m")
            }
            DetailRow(Icons.Default.Explore, "Sector Direction", sectorDirection)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Signal explanation
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = Primary.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "💡 Signal strength varies based on distance from tower, obstacles between you and the tower, and number of devices currently connected.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun InfrastructureCard(
    cellId: String?,
    lac: String?,
    towerTypeDescription: String,
    carrier: String?,
    carrierFullName: String?,
    technologies: List<String>,
    samples: Int
) {
    ElevatedGlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            cellId?.let {
                DetailRow(Icons.Default.CellTower, "Cell ID", it)
            }
            lac?.let {
                DetailRow(Icons.Default.LocationCity, "LAC/TAC", it)
            }
            DetailRow(Icons.Default.SettingsInputAntenna, "Tower Type", towerTypeDescription)
            
            // Show carrier with full name
            if (!carrier.isNullOrBlank()) {
                DetailRow(Icons.Default.Business, "Carrier", carrier)
                if (!carrierFullName.isNullOrBlank() && carrierFullName != carrier) {
                    Text(
                        text = carrierFullName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 32.dp, bottom = 8.dp)
                    )
                }
            }
            
            if (technologies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Supported Technologies",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    technologies.forEach { tech ->
                        TechBadge(tech)
                    }
                }
            }
            
            if (samples > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(Icons.Default.DataUsage, "Database Samples", "$samples measurements")
            }
        }
    }
}

@Composable
private fun TechBadge(tech: String) {
    val color = when (tech) {
        "5G" -> Primary
        "4G", "LTE" -> StatusSecure
        "3G" -> StatusWarning
        "2G" -> StatusDanger
        else -> TextSecondary
    }
    
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = tech,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun SecurityDetailsCard(
    riskScore: Int,
    indicators: List<String>
) {
    ElevatedGlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Risk Score",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "$riskScore / 100",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        riskScore >= 75 -> StatusDanger
                        riskScore >= 50 -> StatusWarning
                        else -> TextPrimary
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                "Detected Indicators:",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            indicators.forEach { indicator ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = StatusWarning,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        indicator,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
private fun ConnectionHistoryCard(
    history: List<EnhancedHistoryItem>
) {
    ElevatedGlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            history.forEach { entry ->
                val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                val dateFormat = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Status indicator
                    Box(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                when (entry.securityStatus) {
                                    "LOW" -> StatusSecure
                                    "MEDIUM" -> StatusWarning
                                    else -> StatusDanger
                                }
                            )
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        // Area name or Cell ID
                        Text(
                            entry.areaName ?: "Unknown Location",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        // Show coordinates if available
                        if (entry.latitude != null && entry.longitude != null) {
                            Text(
                                "📍 %.4f, %.4f".format(entry.latitude, entry.longitude),
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary.copy(alpha = 0.8f)
                            )
                        }
                        
                        // Carrier and network type
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                entry.carrierName ?: "Unknown",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            entry.networkType?.let { type ->
                                Text("•", color = MaterialTheme.colorScheme.outline)
                                TechBadge(type)
                            }
                        }
                        
                        // Cell ID
                        Text(
                            "Cell: ${entry.cellId}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            dateFormat.format(entry.connectedAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            timeFormat.format(entry.connectedAt),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                if (entry != history.last()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

private fun getSignalQuality(dbm: Int): String {
    return when {
        dbm > -70 -> "(Excellent)"
        dbm > -85 -> "(Good)"
        dbm > -100 -> "(Fair)"
        dbm > -110 -> "(Poor)"
        else -> "(Very Poor)"
    }
}
