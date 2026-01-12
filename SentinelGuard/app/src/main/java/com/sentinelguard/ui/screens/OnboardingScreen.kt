package com.sentinelguard.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.sentinelguard.ui.theme.*
import kotlinx.coroutines.launch

/**
 * OnboardingScreen: First-launch tutorial explaining the app features.
 * 
 * Shows 4 swipeable pages with app information.
 * Saves completion flag to preferences.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()
    
    val pages = listOf(
        OnboardingPage(
            icon = Icons.Default.Shield,
            title = "Welcome to SentinelGuard",
            description = "Your personal security companion that monitors and protects your device from threats, unauthorized access, and suspicious behavior.",
            color = Primary
        ),
        OnboardingPage(
            icon = Icons.Default.CellTower,
            title = "Cell Tower Protection",
            description = "Detects fake cell towers (IMSI catchers) that criminals and authorities use to intercept your calls and messages. Get alerted when something suspicious is detected.",
            color = StatusWarning
        ),
        OnboardingPage(
            icon = Icons.Default.Psychology,
            title = "Behavioral Analysis",
            description = "Learns your normal usage patterns and alerts you to anomalies. If someone else uses your phone, you'll know immediately.",
            color = StatusSecure
        ),
        OnboardingPage(
            icon = Icons.Default.Security,
            title = "Ready to Protect",
            description = "All data stays on your device. No cloud, no tracking, complete privacy. Tap 'Get Started' to set up your account.",
            color = Accent
        )
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (pagerState.currentPage < 3) {
                    TextButton(onClick = onComplete) {
                        Text("Skip", color = TextSecondary)
                    }
                }
            }
            
            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                OnboardingPageContent(pages[page])
            }
            
            // Page indicators
            Row(
                modifier = Modifier
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == pagerState.currentPage) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == pagerState.currentPage) Primary
                                else TextMuted.copy(alpha = 0.3f)
                            )
                    )
                }
            }
            
            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                AnimatedVisibility(
                    visible = pagerState.currentPage > 0,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    ) {
                        Text("Back", color = TextSecondary)
                    }
                }
                
                if (pagerState.currentPage == 0) {
                    Spacer(modifier = Modifier.weight(1f))
                }
                
                // Next / Get Started button
                Button(
                    onClick = {
                        if (pagerState.currentPage < 3) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onComplete()
                        }
                    },
                    modifier = Modifier.height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text(
                        if (pagerState.currentPage < 3) "Next" else "Get Started",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val color: Color
)

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon with gradient background
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            page.color.copy(alpha = 0.3f),
                            page.color.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                page.icon,
                contentDescription = null,
                tint = page.color,
                modifier = Modifier.size(72.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Title
        Text(
            page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Description
        Text(
            page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}
