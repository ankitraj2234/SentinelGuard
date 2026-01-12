package com.sentinelguard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sentinelguard.auth.BiometricAuthManager
import com.sentinelguard.data.preferences.SecurePreferencesManager
import com.sentinelguard.ui.screens.*
import com.sentinelguard.ui.viewmodels.MainViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel(),
    startDestination: String,
    biometricAuthManager: BiometricAuthManager? = null,
    securePrefsManager: SecurePreferencesManager? = null,
    onAuthSuccess: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    // Save onboarding completion flag
                    securePrefsManager?.hasCompletedOnboarding = true
                    navController.navigate(Screen.Setup.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Setup.route) {
            SetupScreen(
                onSetupComplete = {
                    // Go to permissions screen after account setup
                    navController.navigate(Screen.Permissions.route) {
                        popUpTo(Screen.Setup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    onAuthSuccess() // Clear app lock
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onForgotPassword = {
                    navController.navigate(Screen.PasswordRecovery.route)
                },
                biometricAuthManager = biometricAuthManager
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToTimeline = { navController.navigate(Screen.Timeline.route) },
                onNavigateToInsights = { navController.navigate(Screen.Insights.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToAlertHistory = { navController.navigate(Screen.AlertHistory.route) },
                onNavigateToScan = { navController.navigate(Screen.Scan.route) },
                onNavigateToNetwork = { navController.navigate(Screen.NetworkInfo.route) }
            )
        }

        composable(Screen.Insights.route) {
            InsightsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Timeline.route) {
            TimelineScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AlertHistory.route) {
            AlertHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToUsageStats = { navController.navigate(Screen.UsageStatistics.route) },
                onNavigateToForensicReport = { navController.navigate(Screen.ForensicReport.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Lock.route) {
            LockScreen(
                onUnlock = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Lock.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Recovery.route) {
            RecoveryScreen(
                onVerified = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Recovery.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PasswordRecovery.route) {
            PasswordRecoveryScreen(
                onNavigateBack = { navController.popBackStack() },
                onRecoveryComplete = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.PasswordRecovery.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Permissions.route) {
            PermissionScreen(
                onAllPermissionsGranted = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Permissions.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Scan.route) {
            ScanScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.NetworkInfo.route) {
            NetworkInfoScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCellTowerDetails = { navController.navigate(Screen.CellTowerDetails.route) }
            )
        }
        
        composable(Screen.CellTowerDetails.route) {
            CellTowerDetailsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.UsageStatistics.route) {
            UsageStatisticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ForensicReport.route) {
            ForensicReportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
