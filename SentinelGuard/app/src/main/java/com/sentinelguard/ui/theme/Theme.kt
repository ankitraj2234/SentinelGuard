package com.sentinelguard.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * SentinelGuard Professional Theme
 * 
 * Features:
 * - Automatic system light/dark mode adaptation
 * - Professional emerald green security branding
 * - High contrast for accessibility
 * - Clean, modern look
 */

private val DarkColorScheme = darkColorScheme(
    // Primary (Emerald Green)
    primary = Primary,
    onPrimary = DarkTextOnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = PrimaryLight,
    
    // Secondary (Cool Gray)
    secondary = Secondary,
    onSecondary = DarkTextOnPrimary,
    secondaryContainer = DarkBackgroundTertiary,
    onSecondaryContainer = DarkTextPrimary,
    
    // Tertiary
    tertiary = AccentCyan,
    onTertiary = DarkTextOnPrimary,
    tertiaryContainer = Color(0xFF164E63),
    onTertiaryContainer = Color(0xFF67E8F9),
    
    // Background & Surface
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    
    // Container surfaces
    surfaceContainerLowest = Color(0xFF0D1117),
    surfaceContainerLow = DarkBackground,
    surfaceContainer = DarkSurface,
    surfaceContainerHigh = DarkBackgroundSecondary,
    surfaceContainerHighest = DarkBackgroundTertiary,
    
    // Inverse
    inverseSurface = LightSurface,
    inverseOnSurface = LightTextPrimary,
    inversePrimary = PrimaryDark,
    
    // Error & Status
    error = Error,
    onError = DarkTextOnPrimary,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFCA5A5),
    
    // Outline & Scrim
    outline = DarkBorder,
    outlineVariant = Color(0xFF1F2937),
    scrim = Color(0xFF000000)
)

private val LightColorScheme = lightColorScheme(
    // Primary (Emerald Green)
    primary = Primary,
    onPrimary = LightTextOnPrimary,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF064E3B),
    
    // Secondary (Cool Gray)
    secondary = SecondaryDark,
    onSecondary = LightTextOnPrimary,
    secondaryContainer = Color(0xFFE5E7EB),
    onSecondaryContainer = Color(0xFF1F2937),
    
    // Tertiary
    tertiary = Color(0xFF0891B2),
    onTertiary = LightTextOnPrimary,
    tertiaryContainer = Color(0xFFCFFAFE),
    onTertiaryContainer = Color(0xFF164E63),
    
    // Background & Surface
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    
    // Container surfaces (cream tones for cards/tiles)
    surfaceContainerLowest = Color(0xFFFFFFFF),   // Pure white
    surfaceContainerLow = Color(0xFFFFFBF5),      // Off-white cream
    surfaceContainer = Color(0xFFFFF8E7),         // Light cream
    surfaceContainerHigh = Color(0xFFFFF3E0),     // Warm cream for cards
    surfaceContainerHighest = Color(0xFFFFECB3),  // Darker cream/honey
    
    // Inverse
    inverseSurface = DarkSurface,
    inverseOnSurface = DarkTextPrimary,
    inversePrimary = PrimaryLight,
    
    // Error & Status
    error = Error,
    onError = LightTextOnPrimary,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
    
    // Outline & Scrim
    outline = LightBorder,
    outlineVariant = Color(0xFFE5E7EB),
    scrim = Color(0xFF000000)
)

/**
 * Main theme composable
 * 
 * @param darkTheme Whether to use dark theme. Defaults to system setting.
 * @param dynamicColor Whether to use dynamic colors (Android 12+). Disabled for branding consistency.
 */
@Composable
fun SentinelGuardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),  // Follow system theme
    dynamicColor: Boolean = false,                // Keep consistent branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            
            val controller = WindowCompat.getInsetsController(window, view)
            
            // Adapt status bar icons to theme
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
            
            // Set status bar color to transparent for edge-to-edge
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Get risk color based on score
 */
fun getRiskColor(score: Int): Color {
    return when {
        score <= 20 -> RiskSecure
        score <= 40 -> RiskLow
        score <= 60 -> RiskMedium
        score <= 80 -> RiskHigh
        else -> RiskCritical
    }
}

/**
 * Get status color
 */
fun getStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "secure", "success", "complete" -> StatusSecure
        "warning", "attention", "pending" -> StatusWarning
        "danger", "critical", "error", "failed" -> StatusDanger
        else -> StatusInfo
    }
}
