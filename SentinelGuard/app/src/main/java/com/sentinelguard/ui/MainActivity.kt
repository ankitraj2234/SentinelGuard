package com.sentinelguard.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.rememberNavController
import com.sentinelguard.auth.BiometricAuthManager
import com.sentinelguard.data.preferences.SecurePreferencesManager
import com.sentinelguard.ui.navigation.AppNavigation
import com.sentinelguard.ui.navigation.Screen
import com.sentinelguard.ui.theme.BackgroundPrimary
import com.sentinelguard.ui.theme.SentinelGuardTheme
import com.sentinelguard.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var biometricAuthManager: BiometricAuthManager
    
    @Inject
    lateinit var securePrefs: SecurePreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Create reactive theme state that updates when prefs change
            val themeMode by produceState(initialValue = securePrefs.themeMode) {
                // Re-check every 100ms while composed (lightweight polling for reactivity)
                while (true) {
                    val currentMode = securePrefs.themeMode
                    if (value != currentMode) {
                        value = currentMode
                    }
                    kotlinx.coroutines.delay(100)
                }
            }
            
            val systemDarkTheme = isSystemInDarkTheme()
            
            val useDarkTheme = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> systemDarkTheme // "system" follows system
            }
            
            SentinelGuardTheme(darkTheme = useDarkTheme) {
                MainContent(
                    biometricAuthManager = biometricAuthManager,
                    securePrefs = securePrefs
                )
            }
        }
    }
    
    override fun onPause() {
        super.onPause()
        // Lock the app when going to background (if session is active)
        if (securePrefs.isSessionActive) {
            securePrefs.isAppLocked = true
        }
    }
}

@Composable
private fun MainContent(
    biometricAuthManager: BiometricAuthManager,
    securePrefs: SecurePreferencesManager
) {
    val mainViewModel: MainViewModel = hiltViewModel()
    val navController = rememberNavController()
    val uiState by mainViewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Track if we need to show login due to app lock
    var needsAuth by remember { mutableStateOf(false) }
    
    // Observe lifecycle to detect app resume
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    // Check if app is locked and we have an active session
                    if (securePrefs.isSessionActive && securePrefs.isAppLocked) {
                        needsAuth = true
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    // App going to background, set locked
                    if (securePrefs.isSessionActive) {
                        securePrefs.isAppLocked = true
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    // Determine start destination based on state
    val startDestination = remember(uiState.isLoading, uiState.hasCompletedOnboarding, uiState.isSetupComplete, uiState.isLoggedIn, needsAuth) {
        when {
            uiState.isLoading -> Screen.Login.route // Default while loading
            !uiState.hasCompletedOnboarding -> Screen.Onboarding.route // Onboarding first!
            !uiState.isSetupComplete -> Screen.Setup.route
            !uiState.isLoggedIn -> Screen.Login.route
            needsAuth -> Screen.Login.route  // Force login if app was locked
            else -> Screen.Dashboard.route
        }
    }
    
    // Handle navigation when auth is needed
    LaunchedEffect(needsAuth) {
        if (needsAuth && uiState.isLoggedIn && !uiState.isLoading) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
    
    // Collect signals when app opens
    LaunchedEffect(Unit) {
        mainViewModel.onAppOpen()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundPrimary
    ) {
        // Only show navigation when not loading
        if (!uiState.isLoading) {
            AppNavigation(
                navController = navController,
                mainViewModel = mainViewModel,
                startDestination = startDestination,
                biometricAuthManager = biometricAuthManager,
                securePrefsManager = securePrefs,
                onAuthSuccess = {
                    // Clear the app lock when auth succeeds
                    securePrefs.isAppLocked = false
                    needsAuth = false
                }
            )
        }
    }
}
