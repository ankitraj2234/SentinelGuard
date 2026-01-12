package com.sentinelguard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sentinelguard.domain.model.RiskLevel
import com.sentinelguard.ui.components.*
import com.sentinelguard.ui.theme.*
import com.sentinelguard.ui.viewmodels.DashboardViewModel

@Composable
fun DashboardScreen(
    onNavigateToTimeline: () -> Unit,
    onNavigateToInsights: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAlertHistory: () -> Unit,
    onNavigateToScan: () -> Unit = {},
    onNavigateToNetwork: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Security",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Risk Indicator
            ElevatedGlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RiskScoreIndicator(
                        score = uiState.riskScore,
                        riskLevel = uiState.riskLevel
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Learning progress
                    if (!uiState.isLearningComplete) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.School,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Learning your patterns (${(uiState.learningProgress * 100).toInt()}%)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LinearProgressIndicator(
                            progress = { uiState.learningProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = AccentBlue,
                            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Status Cards Grid - Simplified
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusCard(
                    modifier = Modifier.weight(1f),
                    status = SecurityStatus.INFO,
                    title = "Network",
                    subtitle = uiState.networkDisplayText,
                    onClick = onNavigateToNetwork
                )
                StatusCard(
                    modifier = Modifier.weight(1f),
                    status = if (uiState.riskLevel == RiskLevel.NORMAL) SecurityStatus.SECURE else SecurityStatus.WARNING,
                    title = "Status",
                    subtitle = uiState.riskLevel.name
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Actions
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            QuickActionItem(
                icon = Icons.Default.Timeline,
                title = "Incident Timeline",
                subtitle = "View security events",
                onClick = onNavigateToTimeline
            )

            Spacer(modifier = Modifier.height(8.dp))

            QuickActionItem(
                icon = Icons.Default.Insights,
                title = "Behavioral Insights",
                subtitle = "View learned patterns",
                onClick = onNavigateToInsights
            )

            Spacer(modifier = Modifier.height(8.dp))

            QuickActionItem(
                icon = Icons.Default.Email,
                title = "Alert History",
                subtitle = "Sent email alerts",
                onClick = onNavigateToAlertHistory
            )

            Spacer(modifier = Modifier.height(8.dp))

            QuickActionItem(
                icon = Icons.Default.Shield,
                title = "Device Scan",
                subtitle = "Scan for malware & threats",
                onClick = onNavigateToScan
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        cornerRadius = 16.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
