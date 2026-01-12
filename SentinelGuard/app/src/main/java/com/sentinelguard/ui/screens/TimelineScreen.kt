package com.sentinelguard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sentinelguard.incident.EventSeverity
import com.sentinelguard.incident.TimelineEvent
import com.sentinelguard.incident.TimelineEventType
import com.sentinelguard.ui.components.GlassCard
import com.sentinelguard.ui.theme.*
import com.sentinelguard.ui.viewmodels.TimelineViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    onNavigateBack: () -> Unit,
    viewModel: TimelineViewModel = hiltViewModel()
) {
    val events by viewModel.timelineEvents.collectAsState()

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
                title = { Text("Incident Timeline") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        @Suppress("DEPRECATION")
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )

            if (events.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusSecure,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No incidents recorded",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(events) { event ->
                        TimelineEventItem(event = event)
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineEventItem(event: TimelineEvent) {
    // Icon based on event type for more visual variety
    val icon = when (event.type) {
        TimelineEventType.SESSION_START -> Icons.Default.Login
        TimelineEventType.SESSION_END -> Icons.Default.Logout
        TimelineEventType.INCIDENT -> Icons.Default.Warning
        TimelineEventType.RISK_CHANGE -> Icons.Default.Security
        TimelineEventType.ACTION_TAKEN -> Icons.Default.CheckCircle
        TimelineEventType.SIGNAL -> when {
            event.title.contains("Biometric", ignoreCase = true) -> Icons.Default.Fingerprint
            event.title.contains("PIN", ignoreCase = true) -> Icons.Default.Pin
            event.title.contains("Login", ignoreCase = true) -> Icons.Default.Lock
            event.title.contains("Network", ignoreCase = true) || event.title.contains("WiFi", ignoreCase = true) -> Icons.Default.Wifi
            event.title.contains("Tower", ignoreCase = true) -> Icons.Default.CellTower
            event.title.contains("SIM", ignoreCase = true) -> Icons.Default.SimCard
            event.title.contains("Carrier", ignoreCase = true) -> Icons.Default.PhoneAndroid
            event.title.contains("Scan", ignoreCase = true) -> Icons.Default.Search
            event.title.contains("Threat", ignoreCase = true) || event.title.contains("Root", ignoreCase = true) -> Icons.Default.GppBad
            event.title.contains("Recording", ignoreCase = true) -> Icons.Default.Videocam
            event.title.contains("Location", ignoreCase = true) -> Icons.Default.LocationOn
            event.title.contains("Device", ignoreCase = true) || event.title.contains("Reboot", ignoreCase = true) -> Icons.Default.PhoneAndroid
            else -> Icons.Default.Circle
        }
    }
    
    // Color based on severity
    val color = when (event.severity) {
        EventSeverity.CRITICAL -> AccentRed
        EventSeverity.HIGH -> AccentRed.copy(alpha = 0.8f)
        EventSeverity.MEDIUM -> StatusWarning
        EventSeverity.LOW -> AccentBlue.copy(alpha = 0.7f)
        EventSeverity.INFO -> StatusSecure
    }

    val timeString = remember(event.timestamp) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(event.timestamp))
    }
    val riskScore = event.metadata["riskScore"]?.toIntOrNull()

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(color.copy(alpha = 0.3f))
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = timeString,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                if (riskScore != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Risk Score: ",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                        Text(
                            text = riskScore.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = color
                        )
                    }
                }
            }
        }
    }
}
