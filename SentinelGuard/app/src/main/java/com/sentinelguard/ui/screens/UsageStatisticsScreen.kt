package com.sentinelguard.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.sentinelguard.ui.components.ElevatedGlassCard
import com.sentinelguard.ui.theme.*
import com.sentinelguard.ui.viewmodels.AppUsageInfo
import com.sentinelguard.ui.viewmodels.HourlyUsage
import com.sentinelguard.ui.viewmodels.UsageStatisticsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsageStatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: UsageStatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
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
                title = { Text("Usage Statistics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadUsageStatistics() }) {
                        Icon(Icons.Default.Refresh, "Refresh", tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
            
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading usage data...", color = TextSecondary)
                        }
                    }
                }
                
                !uiState.hasPermission -> {
                    PermissionRequiredCard(
                        onRequestPermission = {
                            val intent = android.content.Intent(
                                android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS
                            )
                            context.startActivity(intent)
                        }
                    )
                }
                
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = StatusDanger,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                uiState.error ?: "Unknown error",
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadUsageStatistics() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        // Header Stats
                        item {
                            HeaderStatsRow(
                                screenTime = uiState.formattedScreenTime,
                                appsUsed = uiState.appsUsedToday,
                                peakHour = uiState.peakHourFormatted
                            )
                        }
                        
                        // Today's Usage Timeline
                        item {
                            UsageTimelineCard(hourlyUsage = uiState.hourlyUsage)
                        }
                        
                        // Weekly Comparison
                        item {
                            WeeklyComparisonCard(
                                todayTime = uiState.formattedScreenTime,
                                averageTime = uiState.formattedWeeklyAverage,
                                percentChange = uiState.todayVsAveragePercent
                            )
                        }
                        
                        // Top Apps Section Title
                        item {
                            Text(
                                "📱 Most Used Apps",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        // Top Apps List
                        if (uiState.topApps.isEmpty()) {
                            item {
                                ElevatedGlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Default.Apps,
                                            contentDescription = null,
                                            tint = TextMuted,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "No app usage data yet",
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            item {
                                ElevatedGlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                    ) {
                                        uiState.topApps.forEachIndexed { index, app ->
                                            AppUsageRow(
                                                app = app,
                                                rank = index + 1,
                                                maxUsage = uiState.topApps.firstOrNull()?.usageTimeMs ?: 1L
                                            )
                                            if (index < uiState.topApps.lastIndex) {
                                                HorizontalDivider(
                                                    color = DividerColor,
                                                    modifier = Modifier.padding(vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Bottom spacing
                        item { Spacer(modifier = Modifier.height(32.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderStatsRow(
    screenTime: String,
    appsUsed: Int,
    peakHour: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            value = screenTime,
            label = "Screen Time",
            icon = Icons.Default.Timer,
            color = Primary
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = appsUsed.toString(),
            label = "Apps Used",
            icon = Icons.Default.Apps,
            color = Accent
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = peakHour,
            label = "Peak Hour",
            icon = Icons.Default.Schedule,
            color = StatusWarning
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: ImageVector,
    color: Color
) {
    ElevatedGlassCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun UsageTimelineCard(hourlyUsage: List<HourlyUsage>) {
    ElevatedGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    Icons.Default.Timeline,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Today's Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            
            // Timeline bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                val maxUsage = hourlyUsage.maxOfOrNull { it.usageMs } ?: 1L
                
                hourlyUsage.forEach { hour ->
                    val height = if (maxUsage > 0) {
                        (hour.usageMs.toFloat() / maxUsage * 50).coerceAtLeast(2f)
                    } else 2f
                    
                    val barModifier = Modifier
                        .weight(1f)
                        .height(height.dp)
                        .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                    
                    if (hour.usageMs > 0) {
                        Box(
                            modifier = barModifier.background(
                                Brush.verticalGradient(
                                    colors = listOf(Primary, Primary.copy(alpha = 0.6f))
                                )
                            )
                        )
                    } else {
                        Box(
                            modifier = barModifier.background(SurfaceVariant.copy(alpha = 0.3f))
                        )
                    }
                }
            }
            
            // Time labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("12am", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Text("6am", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Text("12pm", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Text("6pm", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Text("12am", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
        }
    }
}

@Composable
private fun WeeklyComparisonCard(
    todayTime: String,
    averageTime: String,
    percentChange: Int
) {
    ElevatedGlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.BarChart,
                        contentDescription = null,
                        tint = Accent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Weekly Comparison",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text("Today: ", color = TextSecondary)
                    Text(todayTime, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text(" | ", color = TextMuted)
                    Text("Avg: ", color = TextSecondary)
                    Text(averageTime, color = MaterialTheme.colorScheme.onBackground)
                }
            }
            
            // Percentage badge
            val isUp = percentChange >= 0
            val badgeColor = if (isUp) StatusWarning.copy(alpha = 0.2f) else StatusSuccess.copy(alpha = 0.2f)
            val textColor = if (isUp) StatusWarning else StatusSuccess
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeColor)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isUp) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${if (isUp) "+" else ""}$percentChange%",
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AppUsageRow(
    app: AppUsageInfo,
    rank: Int,
    maxUsage: Long
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank
        Text(
            "#$rank",
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted,
            modifier = Modifier.width(28.dp)
        )
        
        // App icon
        if (app.icon != null) {
            androidx.compose.foundation.Image(
                painter = rememberDrawablePainter(drawable = app.icon),
                contentDescription = app.appName,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Android,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // App name and progress bar
        Column(modifier = Modifier.weight(1f)) {
            Text(
                app.appName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            
            // Progress bar
            val progress = if (maxUsage > 0) app.usageTimeMs.toFloat() / maxUsage else 0f
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SurfaceVariant.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Primary, Accent)
                            )
                        )
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Duration
        Text(
            app.formattedDuration,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Primary
        )
    }
}

@Composable
private fun PermissionRequiredCard(onRequestPermission: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ElevatedGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = StatusWarning,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Permission Required",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Usage access permission is required to track app usage statistics. This data stays on your device.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onRequestPermission,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Grant Permission")
                }
            }
        }
    }
}
