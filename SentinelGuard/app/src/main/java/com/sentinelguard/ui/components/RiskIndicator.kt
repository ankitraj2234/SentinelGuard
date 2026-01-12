package com.sentinelguard.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sentinelguard.domain.model.RiskLevel
import com.sentinelguard.ui.theme.*

/**
 * Circular risk score indicator with animated progress.
 */
@Composable
fun RiskScoreIndicator(
    score: Int,
    riskLevel: RiskLevel,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    strokeWidth: Dp = 12.dp
) {
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "scoreAnimation"
    )

    val (backgroundColor, progressColor, glowColor) = when (riskLevel) {
        RiskLevel.NORMAL -> Triple(
            StatusSecure.copy(alpha = 0.1f),
            StatusSecure,
            StatusSecure.copy(alpha = 0.3f)
        )
        RiskLevel.WARNING -> Triple(
            StatusWarning.copy(alpha = 0.1f),
            StatusWarning,
            StatusWarning.copy(alpha = 0.3f)
        )
        RiskLevel.HIGH -> Triple(
            AccentRed.copy(alpha = 0.1f),
            AccentRed,
            AccentRed.copy(alpha = 0.3f)
        )
        RiskLevel.CRITICAL -> Triple(
            AccentRed.copy(alpha = 0.15f),
            AccentRed,
            AccentRed.copy(alpha = 0.5f)
        )
    }

    val statusText = when (riskLevel) {
        RiskLevel.NORMAL -> "Secure"
        RiskLevel.WARNING -> "Warning"
        RiskLevel.HIGH -> "High Risk"
        RiskLevel.CRITICAL -> "Critical"
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Background circle
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = strokeWidth.toPx()
            val diameter = size.toPx() - stroke
            
            // Background arc
            drawArc(
                color = backgroundColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(stroke / 2, stroke / 2),
                size = Size(diameter, diameter),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Progress arc
            val sweepAngle = (animatedScore / 100f).coerceIn(0f, 1f) * 360f
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(progressColor, glowColor, progressColor)
                ),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(stroke / 2, stroke / 2),
                size = Size(diameter, diameter),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        // Center content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${animatedScore.toInt()}",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.sp
                ),
                color = progressColor
            )
            Text(
                text = statusText,
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
        }
    }
}

/**
 * Small inline risk badge.
 */
@Composable
fun RiskBadge(
    riskLevel: RiskLevel,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (riskLevel) {
        RiskLevel.NORMAL -> Pair(StatusSecure, "SECURE")
        RiskLevel.WARNING -> Pair(StatusWarning, "WARNING")
        RiskLevel.HIGH -> Pair(AccentRed, "HIGH")
        RiskLevel.CRITICAL -> Pair(AccentRed, "CRITICAL")
    }

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = color
        )
    }
}

/**
 * Pulsing dot for active states.
 */
@Composable
fun PulsingDot(
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 12.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier.size(size * scale),
        contentAlignment = Alignment.Center
    ) {
        // Glow
        Box(
            modifier = Modifier
                .size(size * scale)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.3f * alpha))
        )
        // Core
        Box(
            modifier = Modifier
                .size(size * 0.6f)
                .clip(CircleShape)
                .background(color)
        )
    }
}
