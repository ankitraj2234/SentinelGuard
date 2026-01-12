package com.sentinelguard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinelguard.network.ConnectionType
import com.sentinelguard.network.NetworkInfo
import com.sentinelguard.network.NetworkInfoManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NetworkInfoUiState(
    val isLoading: Boolean = true,
    val networkInfo: NetworkInfo = NetworkInfo(),
    val lastUpdated: Long = 0L
)

@HiltViewModel
class NetworkInfoViewModel @Inject constructor(
    private val networkInfoManager: NetworkInfoManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(NetworkInfoUiState())
    val uiState: StateFlow<NetworkInfoUiState> = _uiState.asStateFlow()

    init {
        refreshNetworkInfo()
    }

    fun refreshNetworkInfo() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Small delay for visual feedback
            delay(300)
            
            // Use the ISP-enabled version for full network info
            val networkInfo = networkInfoManager.getNetworkInfoWithIsp()
            
            _uiState.update {
                it.copy(
                    isLoading = false,
                    networkInfo = networkInfo,
                    lastUpdated = System.currentTimeMillis()
                )
            }
        }
    }
    
    fun getConnectionTypeLabel(): String {
        return when (_uiState.value.networkInfo.connectionType) {
            ConnectionType.WIFI -> "WiFi"
            ConnectionType.MOBILE -> "Mobile Data"
            ConnectionType.ETHERNET -> "Ethernet"
            ConnectionType.VPN -> "VPN"
            ConnectionType.NONE -> "Not Connected"
        }
    }
}
