package com.sentinelguard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinelguard.domain.model.RiskLevel
import com.sentinelguard.data.preferences.SecurePreferencesManager
import com.sentinelguard.network.ConnectionType
import com.sentinelguard.network.NetworkInfoManager
import com.sentinelguard.security.baseline.BaselineEngine
import com.sentinelguard.security.risk.RiskScoringEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val riskScore: Int = 0,
    val riskLevel: RiskLevel = RiskLevel.NORMAL,
    val learningProgress: Float = 0f,
    val isLearningComplete: Boolean = false,
    val networkType: String = "Unknown",
    val networkDisplayText: String = "Loading..."  // e.g. "WiFi (ISP Name)" or "4G (Jio)"
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val riskScoringEngine: RiskScoringEngine,
    private val baselineEngine: BaselineEngine,
    private val securePrefs: SecurePreferencesManager,
    private val networkInfoManager: NetworkInfoManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            val riskScore = riskScoringEngine.getCurrentScore()
            val riskLevel = riskScoringEngine.getCurrentRiskLevel()
            val learningProgress = baselineEngine.getLearningProgress()
            val isLearningComplete = baselineEngine.isLearningComplete()
            
            // Get network info with ISP lookup (async)
            val networkInfo = networkInfoManager.getNetworkInfoWithIsp()
            
            val networkDisplayText = when (networkInfo.connectionType) {
                ConnectionType.WIFI -> {
                    // Prefer ISP name (first word only for compact display), fallback to SSID
                    val ispName = networkInfo.ispName?.let { fullName ->
                        // Extract first word for compact dashboard display
                        // e.g., "Reliance Jio Infocomm" -> "Jio", "Bharti Airtel" -> "Airtel"
                        getShortIspName(fullName)
                    }
                    val ssid = networkInfo.wifiInfo?.ssid?.takeIf { 
                        it != "Unknown" && it != "<unknown ssid>" && it != "Hidden Network" 
                    }
                    
                    when {
                        ispName != null -> "WiFi ($ispName)"
                        ssid != null -> "WiFi ($ssid)"
                        else -> "WiFi"
                    }
                }
                ConnectionType.MOBILE -> {
                    // Use carrier name, but fallback to ISP from API lookup if carrier is Unknown
                    val carrierFromTelephony = networkInfo.mobileInfo?.carrierName
                    val carrierName = if (carrierFromTelephony.isNullOrBlank() || carrierFromTelephony == "Unknown") {
                        networkInfo.ispName ?: "Unknown"  // Fallback to ISP from API
                    } else {
                        carrierFromTelephony
                    }
                    val shortCarrier = getShortIspName(carrierName)
                    val type = networkInfo.mobileInfo?.networkType ?: ""
                    if (type.isNotBlank()) "$type ($shortCarrier)" else "Mobile ($shortCarrier)"
                }
                ConnectionType.ETHERNET -> {
                    val ispName = networkInfo.ispName?.let { getShortIspName(it) }
                    if (ispName != null) "Ethernet ($ispName)" else "Ethernet"
                }
                ConnectionType.VPN -> "VPN"
                ConnectionType.NONE -> "No Connection"
            }
            
            val networkType = when (networkInfo.connectionType) {
                ConnectionType.WIFI -> "WiFi"
                ConnectionType.MOBILE -> networkInfo.mobileInfo?.networkType ?: "Mobile"
                ConnectionType.ETHERNET -> "Ethernet"
                ConnectionType.VPN -> "VPN"
                ConnectionType.NONE -> "Disconnected"
            }

            _uiState.update { it.copy(
                riskScore = riskScore,
                riskLevel = riskLevel,
                learningProgress = learningProgress,
                isLearningComplete = isLearningComplete,
                networkType = networkType,
                networkDisplayText = networkDisplayText
            )}
        }
    }

    fun refresh() {
        loadDashboardData()
    }
    
    /**
     * Extract short, recognizable ISP/carrier name for compact display
     * Examples:
     * - "Reliance Jio Infocomm Limited" -> "Jio"
     * - "Bharti Airtel Limited" -> "Airtel"
     * - "BSNL" -> "BSNL"
     * - "Vodafone Idea Limited" -> "Vi"
     * - "ACT Fibernet" -> "ACT"
     */
    private fun getShortIspName(fullName: String): String {
        val lowerName = fullName.lowercase()
        
        // Known ISP/carrier mappings for cleaner display
        return when {
            // Indian ISPs
            lowerName.contains("jio") -> "Jio"
            lowerName.contains("airtel") -> "Airtel"
            lowerName.contains("vodafone") || lowerName.contains("idea") -> "Vi"
            lowerName.contains("bsnl") -> "BSNL"
            lowerName.contains("mtnl") -> "MTNL"
            lowerName.contains("act fibernet") || lowerName.contains("act ") -> "ACT"
            lowerName.contains("hathway") -> "Hathway"
            lowerName.contains("tikona") -> "Tikona"
            lowerName.contains("excitel") -> "Excitel"
            lowerName.contains("spectra") -> "Spectra"
            lowerName.contains("tata") && lowerName.contains("sky") -> "Tata Sky"
            lowerName.contains("you broadband") -> "You"
            lowerName.contains("railwire") -> "Railwire"
            
            // Global ISPs
            lowerName.contains("comcast") -> "Xfinity"
            lowerName.contains("verizon") -> "Verizon"
            lowerName.contains("at&t") || lowerName.contains("att") -> "AT&T"
            lowerName.contains("t-mobile") -> "T-Mobile"
            lowerName.contains("sprint") -> "Sprint"
            lowerName.contains("spectrum") -> "Spectrum"
            lowerName.contains("google fiber") -> "Google"
            
            // Default: take first meaningful word
            else -> {
                // Remove common suffixes and get first word
                val cleanedName = fullName
                    .replace(Regex("\\b(Limited|Ltd|Inc|Corp|Corporation|Pvt|Private|Telecom|Communications|Broadband|Internet|Services|Network|Networks)\\b", RegexOption.IGNORE_CASE), "")
                    .trim()
                
                // Get first word that's at least 2 characters
                cleanedName.split(" ")
                    .firstOrNull { it.length >= 2 }
                    ?.replaceFirstChar { it.uppercase() }
                    ?: fullName.take(10)
            }
        }
    }
}

