package com.sentinelguard.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * SentinelGuard Professional Color Scheme
 * 
 * Design Philosophy:
 * - Charcoal/Slate backgrounds for professional look
 * - Emerald Green primary (trust, security, protection)
 * - Clean neutrals for readability
 * - High contrast for accessibility
 * 
 * Inspired by: Norton, Avast, Bitdefender, 1Password
 */

// ============ Primary Colors (Teal/Cyan - Modern Tech/Security) ============
val Primary = Color(0xFF0891B2)              // Teal cyan - main brand color
val PrimaryDark = Color(0xFF0E7490)          // Darker teal for pressed states
val PrimaryLight = Color(0xFF22D3EE)         // Lighter cyan for highlights
val PrimaryContainer = Color(0xFF164E63)     // Dark container background

// ============ Secondary Colors (Cool Gray) ============
val Secondary = Color(0xFF6B7280)            // Neutral gray
val SecondaryDark = Color(0xFF4B5563)
val SecondaryLight = Color(0xFF9CA3AF)

// ============ Dark Theme Background Colors ============
val DarkBackground = Color(0xFF111827)       // Rich dark slate
val DarkBackgroundSecondary = Color(0xFF1F2937)  // Slightly lighter
val DarkBackgroundTertiary = Color(0xFF374151)   // Cards and elevated surfaces
val DarkSurface = Color(0xFF1F2937)          // Card surfaces
val DarkSurfaceVariant = Color(0xFF374151)   // Elevated surfaces

// ============ Light Theme Background Colors ============
val LightBackground = Color(0xFFE0F7FA)      // Light sky blue (soft cyan tint)
val LightBackgroundSecondary = Color(0xFFE8F8F5) // Lighter mint
val LightBackgroundTertiary = Color(0xFFD1D5DB)   // Neutral gray
val LightSurface = Color(0xFFFFF8E1)         // Cream/warm white for cards
val LightSurfaceVariant = Color(0xFFFFF3E0) // Warmer cream for elevated surfaces

// ============ Dark Theme Text Colors ============
val DarkTextPrimary = Color(0xFFF9FAFB)      // Almost white
val DarkTextSecondary = Color(0xFF9CA3AF)    // Muted gray
val DarkTextTertiary = Color(0xFF6B7280)     // More muted
val DarkTextOnPrimary = Color(0xFFFFFFFF)    // White on green

// ============ Light Theme Text Colors ============
val LightTextPrimary = Color(0xFF111827)     // Dark slate
val LightTextSecondary = Color(0xFF4B5563)   // Medium gray
val LightTextTertiary = Color(0xFF6B7280)    // Lighter gray
val LightTextOnPrimary = Color(0xFFFFFFFF)   // White on green

// ============ Status Colors (Universal) ============
val StatusSecure = Color(0xFF10B981)         // Emerald green - secure/success
val StatusWarning = Color(0xFFF59E0B)        // Amber - warning
val StatusDanger = Color(0xFFEF4444)         // Red - critical/error
val StatusInfo = Color(0xFF3B82F6)           // Blue - informational

// ============ Semantic Colors ============
val Success = Color(0xFF10B981)
val Warning = Color(0xFFF59E0B)
val Error = Color(0xFFEF4444)
val Info = Color(0xFF3B82F6)

// ============ Risk Level Colors ============
val RiskSecure = Color(0xFF10B981)           // Green - 0-20
val RiskLow = Color(0xFF22C55E)              // Light green - 21-40
val RiskMedium = Color(0xFFF59E0B)           // Amber - 41-60
val RiskHigh = Color(0xFFF97316)             // Orange - 61-80
val RiskCritical = Color(0xFFEF4444)         // Red - 81-100

// ============ Border & Divider Colors ============
val DarkBorder = Color(0xFF374151)           // Subtle dark border
val DarkDivider = Color(0xFF1F2937)          // Dark divider
val LightBorder = Color(0xFFE5E7EB)          // Subtle light border
val LightDivider = Color(0xFFF3F4F6)         // Light divider

// ============ Glass Effect Colors ============
val GlassDark = Color(0x1AFFFFFF)            // 10% white overlay
val GlassLight = Color(0x1A000000)           // 10% black overlay
val GlassBorderDark = Color(0x33FFFFFF)      // 20% white border
val GlassBorderLight = Color(0x33000000)     // 20% black border

// ============ Button Colors ============
val ButtonPrimary = Primary
val ButtonPrimaryPressed = PrimaryDark
val ButtonSecondary = Color(0xFF374151)
val ButtonSecondaryPressed = Color(0xFF4B5563)
val ButtonDestructive = Error
val ButtonDestructivePressed = Color(0xFFDC2626)

// ============ Shield/Icon Colors ============
val ShieldSecure = Color(0xFF10B981)
val ShieldWarning = Color(0xFFF59E0B)
val ShieldDanger = Color(0xFFEF4444)
val ShieldNeutral = Color(0xFF6B7280)

// ============ Gradient Colors ============
val GradientPrimaryStart = Color(0xFF10B981)
val GradientPrimaryEnd = Color(0xFF059669)
val GradientDarkStart = Color(0xFF1F2937)
val GradientDarkEnd = Color(0xFF111827)

// ============ Legacy Compatibility (for existing code) ============
val BackgroundPrimary = DarkBackground
val BackgroundSecondary = DarkBackgroundSecondary
val BackgroundTertiary = DarkBackgroundTertiary
val SurfaceCard = DarkSurface
val SurfaceGlass = GlassDark
val TextPrimary = DarkTextPrimary
val TextSecondary = DarkTextSecondary
val TextTertiary = DarkTextTertiary
val TextOnPrimary = DarkTextOnPrimary
val AccentBlue = Info
val AccentCyan = Color(0xFF06B6D4)
val AccentPurple = Color(0xFF8B5CF6)
val AccentPink = Color(0xFFEC4899)
val AccentGreen = Success
val AccentOrange = Warning
val AccentRed = Error
val AccentYellow = Color(0xFFEAB308)
val BorderGlass = GlassBorderDark
val Divider = DarkDivider
val GradientStart = GradientPrimaryStart
val GradientEnd = GradientPrimaryEnd
val SurfaceDark = DarkBackgroundTertiary
val BackgroundDark = DarkBackground

// Additional aliases for Usage Statistics screen
val TextMuted = DarkTextTertiary
val SurfaceVariant = DarkSurfaceVariant
val DividerColor = DarkDivider
val Accent = AccentCyan
val StatusSuccess = Success
