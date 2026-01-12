package com.sentinelguard.ui.screens

import android.Manifest
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.sentinelguard.ui.theme.*

/**
 * PermissionScreen: Guides user through granting required permissions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionScreen(
    onAllPermissionsGranted: () -> Unit,
    viewModel: PermissionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Runtime permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        viewModel.onPermissionsResult(results)
    }
    
    // Check if all granted
    // Removed auto-navigation - user should click button to proceed
    
    // Refresh permissions when returning from settings (ON_RESUME)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Permissions Required") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
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
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "SentinelGuard needs permissions to monitor your device security",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Location Permission
            PermissionCard(
                title = "Location",
                description = "Track location for anomaly detection",
                icon = Icons.Default.LocationOn,
                isGranted = uiState.locationGranted,
                onRequest = {
                    permissionLauncher.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ))
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Phone State Permission
            PermissionCard(
                title = "Phone State",
                description = "Monitor SIM card changes",
                icon = Icons.Default.SimCard,
                isGranted = uiState.phoneStateGranted,
                onRequest = {
                    permissionLauncher.launch(arrayOf(Manifest.permission.READ_PHONE_STATE))
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Notification Permission
            PermissionCard(
                title = "Notifications",
                description = "Show security alerts",
                icon = Icons.Default.Notifications,
                isGranted = uiState.notificationGranted,
                onRequest = {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Usage Stats Permission (Special - needs Settings)
            PermissionCard(
                title = "App Usage Access",
                description = "Track which apps are opened/closed",
                icon = Icons.Default.Apps,
                isGranted = uiState.usageStatsGranted,
                isSpecialPermission = true,
                onRequest = {
                    context.startActivity(viewModel.getUsageStatsIntent())
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Camera Permission (for intruder selfie)
            PermissionCard(
                title = "Camera",
                description = "Capture intruder photo on unauthorized access",
                icon = Icons.Default.CameraAlt,
                isGranted = uiState.cameraGranted,
                onRequest = {
                    permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Continue button
            Button(
                onClick = {
                    if (uiState.allGranted) {
                        onAllPermissionsGranted()
                    } else {
                        // Request remaining permissions
                        val missing = viewModel.getMissingRuntimePermissions()
                        if (missing.isNotEmpty()) {
                            permissionLauncher.launch(missing.toTypedArray())
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.allGranted) StatusSecure else Primary
                )
            ) {
                if (uiState.allGranted) {
                    Icon(Icons.Default.Check, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("All Permissions Granted - Continue")
                } else {
                    Text("Grant Remaining Permissions")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Skip button (for users who don't want to grant all)
            TextButton(
                onClick = onAllPermissionsGranted,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Skip for now (limited functionality)", color = TextSecondary)
            }
        }
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    icon: ImageVector,
    isGranted: Boolean,
    isSpecialPermission: Boolean = false,
    onRequest: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        if (isGranted) StatusSecure.copy(alpha = 0.1f) else SurfaceCard,
        label = "bgColor"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isGranted) StatusSecure.copy(alpha = 0.2f) else Primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    null,
                    tint = if (isGranted) StatusSecure else Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                if (isSpecialPermission && !isGranted) {
                    Text(
                        "Opens Settings",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentCyan
                    )
                }
            }
            
            // Status / Action
            if (isGranted) {
                Icon(
                    Icons.Default.CheckCircle,
                    null,
                    tint = StatusSecure,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                FilledTonalButton(
                    onClick = onRequest,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (isSpecialPermission) "Open" else "Grant")
                }
            }
        }
    }
}
