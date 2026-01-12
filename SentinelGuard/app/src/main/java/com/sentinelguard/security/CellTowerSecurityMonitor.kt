package com.sentinelguard.security

import android.content.Context
import com.sentinelguard.alert.EmailAlertService
import com.sentinelguard.data.database.dao.CellTowerDao
import com.sentinelguard.data.database.entities.CellTowerHistoryEntity
import com.sentinelguard.data.database.entities.CellTowerIncidentEntity
import com.sentinelguard.network.CellTowerLocation
import com.sentinelguard.network.CellTowerLookupService
import com.sentinelguard.network.CellTowerRequest
import com.sentinelguard.network.TowerSecurityStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Security verification results - what we CAN verify
 */
data class VerificationResult(
    val isSimVerified: Boolean,
    val isCarrierMatch: Boolean,
    val isConnectionActive: Boolean,
    val isNetworkTypeSecure: Boolean,  // 4G/5G is more secure than 2G
    val isInDatabase: Boolean,
    val databaseSamples: Int
)

/**
 * Security indicator types for REAL threats only
 * "Not in database" is NOT a threat by itself
 */
enum class TowerSecurityIndicator(val weight: Int, val description: String, val isCritical: Boolean) {
    // REAL threats (high weight)
    DOWNGRADE_2G(50, "Network forced to insecure 2G protocol - possible interception", true),
    SIGNAL_SPIKE(35, "Abnormally strong signal detected - possible fake tower", true),
    CARRIER_MISMATCH(40, "Tower carrier doesn't match your SIM operator", true),
    RAPID_TOWER_CHANGE(25, "Unusual rapid tower switching detected", false),
    
    // Informational only (low/zero weight) - NOT security threats
    NOT_IN_DATABASE(0, "Tower not yet in crowdsourced database (possibly new)", false),
    LOW_SAMPLE_COUNT(0, "Tower has few reports in database (normal for new towers)", false)
}

/**
 * Risk level thresholds - raised to avoid false positives
 */
enum class TowerRiskLevel(val threshold: Int) {
    LOW(0),
    MEDIUM(35),      // Only real indicators trigger this
    HIGH(60),
    CRITICAL(85)
}

/**
 * Security analysis result with verified items
 */
data class TowerSecurityAnalysis(
    val riskScore: Int,
    val riskLevel: TowerRiskLevel,
    val indicators: List<TowerSecurityIndicator>,
    val verification: VerificationResult,
    val isSuspicious: Boolean,
    val recommendation: String,
    val statusTitle: String,
    val statusDescription: String
)

/**
 * Cell Tower Security Monitor
 * 
 * IMPORTANT: Only flags REAL security threats, not missing database entries.
 * A tower not being in OpenCellID just means it's new or in a less-reported area.
 * 
 * REAL threats we detect:
 * - 2G downgrade attacks (IMSI catchers force 2G to intercept)
 * - Signal strength spikes (fake towers broadcast very strong)
 * - Carrier mismatch (tower claims different carrier than your SIM)
 * - Rapid tower switching (unusual handoff patterns)
 */
@Singleton
class CellTowerSecurityMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cellTowerLookupService: CellTowerLookupService,
    private val cellTowerDao: CellTowerDao,
    private val emailAlertService: EmailAlertService
) {
    companion object {
        private const val RAPID_CHANGE_THRESHOLD_MS = 15_000L // 15 seconds
        private const val SIGNAL_SPIKE_THRESHOLD = -45 // dBm (VERY strong = suspicious)
        private const val ALERT_COOLDOWN_MS = 3600_000L // 1 hour
        private const val DUPLICATE_THRESHOLD_MS = 300_000L // 5 minutes - don't record same tower
    }
    
    // Track last known tower for change detection
    private var lastTowerId: String? = null
    private var lastTowerChangeTime: Long = 0
    private var lastAlertTime: Long = 0
    
    /**
     * Analyze a cell tower for REAL security threats
     */
    suspend fun analyzeTower(
        request: CellTowerRequest,
        towerLocation: CellTowerLocation?,
        currentSignalStrength: Int?,
        simCarrier: String?
    ): TowerSecurityAnalysis = withContext(Dispatchers.Default) {
        val threats = mutableListOf<TowerSecurityIndicator>()
        val info = mutableListOf<TowerSecurityIndicator>()
        
        // Build verification result first
        val verification = VerificationResult(
            isSimVerified = !simCarrier.isNullOrBlank(),
            isCarrierMatch = true, // Will be set to false if mismatch detected
            isConnectionActive = true, // We have cell info, so connection is active
            isNetworkTypeSecure = request.radioType.lowercase() in listOf("nr", "lte", "wcdma"),
            isInDatabase = towerLocation != null && towerLocation.securityStatus != TowerSecurityStatus.UNKNOWN,
            databaseSamples = towerLocation?.samples ?: 0
        )
        
        // ===== REAL THREATS =====
        
        // Threat 1: 2G Downgrade Attack (CRITICAL - IMSI catchers force 2G)
        if (request.radioType.lowercase() in listOf("gsm", "edge", "gprs")) {
            threats.add(TowerSecurityIndicator.DOWNGRADE_2G)
        }
        
        // Threat 2: Signal Spike (fake towers broadcast very strong signals)
        currentSignalStrength?.let {
            if (it > SIGNAL_SPIKE_THRESHOLD) {
                threats.add(TowerSecurityIndicator.SIGNAL_SPIKE)
            }
        }
        
        // Threat 3: Carrier Mismatch (tower carrier doesn't match SIM)
        // Only check if we have BOTH carrier names
        var carrierMatches = true
        if (!simCarrier.isNullOrBlank() && !towerLocation?.carrier.isNullOrBlank()) {
            val simLower = simCarrier.lowercase()
            val towerCarrier = towerLocation!!.carrier!!.lowercase()
            carrierMatches = simLower.contains(towerCarrier) || towerCarrier.contains(simLower) ||
                    isKnownCarrierMatch(simLower, towerCarrier)
            if (!carrierMatches) {
                threats.add(TowerSecurityIndicator.CARRIER_MISMATCH)
            }
        }
        
        // Threat 4: Rapid Tower Switching
        val now = System.currentTimeMillis()
        if (lastTowerId != null && 
            lastTowerId != request.cellId && 
            (now - lastTowerChangeTime) < RAPID_CHANGE_THRESHOLD_MS) {
            threats.add(TowerSecurityIndicator.RAPID_TOWER_CHANGE)
        }
        
        // ===== INFORMATIONAL (NOT THREATS) =====
        
        // Info 1: Not in database (just means new tower, NOT suspicious)
        if (towerLocation == null || towerLocation.securityStatus == TowerSecurityStatus.UNKNOWN) {
            info.add(TowerSecurityIndicator.NOT_IN_DATABASE)
        }
        
        // Info 2: Low sample count
        if (towerLocation != null && towerLocation.samples < 10) {
            info.add(TowerSecurityIndicator.LOW_SAMPLE_COUNT)
        }
        
        // Update tracking
        lastTowerId = request.cellId
        lastTowerChangeTime = now
        
        // Calculate risk score from REAL THREATS ONLY
        val riskScore = threats.filter { it.isCritical }.sumOf { it.weight }
        
        // Determine risk level
        val riskLevel = when {
            riskScore >= TowerRiskLevel.CRITICAL.threshold -> TowerRiskLevel.CRITICAL
            riskScore >= TowerRiskLevel.HIGH.threshold -> TowerRiskLevel.HIGH
            riskScore >= TowerRiskLevel.MEDIUM.threshold -> TowerRiskLevel.MEDIUM
            else -> TowerRiskLevel.LOW
        }
        
        // Generate user-friendly status
        val (statusTitle, statusDescription, recommendation) = generateStatus(
            riskLevel, threats, info, verification
        )
        
        // Combine threats and info for display
        val allIndicators = threats + info
        
        TowerSecurityAnalysis(
            riskScore = riskScore,
            riskLevel = riskLevel,
            indicators = allIndicators,
            verification = verification,
            isSuspicious = riskLevel >= TowerRiskLevel.HIGH,
            recommendation = recommendation,
            statusTitle = statusTitle,
            statusDescription = statusDescription
        )
    }
    
    /**
     * Check if carriers are known to be the same (different names for same company)
     */
    private fun isKnownCarrierMatch(sim: String, tower: String): Boolean {
        val carrierAliases = listOf(
            listOf("jio", "reliance", "rjio"),
            listOf("airtel", "bharti"),
            listOf("vi", "vodafone", "idea", "vodafone idea"),
            listOf("bsnl", "bharat sanchar"),
            listOf("att", "at&t", "at and t"),
            listOf("verizon", "vzw"),
            listOf("tmobile", "t-mobile", "t mobile")
        )
        
        return carrierAliases.any { aliases ->
            aliases.any { sim.contains(it) } && aliases.any { tower.contains(it) }
        }
    }
    
    /**
     * Generate user-friendly status messages
     */
    private fun generateStatus(
        riskLevel: TowerRiskLevel,
        threats: List<TowerSecurityIndicator>,
        info: List<TowerSecurityIndicator>,
        verification: VerificationResult
    ): Triple<String, String, String> {
        return when {
            riskLevel == TowerRiskLevel.CRITICAL -> Triple(
                "🚨 Critical Threat Detected",
                "Multiple serious security indicators found",
                "IMMEDIATE ACTION: Enable Airplane Mode and move to a different location. Avoid any sensitive communications."
            )
            riskLevel == TowerRiskLevel.HIGH -> Triple(
                "⚠️ High Risk Detected",
                threats.firstOrNull()?.description ?: "Security concern detected",
                "Consider enabling Airplane Mode. Avoid banking, passwords, and sensitive communications."
            )
            riskLevel == TowerRiskLevel.MEDIUM -> Triple(
                "⚠️ Caution Advised",
                threats.firstOrNull()?.description ?: "Minor concern detected",
                "Monitor your connection. Avoid very sensitive activities until you move to a different area."
            )
            // LOW risk - everything is fine
            verification.isInDatabase -> Triple(
                "✅ Connection Verified",
                "Tower found in verified database with ${verification.databaseSamples} reports",
                "Your connection appears secure. Normal network behavior detected."
            )
            verification.isSimVerified && verification.isNetworkTypeSecure -> Triple(
                "✅ Connection Secure",
                "SIM verified • ${if (verification.isNetworkTypeSecure) "Secure 4G/5G" else "Connected"} • No threats detected",
                "Connection is secure. Tower may be new and not yet in crowdsourced database - this is normal."
            )
            else -> Triple(
                "✅ Connection Active",
                "Connected to carrier network",
                "No security threats detected. Connection is normal."
            )
        }
    }
    
    /**
     * Record tower connection - ONLY if tower changed (no duplicates)
     */
    suspend fun onTowerConnected(
        request: CellTowerRequest,
        towerLocation: CellTowerLocation?,
        carrierName: String?,
        networkType: String?,
        signalStrength: Int?
    ) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        
        // Check if this is same tower as last entry (avoid duplicates)
        val lastEntry = cellTowerDao.getLastHistoryEntry()
        if (lastEntry?.cellId == request.cellId && 
            (now - lastEntry.connectedAt) < DUPLICATE_THRESHOLD_MS) {
            // Same tower within 5 minutes - don't add duplicate
            return@withContext
        }
        
        // Close any open connections
        cellTowerDao.closeOpenConnections(now)
        
        // Analyze security
        val analysis = analyzeTower(request, towerLocation, signalStrength, carrierName)
        
        // Get area name - reverse geocode if not provided
        val areaName = towerLocation?.areaName ?: towerLocation?.let {
            if (it.latitude != 0.0 && it.longitude != 0.0) {
                try {
                    val geocoder = android.location.Geocoder(context, java.util.Locale.getDefault())
                    if (android.location.Geocoder.isPresent()) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            // Android 13+ async with blocking wait
                            var result: String? = null
                            val latch = java.util.concurrent.CountDownLatch(1)
                            geocoder.getFromLocation(it.latitude, it.longitude, 1) { addresses ->
                                result = addresses.firstOrNull()?.let { addr ->
                                    listOfNotNull(addr.subLocality, addr.locality, addr.adminArea)
                                        .take(2).joinToString(", ")
                                }
                                latch.countDown()
                            }
                            latch.await(2, java.util.concurrent.TimeUnit.SECONDS)
                            result
                        } else {
                            // Pre-Android 13 sync call
                            @Suppress("DEPRECATION")
                            val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                            addresses?.firstOrNull()?.let { addr ->
                                listOfNotNull(addr.subLocality, addr.locality, addr.adminArea)
                                    .take(2).joinToString(", ")
                            }
                        }
                    } else null
                } catch (e: Exception) {
                    android.util.Log.e("CellTowerSecurityMonitor", "Geocode failed: ${e.message}")
                    null
                }
            } else null
        }
        
        // Record connection
        val historyEntry = CellTowerHistoryEntity(
            cellId = request.cellId,
            lac = request.lac,
            mcc = request.mcc,
            mnc = request.mnc,
            latitude = towerLocation?.latitude,
            longitude = towerLocation?.longitude,
            areaName = areaName,  // Now populated from geocoding
            carrierName = carrierName,
            networkType = networkType,
            signalStrength = signalStrength,
            connectedAt = now,
            disconnectedAt = null,
            securityStatus = analysis.riskLevel.name,
            wasAlertSent = false
        )
        cellTowerDao.insertHistory(historyEntry)
        
        // Only alert for REAL threats
        if (analysis.isSuspicious) {
            handleSuspiciousTower(request, towerLocation, analysis)
        }
    }
    
    /**
     * Handle detection of suspicious tower
     */
    private suspend fun handleSuspiciousTower(
        request: CellTowerRequest,
        towerLocation: CellTowerLocation?,
        analysis: TowerSecurityAnalysis
    ) {
        val now = System.currentTimeMillis()
        
        // Only real critical threats get incidents logged
        val criticalIndicators = analysis.indicators.filter { it.isCritical }
        if (criticalIndicators.isEmpty()) return
        
        // Create incident record
        val incident = CellTowerIncidentEntity(
            cellId = request.cellId,
            lac = request.lac,
            incidentType = determineIncidentType(analysis),
            riskLevel = analysis.riskLevel.name,
            description = analysis.recommendation,
            indicators = createIndicatorsJson(criticalIndicators),
            latitude = towerLocation?.latitude,
            longitude = towerLocation?.longitude,
            areaName = towerLocation?.areaName,
            occurredAt = now,
            wasEmailSent = false,
            wasResolved = false
        )
        val incidentId = cellTowerDao.insertIncident(incident)
        
        // Send email alert for HIGH or CRITICAL (with cooldown)
        if (analysis.riskLevel >= TowerRiskLevel.HIGH && 
            (now - lastAlertTime) > ALERT_COOLDOWN_MS) {
            sendSecurityAlert(request, towerLocation, analysis, incidentId)
            lastAlertTime = now
        }
    }
    
    private fun determineIncidentType(analysis: TowerSecurityAnalysis): String {
        return when {
            analysis.indicators.any { it == TowerSecurityIndicator.DOWNGRADE_2G } -> "DOWNGRADE_ATTACK"
            analysis.indicators.any { it == TowerSecurityIndicator.CARRIER_MISMATCH } -> "CARRIER_MISMATCH"
            analysis.indicators.any { it == TowerSecurityIndicator.SIGNAL_SPIKE } -> "POSSIBLE_FAKE_TOWER"
            else -> "SUSPICIOUS_ACTIVITY"
        }
    }
    
    private fun createIndicatorsJson(indicators: List<TowerSecurityIndicator>): String {
        val jsonArray = JSONArray()
        indicators.forEach { jsonArray.put(it.description) }
        return jsonArray.toString()
    }
    
    private suspend fun sendSecurityAlert(
        request: CellTowerRequest,
        towerLocation: CellTowerLocation?,
        analysis: TowerSecurityAnalysis,
        incidentId: Long
    ) {
        val indicatorList = analysis.indicators
            .filter { it.isCritical }
            .joinToString("\n") { "• ${it.description}" }
        
        val subject = when (analysis.riskLevel) {
            TowerRiskLevel.CRITICAL -> "🚨 CRITICAL: Cell Tower Security Alert"
            TowerRiskLevel.HIGH -> "⚠️ Security Alert: Suspicious Cell Tower"
            else -> "⚠️ Security Notice: Cell Tower Warning"
        }
        
        val body = """
            |Your device has connected to a suspicious cell tower.
            |
            |📍 TOWER DETAILS
            |━━━━━━━━━━━━━━━━━━━━
            |Cell Tower ID: ${request.cellId}
            |Location Area: ${request.lac}
            |Area: ${towerLocation?.areaName ?: "Unknown"}
            |
            |⚠️ DETECTED THREATS
            |━━━━━━━━━━━━━━━━━━━━
            |$indicatorList
            |
            |📋 RECOMMENDED ACTIONS
            |━━━━━━━━━━━━━━━━━━━━
            |1. Enable Airplane Mode immediately
            |2. Move to a different location
            |3. Avoid sensitive communications
            |4. Connect to trusted WiFi if available
            |
            |━━━━━━━━━━━━━━━━━━━━
            |Incident ID: $incidentId
            |Time: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}
        """.trimMargin()
        
        try {
            emailAlertService.sendAlert(subject, body)
            cellTowerDao.markEmailSent(incidentId)
        } catch (e: Exception) {
            // Email failed, will retry later
        }
    }
    
    suspend fun getRecentIncidents(limit: Int = 10): List<CellTowerIncidentEntity> {
        return cellTowerDao.getRecentIncidents(limit)
    }
    
    suspend fun getConnectionHistory(limit: Int = 50): List<CellTowerHistoryEntity> {
        return cellTowerDao.getRecentHistory(limit)
    }
    
    suspend fun hasActiveThreats(): Boolean {
        return cellTowerDao.getHighRiskIncidentCount() > 0
    }
}
