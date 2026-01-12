package com.sentinelguard.ui.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.telephony.CellInfoLte
import android.telephony.CellInfoNr
import android.telephony.TelephonyManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinelguard.data.database.dao.CellTowerDao
import com.sentinelguard.data.database.entities.CellTowerHistoryEntity
import com.sentinelguard.network.CellTowerLocation
import com.sentinelguard.network.CellTowerLookupService
import com.sentinelguard.network.CellTowerRequest
import com.sentinelguard.network.TowerSecurityStatus
import com.sentinelguard.network.TowerType
import com.sentinelguard.security.CellTowerSecurityMonitor
import com.sentinelguard.security.TowerSecurityAnalysis
import com.sentinelguard.security.TowerRiskLevel
import com.sentinelguard.security.VerificationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

data class CellTowerDetailsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    
    // Basic cell info
    val cellId: String? = null,
    val lac: String? = null,
    val mcc: String? = null,
    val mnc: String? = null,
    val radioType: String = "LTE",
    val carrierName: String? = null,
    val carrierFullName: String? = null,
    
    // Location info
    val latitude: Double? = null,
    val longitude: Double? = null,
    val areaName: String? = null,
    val accuracy: Int? = null,
    val distanceFromUser: String? = null,
    
    // Coverage info
    val towerRange: Int? = null,
    val signalStrength: Int? = null,
    val expectedSignalStrength: String? = null,
    val sectorDirection: String = "Omnidirectional",
    
    // Infrastructure info
    val towerType: TowerType = TowerType.UNKNOWN,
    val towerTypeDescription: String = "Unknown",
    val technologies: List<String> = emptyList(),
    val samples: Int = 0,
    
    // Security info
    val securityStatus: TowerSecurityStatus = TowerSecurityStatus.UNKNOWN,
    val riskLevel: TowerRiskLevel = TowerRiskLevel.LOW,
    val securityAnalysis: TowerSecurityAnalysis? = null,
    val statusTitle: String = "Checking...",
    val statusDescription: String = "Analyzing tower connection",
    val recommendation: String = "",
    val verification: VerificationResult? = null,
    
    // History with enhanced location info
    val connectionHistory: List<EnhancedHistoryItem> = emptyList()
)

/**
 * Enhanced history item with location name and coordinates
 */
data class EnhancedHistoryItem(
    val cellId: String,
    val lac: String?,
    val areaName: String?,
    val latitude: Double?,
    val longitude: Double?,
    val carrierName: String?,
    val networkType: String?,
    val signalStrength: Int?,
    val connectedAt: Long,
    val securityStatus: String
)

@HiltViewModel
class CellTowerDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cellTowerLookupService: CellTowerLookupService,
    private val cellTowerSecurityMonitor: CellTowerSecurityMonitor,
    private val cellTowerDao: CellTowerDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(CellTowerDetailsUiState())
    val uiState: StateFlow<CellTowerDetailsUiState> = _uiState.asStateFlow()
    
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    init {
        loadCellTowerDetails()
    }

    @SuppressLint("MissingPermission")
    fun loadCellTowerDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Get current cell info
                val cellInfo = getCurrentCellInfo()
                
                if (cellInfo == null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = "Unable to get cell tower information. Make sure mobile data is enabled and location permission is granted."
                        ) 
                    }
                    return@launch
                }
                
                // Create lookup request
                val request = CellTowerRequest(
                    cellId = cellInfo.cellId,
                    lac = cellInfo.lac,
                    mcc = cellInfo.mcc,
                    mnc = cellInfo.mnc,
                    radioType = cellInfo.radioType
                )
                
                // Lookup tower location
                val towerLocation = cellTowerLookupService.lookupCellTower(request)
                
                // Get carrier name - use SIM operator as tower owner (logical since if using Jio SIM, tower is Jio's)
                val simOperatorName = telephonyManager.simOperatorName?.takeIf { it.isNotBlank() }
                val networkOperatorName = telephonyManager.networkOperatorName?.takeIf { it.isNotBlank() }
                val carrierName = networkOperatorName ?: simOperatorName ?: "Unknown Carrier"
                val carrierFullName = getFullCarrierName(simOperatorName ?: networkOperatorName ?: "")
                
                // Determine tower type based on radio type if API doesn't provide
                val towerType = towerLocation?.towerType ?: estimateTowerTypeFromRadio(cellInfo.radioType)
                val towerTypeDesc = getTowerTypeDescription(towerType, cellInfo.radioType)
                
                // Get technologies - build complete list based on radio type
                val technologies = getTechnologiesList(cellInfo.radioType)
                
                // Calculate distance if we have location
                val distanceStr = towerLocation?.let { tower ->
                    calculateEstimatedDistance(cellInfo.signalStrength, tower.range)
                } ?: calculateEstimatedDistanceFromSignal(cellInfo.signalStrength)
                
                // Get area name - use reverse geocoding if API didn't provide
                val areaName = towerLocation?.areaName 
                    ?: reverseGeocodeFromTower(towerLocation?.latitude, towerLocation?.longitude)
                
                // Analyze security
                val securityAnalysis = cellTowerSecurityMonitor.analyzeTower(
                    request = request,
                    towerLocation = towerLocation,
                    currentSignalStrength = cellInfo.signalStrength,
                    simCarrier = simOperatorName
                )
                
                // Get connection history with enhanced location info
                val history = cellTowerDao.getRecentHistory(20)
                val enhancedHistory = enhanceHistoryWithLocation(history)
                
                // Calculate expected signal strength
                val expectedSignal = towerLocation?.let { tower ->
                    calculateExpectedSignalRange(tower.range)
                } ?: getDefaultExpectedSignal(cellInfo.radioType)
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        cellId = cellInfo.cellId,
                        lac = cellInfo.lac,
                        mcc = cellInfo.mcc,
                        mnc = cellInfo.mnc,
                        radioType = cellInfo.radioType.uppercase(),
                        carrierName = carrierName,
                        carrierFullName = carrierFullName,
                        latitude = towerLocation?.latitude,
                        longitude = towerLocation?.longitude,
                        areaName = areaName,
                        accuracy = towerLocation?.accuracy,
                        distanceFromUser = distanceStr,
                        towerRange = towerLocation?.range,
                        signalStrength = cellInfo.signalStrength,
                        expectedSignalStrength = expectedSignal,
                        towerType = towerType,
                        towerTypeDescription = towerTypeDesc,
                        technologies = technologies,
                        samples = towerLocation?.samples ?: 0,
                        securityStatus = towerLocation?.securityStatus ?: TowerSecurityStatus.UNKNOWN,
                        riskLevel = securityAnalysis.riskLevel,
                        securityAnalysis = securityAnalysis,
                        statusTitle = securityAnalysis.statusTitle,
                        statusDescription = securityAnalysis.statusDescription,
                        recommendation = securityAnalysis.recommendation,
                        verification = securityAnalysis.verification,
                        connectionHistory = enhancedHistory
                    )
                }
                
                // Record this connection for history tracking
                cellTowerSecurityMonitor.onTowerConnected(
                    request = request,
                    towerLocation = towerLocation,
                    carrierName = carrierName,
                    networkType = cellInfo.radioType.uppercase(),
                    signalStrength = cellInfo.signalStrength
                )
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(isLoading = false, error = "Error loading cell tower details: ${e.message}") 
                }
            }
        }
    }
    
    /**
     * Clear all connection history
     */
    fun clearHistory() {
        viewModelScope.launch {
            try {
                cellTowerDao.clearAllHistory()
                // Refresh the history list
                _uiState.update { it.copy(connectionHistory = emptyList()) }
            } catch (e: Exception) {
                android.util.Log.e("CellTowerDetailsVM", "Error clearing history: ${e.message}")
            }
        }
    }
    
    /**
     * Get full carrier name from short name
     */
    private fun getFullCarrierName(shortName: String): String {
        val lowerName = shortName.lowercase()
        return when {
            lowerName.contains("jio") -> "Reliance Jio Infocomm Limited"
            lowerName.contains("airtel") -> "Bharti Airtel Limited"
            lowerName.contains("vi") || lowerName.contains("vodafone") || lowerName.contains("idea") -> "Vodafone Idea Limited"
            lowerName.contains("bsnl") -> "Bharat Sanchar Nigam Limited (BSNL)"
            lowerName.contains("mtnl") -> "Mahanagar Telephone Nigam Limited"
            lowerName.contains("att") || lowerName.contains("at&t") -> "AT&T Inc."
            lowerName.contains("verizon") -> "Verizon Communications Inc."
            lowerName.contains("t-mobile") || lowerName.contains("tmobile") -> "T-Mobile US, Inc."
            else -> shortName
        }
    }
    
    /**
     * Estimate tower type from radio technology
     */
    private fun estimateTowerTypeFromRadio(radioType: String): TowerType {
        return when (radioType.lowercase()) {
            "nr" -> TowerType.MACRO       // 5G towers are typically macro cells
            "lte" -> TowerType.MACRO      // 4G LTE is usually macro
            "wcdma", "umts" -> TowerType.MACRO
            "gsm" -> TowerType.MACRO
            else -> TowerType.MACRO       // Default to macro for outdoor coverage
        }
    }
    
    /**
     * Get human-readable tower type description
     */
    private fun getTowerTypeDescription(towerType: TowerType, radioType: String): String {
        val radioDesc = when (radioType.lowercase()) {
            "nr" -> "5G NR"
            "lte" -> "4G LTE"
            "wcdma", "umts" -> "3G UMTS"
            "gsm" -> "2G GSM"
            else -> radioType.uppercase()
        }
        
        return when (towerType) {
            TowerType.MACRO -> "Macro Cell ($radioDesc) - 1-30 km range"
            TowerType.MICRO -> "Micro Cell ($radioDesc) - 200m-2 km range"
            TowerType.PICO -> "Pico Cell ($radioDesc) - 100-200m range"
            TowerType.FEMTO -> "Femto Cell (Indoor) - 10-50m range"
            TowerType.UNKNOWN -> "Macro Cell ($radioDesc)"  // Default display
        }
    }
    
    /**
     * Get complete technologies list based on radio type
     */
    private fun getTechnologiesList(radioType: String): List<String> {
        return when (radioType.lowercase()) {
            "nr" -> listOf("5G NR", "4G LTE", "3G UMTS", "2G GSM")
            "lte" -> listOf("4G LTE", "3G UMTS", "2G GSM")
            "wcdma", "umts" -> listOf("3G UMTS", "2G GSM")
            "gsm" -> listOf("2G GSM")
            else -> listOf(radioType.uppercase())
        }
    }
    
    /**
     * Estimate distance from signal strength when no tower range available
     */
    private fun calculateEstimatedDistanceFromSignal(signalStrength: Int?): String {
        if (signalStrength == null) return "~1-2 km"
        
        return when {
            signalStrength > -70 -> "< 500m (Very Close)"
            signalStrength > -85 -> "500m - 1 km"
            signalStrength > -100 -> "1 - 3 km"
            signalStrength > -110 -> "3 - 5 km"
            else -> "> 5 km"
        }
    }
    
    private fun calculateEstimatedDistance(signalStrength: Int?, towerRange: Int): String {
        val estimatedMeters = when {
            signalStrength == null -> towerRange / 2
            signalStrength > -70 -> (towerRange * 0.1).toInt()
            signalStrength > -85 -> (towerRange * 0.3).toInt()
            signalStrength > -100 -> (towerRange * 0.6).toInt()
            else -> (towerRange * 0.9).toInt()
        }
        
        return cellTowerLookupService.formatDistance(estimatedMeters.toDouble())
    }
    
    private fun calculateExpectedSignalRange(towerRange: Int): String {
        return when {
            towerRange <= 500 -> "-50 to -70 dBm (Excellent)"
            towerRange <= 2000 -> "-70 to -90 dBm (Good)"
            towerRange <= 10000 -> "-90 to -110 dBm (Fair)"
            else -> "-100 to -120 dBm (Weak)"
        }
    }
    
    private fun getDefaultExpectedSignal(radioType: String): String {
        return when (radioType.lowercase()) {
            "nr" -> "-80 to -100 dBm (Typical 5G)"
            "lte" -> "-70 to -100 dBm (Typical 4G)"
            else -> "-75 to -105 dBm (Typical)"
        }
    }
    
    /**
     * Reverse geocode coordinates to area name
     */
    @Suppress("DEPRECATION")
    private suspend fun reverseGeocodeFromTower(latitude: Double?, longitude: Double?): String? {
        if (latitude == null || longitude == null) return null
        
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    var result: String? = null
                    geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            result = buildString {
                                address.subLocality?.let { append(it).append(", ") }
                                address.locality?.let { append(it) }
                                if (isEmpty()) address.adminArea?.let { append(it) }
                            }.takeIf { it.isNotBlank() }
                        }
                    }
                    Thread.sleep(500)
                    result
                } else {
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        buildString {
                            address.subLocality?.let { append(it).append(", ") }
                            address.locality?.let { append(it) }
                            if (isEmpty()) address.adminArea?.let { append(it) }
                        }.takeIf { it.isNotBlank() }
                    } else null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
    
    /**
     * Enhance history items with location information
     */
    private suspend fun enhanceHistoryWithLocation(
        history: List<CellTowerHistoryEntity>
    ): List<EnhancedHistoryItem> = withContext(Dispatchers.IO) {
        history.map { entry ->
            // Try to get area name if we have coordinates
            val areaName = entry.areaName ?: entry.latitude?.let { lat ->
                entry.longitude?.let { lon ->
                    reverseGeocodeFromTower(lat, lon)
                }
            }
            
            EnhancedHistoryItem(
                cellId = entry.cellId,
                lac = entry.lac,
                areaName = areaName,
                latitude = entry.latitude,
                longitude = entry.longitude,
                carrierName = entry.carrierName,
                networkType = entry.networkType,
                signalStrength = entry.signalStrength,
                connectedAt = entry.connectedAt,
                securityStatus = entry.securityStatus
            )
        }
    }
    
    @SuppressLint("MissingPermission")
    private fun getCurrentCellInfo(): CurrentCellInfo? {
        return try {
            val cellInfoList = telephonyManager.allCellInfo ?: return null
            
            for (cellInfo in cellInfoList) {
                if (!cellInfo.isRegistered) continue
                
                when (cellInfo) {
                    is CellInfoLte -> {
                        val identity = cellInfo.cellIdentity
                        val strength = cellInfo.cellSignalStrength
                        return CurrentCellInfo(
                            cellId = identity.ci.takeIf { it != Int.MAX_VALUE }?.toString() ?: continue,
                            lac = identity.tac.takeIf { it != Int.MAX_VALUE }?.toString() ?: "0",
                            mcc = identity.mccString ?: "0",
                            mnc = identity.mncString ?: "0",
                            radioType = "lte",
                            signalStrength = strength.dbm
                        )
                    }
                    is CellInfoNr -> {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            val identity = cellInfo.cellIdentity as? android.telephony.CellIdentityNr ?: continue
                            val strength = cellInfo.cellSignalStrength as? android.telephony.CellSignalStrengthNr
                            return CurrentCellInfo(
                                cellId = identity.nci.takeIf { it != Long.MAX_VALUE }?.toString() ?: continue,
                                lac = identity.tac.takeIf { it != Int.MAX_VALUE }?.toString() ?: "0",
                                mcc = identity.mccString ?: "0",
                                mnc = identity.mncString ?: "0",
                                radioType = "nr",
                                signalStrength = strength?.dbm
                            )
                        }
                    }
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }
    
    private data class CurrentCellInfo(
        val cellId: String,
        val lac: String,
        val mcc: String,
        val mnc: String,
        val radioType: String,
        val signalStrength: Int?
    )
    
    fun refresh() {
        loadCellTowerDetails()
    }
}
