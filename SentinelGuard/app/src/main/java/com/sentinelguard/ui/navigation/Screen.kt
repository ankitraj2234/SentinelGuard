package com.sentinelguard.ui.navigation

/**
 * Navigation destinations for the app.
 */
sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Setup : Screen("setup")
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
    data object Insights : Screen("insights")
    data object Timeline : Screen("timeline")
    data object AlertHistory : Screen("alert_history")
    data object Settings : Screen("settings")
    data object Lock : Screen("lock")
    data object Recovery : Screen("recovery")
    data object PasswordRecovery : Screen("password_recovery")
    data object Permissions : Screen("permissions")
    data object Scan : Screen("scan")
    data object NetworkInfo : Screen("network_info")
    data object CellTowerDetails : Screen("cell_tower_details")
    data object UsageStatistics : Screen("usage_statistics")
    data object ForensicReport : Screen("forensic_report")
}
