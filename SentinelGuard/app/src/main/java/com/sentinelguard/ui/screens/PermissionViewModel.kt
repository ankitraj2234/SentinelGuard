package com.sentinelguard.ui.screens

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinelguard.permission.PermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PermissionUiState(
    val locationGranted: Boolean = false,
    val phoneStateGranted: Boolean = false,
    val notificationGranted: Boolean = false,
    val usageStatsGranted: Boolean = false,
    val cameraGranted: Boolean = false,
    val allGranted: Boolean = false
)

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val permissionManager: PermissionManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PermissionUiState())
    val uiState: StateFlow<PermissionUiState> = _uiState.asStateFlow()
    
    init {
        refreshPermissions()
    }
    
    fun refreshPermissions() {
        viewModelScope.launch {
            val status = permissionManager.checkPermissions()
            _uiState.update {
                PermissionUiState(
                    locationGranted = status.locationGranted,
                    phoneStateGranted = status.phoneStateGranted,
                    notificationGranted = status.notificationGranted,
                    usageStatsGranted = status.usageStatsGranted,
                    cameraGranted = status.cameraGranted,
                    allGranted = status.allGranted
                )
            }
        }
    }
    
    fun onPermissionsResult(results: Map<String, Boolean>) {
        // Refresh after permission dialog
        refreshPermissions()
    }
    
    fun getMissingRuntimePermissions(): List<String> {
        return permissionManager.getMissingRuntimePermissions()
    }
    
    fun getUsageStatsIntent(): Intent {
        return permissionManager.getUsageStatsSettingsIntent()
    }
}
