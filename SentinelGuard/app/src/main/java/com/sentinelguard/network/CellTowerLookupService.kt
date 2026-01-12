package com.sentinelguard.network

import android.content.Context
import android.location.Geocoder
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

import com.sentinelguard.data.local.preferences.SecurePreferences

/**
 * Tower type enumeration
 */
enum class TowerType {
    MACRO,      // Large coverage area (1-30 km)
    MICRO,      // Medium coverage (200m-2km)
    PICO,       // Small coverage (100-200m)
    FEMTO,      // Indoor (10-50m)
    UNKNOWN
}

/**
 * Security status of a cell tower
 */
enum class TowerSecurityStatus {
    VERIFIED,       // Tower exists in database with good samples
    UNVERIFIED,     // Tower exists but low confidence
    UNKNOWN,        // Tower not found in database
    SUSPICIOUS      // Tower flagged as potentially fake
}

/**
 * Complete cell tower location and details
 */
data class CellTowerLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Int,              // meters (estimate of location accuracy)
    val range: Int,                 // Tower coverage range in meters
    val samples: Int,               // Number of measurements in database
    val areaName: String?,          // Reverse geocoded area name
    val towerType: TowerType,
    val carrier: String?,
    val technologies: List<String>, // 2G, 3G, 4G, 5G
    val securityStatus: TowerSecurityStatus,
    val lastUpdated: Long           // When this data was fetched
)

/**
 * Cell tower lookup request data
 */
data class CellTowerRequest(
    val cellId: String,
    val lac: String,                // Location Area Code (2G/3G) or TAC (4G/5G)
    val mcc: String,                // Mobile Country Code
    val mnc: String,                // Mobile Network Code
    val radioType: String = "lte"   // gsm, cdma, wcdma, lte, nr
)

/**
 * Cell Tower Lookup Service
 * 
 * Uses OpenCellID API (primary) and Mozilla Location Service (fallback)
 * to get cell tower geolocation and details.
 * 
 * OpenCellID: Free community-driven database
 * - Register at: https://opencellid.org
 * - Returns: lat, lon, range, samples
 * 
 * Rate Limiting: Applied to all API calls to prevent abuse
 */
@Singleton
class CellTowerLookupService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val securePreferences: SecurePreferences,
    private val rateLimiter: com.sentinelguard.security.ratelimit.RateLimiter
) {
    // OpenCellID API token - registered at opencellid.org
    private var apiToken: String = "pk.38eb38bd47201f14d9a5f392b86039f7"
    
    // Google Geolocation API key - fallback for when OpenCellID doesn't have tower data
    // Best coverage for new 5G towers and Indian carriers
    private var googleApiKey: String = "AIzaSyCYqr1W41S69MFWK_uhW9S0wOrNTmiorcM"
    
    init {
        // Override with user-configured key if available
        securePreferences.googleGeolocationApiKey?.let { key ->
            if (key.isNotBlank()) {
                googleApiKey = key
                android.util.Log.d("CellTowerLookup", "Using custom Google API key from preferences")
            }
        }
    }
    
    /**
     * Check if Google Geolocation API is configured
     */
    fun isGoogleApiConfigured(): Boolean = googleApiKey.isNotBlank()
    
    /**
     * Check if OpenCellID API is configured
     */
    fun isOpenCellIdConfigured(): Boolean = apiToken.isNotBlank()
    
    companion object {
        private const val OPENCELLID_BASE_URL = "https://opencellid.org/cell/get"
        private const val MOZILLA_MLS_URL = "https://location.services.mozilla.com/v1/geolocate?key=test"
        private const val GOOGLE_GEOLOCATION_URL = "https://www.googleapis.com/geolocation/v1/geolocate"
        
        // Tower type estimation based on range
        fun estimateTowerType(range: Int): TowerType {
            return when {
                range <= 50 -> TowerType.FEMTO
                range <= 200 -> TowerType.PICO
                range <= 2000 -> TowerType.MICRO
                else -> TowerType.MACRO
            }
        }
        
        // Estimate technologies based on radio type
        fun estimateTechnologies(radioType: String): List<String> {
            return when (radioType.lowercase()) {
                "nr" -> listOf("5G", "4G", "3G", "2G")
                "lte" -> listOf("4G", "3G", "2G")
                "wcdma", "umts" -> listOf("3G", "2G")
                "gsm" -> listOf("2G")
                "cdma" -> listOf("3G", "2G")
                else -> listOf("4G", "3G", "2G")
            }
        }
    }
    
    /**
     * Set the OpenCellID API token
     */
    fun setApiToken(token: String) {
        apiToken = token
    }
    
    /**
     * Set the Google Geolocation API key
     * Get from: https://console.cloud.google.com/apis/credentials
     * Enable: Geolocation API in Google Cloud Console
     */
    fun setGoogleApiKey(key: String) {
        googleApiKey = key
    }
    
    /**
     * Check if Google API is configured
     */
    fun hasGoogleApiKey(): Boolean = googleApiKey.isNotBlank()
    
    /**
     * Lookup cell tower location using cascade fallback:
     * 1. OpenCellID (free, crowdsourced)
     * 2. Mozilla MLS (free, limited)
     * 3. Google Geolocation API (best coverage, paid beyond free tier)
     */
    suspend fun lookupCellTower(request: CellTowerRequest): CellTowerLocation? {
        return withContext(Dispatchers.IO) {
            // Check rate limit before making any API calls
            val rateLimitResult = rateLimiter.checkRateLimit("cell_tower_lookup", 20)
            if (!rateLimitResult.allowed) {
                android.util.Log.w("CellTowerLookup", "Rate limited: ${rateLimitResult.message}")
                return@withContext null
            }
            
            // Try cascade: OpenCellID -> Mozilla MLS -> Google
            val result = tryOpenCellId(request) 
                ?: tryMozillaMLS(request)
                ?: tryGoogleGeolocation(request)
            
            // If we got a result, add reverse geocoding
            result?.let { location ->
                val areaName = location.areaName ?: reverseGeocode(location.latitude, location.longitude)
                location.copy(areaName = areaName)
            }
        }
    }
    
    /**
     * Try OpenCellID API for tower location
     */
    private fun tryOpenCellId(request: CellTowerRequest): CellTowerLocation? {
        // Skip if no API token configured
        if (apiToken.isBlank()) {
            android.util.Log.d("CellTowerLookup", "OpenCellID: No API token configured - skipping")
            return null
        }
        
        return try {
            val cellId = request.cellId.toLongOrNull()
            val lac = request.lac.toIntOrNull()
            val mcc = request.mcc.toIntOrNull()
            val mnc = request.mnc.toIntOrNull()
            
            android.util.Log.d("CellTowerLookup", "OpenCellID lookup: mcc=$mcc, mnc=$mnc, lac=$lac, cellId=$cellId")
            
            if (cellId == null || lac == null || mcc == null || mnc == null) {
                android.util.Log.w("CellTowerLookup", "OpenCellID: Invalid cell params - skipping")
                return null
            }
            
            val url = URL("$OPENCELLID_BASE_URL?key=$apiToken&mcc=$mcc&mnc=$mnc&lac=$lac&cellid=$cellId&format=json")
            android.util.Log.d("CellTowerLookup", "OpenCellID URL: [REDACTED]")
            
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.requestMethod = "GET"
            
            val responseCode = connection.responseCode
            android.util.Log.d("CellTowerLookup", "OpenCellID response code: $responseCode")
            
            if (responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                android.util.Log.d("CellTowerLookup", "OpenCellID response: $response")
                
                val json = JSONObject(response)
                
                // Check for error
                if (json.has("error")) {
                    android.util.Log.w("CellTowerLookup", "OpenCellID error: ${json.optString("error")}")
                    return null
                }
                
                val lat = json.optDouble("lat", 0.0)
                val lon = json.optDouble("lon", 0.0)
                val range = json.optInt("range", 1000)
                val samples = json.optInt("samples", 0)
                
                android.util.Log.d("CellTowerLookup", "OpenCellID found: lat=$lat, lon=$lon, samples=$samples")
                
                // Estimate accuracy based on samples
                val accuracy = when {
                    samples > 100 -> range / 2
                    samples > 10 -> range
                    else -> range * 2
                }
                
                // Determine security status
                val securityStatus = when {
                    samples > 50 -> TowerSecurityStatus.VERIFIED
                    samples > 5 -> TowerSecurityStatus.UNVERIFIED
                    else -> TowerSecurityStatus.UNKNOWN
                }
                
                CellTowerLocation(
                    latitude = lat,
                    longitude = lon,
                    accuracy = accuracy,
                    range = range,
                    samples = samples,
                    areaName = null, // Will be filled by reverse geocoding
                    towerType = estimateTowerType(range),
                    carrier = null, // OpenCellID doesn't provide carrier
                    technologies = estimateTechnologies(request.radioType),
                    securityStatus = securityStatus,
                    lastUpdated = System.currentTimeMillis()
                )
            } else {
                android.util.Log.w("CellTowerLookup", "OpenCellID HTTP error: $responseCode")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("CellTowerLookup", "OpenCellID exception: ${e.message}")
            null
        }
    }
    
    /**
     * Fallback: Mozilla Location Service
     */
    private fun tryMozillaMLS(request: CellTowerRequest): CellTowerLocation? {
        return try {
            val cellId = request.cellId.toLongOrNull() ?: return null
            val lac = request.lac.toIntOrNull() ?: return null
            val mcc = request.mcc.toIntOrNull() ?: return null
            val mnc = request.mnc.toIntOrNull() ?: return null
            
            val radioType = when (request.radioType.lowercase()) {
                "lte" -> "lte"
                "wcdma", "umts" -> "wcdma"
                "nr" -> "lte" // MLS doesn't support 5G yet, use LTE
                else -> "gsm"
            }
            
            val requestBody = """
                {
                    "cellTowers": [{
                        "radioType": "$radioType",
                        "mobileCountryCode": $mcc,
                        "mobileNetworkCode": $mnc,
                        "locationAreaCode": $lac,
                        "cellId": $cellId
                    }]
                }
            """.trimIndent()
            
            val url = URL(MOZILLA_MLS_URL)
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            
            connection.outputStream.bufferedWriter().use { it.write(requestBody) }
            
            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)
                
                val location = json.optJSONObject("location") ?: return null
                val lat = location.optDouble("lat", 0.0)
                val lon = location.optDouble("lng", 0.0)
                val accuracy = json.optDouble("accuracy", 1000.0).toInt()
                
                CellTowerLocation(
                    latitude = lat,
                    longitude = lon,
                    accuracy = accuracy,
                    range = accuracy,
                    samples = 1, // MLS doesn't provide sample count
                    areaName = null,
                    towerType = estimateTowerType(accuracy),
                    carrier = null,
                    technologies = estimateTechnologies(request.radioType),
                    securityStatus = TowerSecurityStatus.UNKNOWN, // Lower confidence
                    lastUpdated = System.currentTimeMillis()
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Fallback 2: Google Geolocation API
     * Best coverage (has data from billions of Android devices)
     * Free tier: 40,000 requests/month
     * 
     * API Docs: https://developers.google.com/maps/documentation/geolocation/overview
     */
    private fun tryGoogleGeolocation(request: CellTowerRequest): CellTowerLocation? {
        // Skip if no API key configured
        if (googleApiKey.isBlank()) {
            android.util.Log.w("CellTowerLookup", "Google API: No API key configured")
            return null
        }
        
        return try {
            val cellId = request.cellId.toLongOrNull()
            val lac = request.lac.toIntOrNull()
            val mcc = request.mcc.toIntOrNull()
            val mnc = request.mnc.toIntOrNull()
            
            android.util.Log.d("CellTowerLookup", "Google API lookup: mcc=$mcc, mnc=$mnc, lac=$lac, cellId=$cellId, radioType='${request.radioType}'")
            
            if (cellId == null || lac == null || mcc == null || mnc == null) {
                android.util.Log.w("CellTowerLookup", "Google API: Invalid cell params - skipping")
                return null
            }
            
            // Map radio type to Google's expected format
            // IMPORTANT: Google Geolocation API only supports: gsm, cdma, wcdma, lte
            // 5G NR is NOT supported yet - must fall back to LTE
            val is5G = request.radioType.lowercase().trim() in listOf("nr", "5g")
            val radioType = when (request.radioType.lowercase().trim()) {
                "nr", "5g" -> "lte"          // 5G must fallback to LTE (Google doesn't support NR)
                "lte", "4g" -> "lte"         // 4G LTE
                "wcdma", "umts", "3g" -> "wcdma"  // 3G
                "cdma" -> "cdma"             // CDMA
                "gsm", "2g" -> "gsm"         // 2G GSM
                "" -> "lte"                  // Empty = assume LTE (most common)
                else -> "lte"                // Unknown = assume LTE
            }
            
            // Handle cell ID size limits:
            // - 5G NR cell IDs are 36-bit (can exceed 4 billion)
            // - Google's LTE cellId is UINT32 (max 4,294,967,295)
            // - LTE cell IDs should be 28-bit (max 268,435,455)
            // 
            // For 5G cells, we extract the lower 28 bits as the "cell identity" part
            // This gives us the cell sector within the gNB (base station)
            val maxLteCellId = 268435455L  // 2^28 - 1
            val maxUInt32 = 4294967295L    // 2^32 - 1
            
            val adjustedCellId = when {
                cellId <= maxLteCellId -> cellId  // Already fits in 28 bits
                cellId <= maxUInt32 && !is5G -> cellId  // Fits in UINT32, not 5G
                is5G -> {
                    // For 5G NR: Extract eNB portion (bits 8-27) which often correlates 
                    // with the LTE anchor cell on the same tower
                    val enbId = (cellId shr 8) and 0xFFFFF  // 20 bits for eNB ID
                    val sectorId = cellId and 0xFF          // 8 bits for sector
                    val lteCellId = (enbId shl 8) or sectorId  // Combine to 28-bit format
                    android.util.Log.d("CellTowerLookup", "5G NR cell ID $cellId -> LTE compatible $lteCellId")
                    lteCellId
                }
                else -> cellId and maxLteCellId  // Fallback: mask to 28 bits
            }
            
            android.util.Log.d("CellTowerLookup", "Google API using radioType: $radioType, cellId: $adjustedCellId (original: '${request.radioType}', cellId: $cellId)")
            
            // Build request body with adjusted cellId
            val requestBody = """
            {
                "cellTowers": [{
                    "radioType": "$radioType",
                    "mobileCountryCode": $mcc,
                    "mobileNetworkCode": $mnc,
                    "locationAreaCode": $lac,
                    "cellId": $adjustedCellId
                }]
            }
            """.trimIndent()
            
            android.util.Log.d("CellTowerLookup", "Google API request body: $requestBody")
            
            val url = URL("$GOOGLE_GEOLOCATION_URL?key=$googleApiKey")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            
            connection.outputStream.bufferedWriter().use { it.write(requestBody) }
            
            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)
                
                val location = json.optJSONObject("location") ?: return null
                val lat = location.optDouble("lat", 0.0)
                val lon = location.optDouble("lng", 0.0)
                val rawAccuracy = json.optDouble("accuracy", 1000.0).toInt()
                
                android.util.Log.d("CellTowerLookup", "Google API found tower at: $lat, $lon (raw accuracy: $rawAccuracy)")
                
                // Cap accuracy to reasonable tower range values
                // Large accuracy values (>10km) indicate low confidence - use estimated ranges
                // Typical tower ranges:
                // - 5G NR: 100-500m (small cells) to 1-3km (macro)
                // - LTE: 1-10km typical, up to 30km rural
                // - 3G: 2-5km
                // - GSM: 5-35km
                val maxReasonableRange = when (radioType) {
                    "lte" -> if (is5G) 3000 else 10000   // 5G converted to LTE: 3km max, pure LTE: 10km
                    "wcdma" -> 5000                      // 3G: 5km max
                    "gsm" -> 35000                       // 2G: 35km max
                    else -> 10000                        // Default: 10km
                }
                
                // Use capped accuracy for display, but keep actual accuracy for internal use
                val displayRange = if (rawAccuracy > maxReasonableRange) {
                    android.util.Log.d("CellTowerLookup", "Low confidence result, capping range from ${rawAccuracy}m to ${maxReasonableRange}m")
                    maxReasonableRange
                } else {
                    rawAccuracy
                }
                
                // For 5G, estimate closer range since towers are typically smaller
                val estimatedRange = if (is5G && displayRange > 2000) {
                    2000  // 5G typically 500m-2km
                } else {
                    displayRange
                }
                
                CellTowerLocation(
                    latitude = lat,
                    longitude = lon,
                    accuracy = estimatedRange,  // Use capped/estimated value
                    range = estimatedRange,     // Tower range for display
                    samples = if (rawAccuracy > 10000) 10 else 100, // Low samples if low confidence
                    areaName = null, // Will be reverse geocoded
                    towerType = estimateTowerType(estimatedRange),
                    carrier = null,
                    technologies = estimateTechnologies(request.radioType),
                    securityStatus = if (rawAccuracy > 50000) TowerSecurityStatus.UNVERIFIED else TowerSecurityStatus.VERIFIED,
                    lastUpdated = System.currentTimeMillis()
                )
            } else {
                val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                android.util.Log.w("CellTowerLookup", "Google API error ${connection.responseCode}: $errorBody")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("CellTowerLookup", "Google API failed: ${e.message}")
            null
        }
    }
    
    /**
     * Reverse geocode coordinates to area name
     * Fixed: Uses CountDownLatch to properly wait for async callback on Android 13+
     */
    @Suppress("DEPRECATION")
    private fun reverseGeocode(latitude: Double, longitude: Double): String? {
        // Skip if coordinates are invalid
        if (latitude == 0.0 && longitude == 0.0) {
            android.util.Log.w("CellTowerLookup", "reverseGeocode: Invalid coordinates (0,0)")
            return null
        }
        
        android.util.Log.d("CellTowerLookup", "reverseGeocode: lat=$latitude, lon=$longitude")
        
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            
            if (!Geocoder.isPresent()) {
                android.util.Log.w("CellTowerLookup", "Geocoder not available on this device")
                return null
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+ uses async callback
                val latch = java.util.concurrent.CountDownLatch(1)
                var result: String? = null
                
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0]
                        result = buildString {
                            address.subLocality?.let { append(it).append(", ") }
                            address.locality?.let { append(it) }
                            if (isEmpty()) address.adminArea?.let { append(it) }
                        }.takeIf { it.isNotBlank() }
                        android.util.Log.d("CellTowerLookup", "Geocoder async result: $result")
                    } else {
                        android.util.Log.w("CellTowerLookup", "Geocoder: No addresses found")
                    }
                    latch.countDown()
                }
                
                // Wait up to 3 seconds for result
                latch.await(3, java.util.concurrent.TimeUnit.SECONDS)
                result
            } else {
                // Pre-Android 13: synchronous call
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val result = buildString {
                        address.subLocality?.let { append(it).append(", ") }
                        address.locality?.let { append(it) }
                        if (isEmpty()) address.adminArea?.let { append(it) }
                    }.takeIf { it.isNotBlank() }
                    android.util.Log.d("CellTowerLookup", "Geocoder sync result: $result")
                    result
                } else {
                    android.util.Log.w("CellTowerLookup", "Geocoder: No addresses found (sync)")
                    null
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("CellTowerLookup", "reverseGeocode error: ${e.message}")
            null
        }
    }
    
    /**
     * Calculate distance between two coordinates in meters
     */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000.0 // meters
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        
        return earthRadius * c
    }
    
    /**
     * Format distance for display
     */
    fun formatDistance(meters: Double): String {
        return when {
            meters < 1000 -> "${meters.toInt()} m"
            else -> String.format("%.1f km", meters / 1000)
        }
    }
}
