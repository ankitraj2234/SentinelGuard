package com.sentinelguard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sentinelguard.ui.theme.*

/**
 * Glassmorphism card component.
 * 
 * Features:
 * - Theme-aware backgrounds
 * - Adaptive for light/dark mode
 * - Subtle border
 * - Rounded corners
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    alpha: Float = 0.08f,
    content: @Composable ColumnScope.() -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(surfaceColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

/**
 * Elevated glass card with more prominent effect.
 */
@Composable
fun ElevatedGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surfaceContainer
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(surfaceColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

/**
 * Gradient card for highlighted content.
 */
@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    startColor: Color = MaterialTheme.colorScheme.primary,
    endColor: Color = MaterialTheme.colorScheme.primaryContainer,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(startColor, endColor)
                )
            )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

/**
 * Status indicator card with colored accent.
 */
@Composable
fun StatusCard(
    modifier: Modifier = Modifier,
    status: SecurityStatus,
    title: String,
    subtitle: String? = null,
    cornerRadius: Dp = 16.dp,
    onClick: (() -> Unit)? = null
) {
    val statusColor = when (status) {
        SecurityStatus.SECURE -> StatusSecure
        SecurityStatus.WARNING -> StatusWarning
        SecurityStatus.DANGER -> StatusDanger
        SecurityStatus.INFO -> AccentBlue
    }

    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }
    
    val surfaceColor = MaterialTheme.colorScheme.surfaceContainerHigh

    Row(
        modifier = modifier
            .then(clickModifier)
            .clip(RoundedCornerShape(cornerRadius))
            .background(surfaceColor)
            .border(
                width = 1.dp,
                color = statusColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(16.dp)
    ) {
        // Status indicator dot
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(statusColor)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

enum class SecurityStatus {
    SECURE,
    WARNING,
    DANGER,
    INFO
}

