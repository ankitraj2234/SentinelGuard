package com.sentinelguard.network

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.CellIdentityGsm
import android.telephony.CellIdentityLte
import android.telephony.CellIdentityNr
import android.telephony.CellIdentityWcdma
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoNr
import android.telephony.CellInfoWcdma
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Connection type enumeration
 */
enum class ConnectionType {
    WIFI,
    MOBILE,
    ETHERNET,
    VPN,
    NONE
}

/**
 * WiFi frequency band
 */
enum class WifiFrequency {
    BAND_2_4_GHZ,
    BAND_5_GHZ,
    BAND_6_GHZ,
    UNKNOWN
}

/**
 * Complete network information data class
 */
data class NetworkInfo(
    // General
    val connectionType: ConnectionType = ConnectionType.NONE,
    val isConnected: Boolean = false,
    val isVpnActive: Boolean = false,
    val ipAddress: String? = null,
    val publicIpAddress: String? = null,   // Public IP (from external lookup)
    val ispName: String? = null,            // ISP provider name
    val gateway: String? = null,
    val dnsServers: List<String> = emptyList(),
    val subnetMask: String? = null,
    
    // WiFi specific
    val wifiInfo: WifiNetworkInfo? = null,
    
    // Mobile specific
    val mobileInfo: MobileNetworkInfo? = null
)

/**
 * WiFi network details
 */
data class WifiNetworkInfo(
    val ssid: String,                    // Network name
    val bssid: String?,                  // Router MAC address
    val encryptionType: String,          // WPA2, WPA3, etc.
    val frequency: WifiFrequency,        // 2.4GHz or 5GHz
    val frequencyMhz: Int,               // Exact frequency in MHz
    val signalStrength: Int,             // Signal in dBm
    val signalPercent: Int,              // 0-100%
    val linkSpeed: Int,                  // Link speed in Mbps
    val channelWidth: String?,           // Channel bandwidth
    val isHidden: Boolean = false
)

/**
 * Mobile/Cellular network details
 */
data class MobileNetworkInfo(
    val carrierName: String,             // Operator name (e.g., "Jio", "Airtel")
    val networkType: String,             // 4G, 5G, LTE, 3G, 2G
    val networkTypeTechnical: String,    // LTE, NR, HSPA+, etc.
    val isRoaming: Boolean,
    val signalStrength: Int?,            // dBm (if available)
    val signalBars: Int?,                // 0-4 bars
    val dataState: String,               // Connected, Disconnected, etc.
    val cellId: String?,                 // Cell tower ID
    val lac: String?,                    // Location Area Code
    val mcc: String?,                    // Mobile Country Code
    val mnc: String?,                    // Mobile Network Code
    val simOperator: String?,            // SIM operator name
    val simCountry: String?,             // SIM country
    val phoneNumber: String?,            // Phone number (if available)
    val simSlot: Int?,                   // SIM slot (0 or 1 for dual SIM)
    val simSerialNumber: String?,        // SIM serial (ICCID)
    val imei: String?                    // Device IMEI (if available)
)

/**
 * Comprehensive Network Information Manager
 * 
 * Collects detailed information about the current network connection
 * including WiFi details, mobile data info, and IP/DNS configuration.
 */
@Singleton
class NetworkInfoManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    /**
     * Get comprehensive network information
     */
    fun getNetworkInfo(): NetworkInfo {
        val network = connectivityManager.activeNetwork
        val capabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }
        val linkProperties = network?.let { connectivityManager.getLinkProperties(it) }
        
        val connectionType = getConnectionType(capabilities)
        val isConnected = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        val isVpn = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true
        
        return NetworkInfo(
            connectionType = connectionType,
            isConnected = isConnected,
            isVpnActive = isVpn,
            ipAddress = getIpAddress(linkProperties),
            gateway = getGateway(linkProperties),
            dnsServers = getDnsServers(linkProperties),
            subnetMask = getSubnetMask(linkProperties),
            wifiInfo = if (connectionType == ConnectionType.WIFI) getWifiInfo() else null,
            mobileInfo = if (connectionType == ConnectionType.MOBILE) getMobileInfo() else null
        )
    }
    
    private fun getConnectionType(capabilities: NetworkCapabilities?): ConnectionType {
        return when {
            capabilities == null -> ConnectionType.NONE
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> ConnectionType.VPN
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionType.MOBILE
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ConnectionType.ETHERNET
            else -> ConnectionType.NONE
        }
    }
    
    private fun getIpAddress(linkProperties: LinkProperties?): String? {
        // First try from link properties
        linkProperties?.linkAddresses?.forEach { linkAddress ->
            val address = linkAddress.address
            if (address is Inet4Address && !address.isLoopbackAddress) {
                return address.hostAddress
            }
        }
        
        // Fallback: enumerate network interfaces
        try {
            NetworkInterface.getNetworkInterfaces()?.toList()?.forEach { networkInterface ->
                networkInterface.inetAddresses?.toList()?.forEach { address ->
                    if (!address.isLoopbackAddress && address is Inet4Address) {
                        return address.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore
        }
        
        return null
    }
    
    private fun getGateway(linkProperties: LinkProperties?): String? {
        return try {
            linkProperties?.routes?.firstOrNull { it.isDefaultRoute }?.gateway?.hostAddress
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getDnsServers(linkProperties: LinkProperties?): List<String> {
        return try {
            linkProperties?.dnsServers?.mapNotNull { it.hostAddress } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun getSubnetMask(linkProperties: LinkProperties?): String? {
        return try {
            val prefixLength = linkProperties?.linkAddresses?.firstOrNull { 
                it.address is Inet4Address 
            }?.prefixLength ?: return null
            
            prefixLengthToSubnetMask(prefixLength)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun prefixLengthToSubnetMask(prefixLength: Int): String {
        val mask = (-1 shl (32 - prefixLength)).toLong() and 0xFFFFFFFFL
        return "${(mask shr 24) and 0xFF}.${(mask shr 16) and 0xFF}.${(mask shr 8) and 0xFF}.${mask and 0xFF}"
    }
    
    /**
     * Get detailed WiFi information
     */
    @SuppressLint("MissingPermission")
    private fun getWifiInfo(): WifiNetworkInfo? {
        if (!hasWifiPermission()) return null
        
        return try {
            val wifiInfo: WifiInfo? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+ - need to use callback or network capabilities
                val network = connectivityManager.activeNetwork
                val capabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }
                @Suppress("DEPRECATION")
                capabilities?.transportInfo as? WifiInfo
            } else {
                @Suppress("DEPRECATION")
                wifiManager.connectionInfo
            }
            
            if (wifiInfo == null || wifiInfo.networkId == -1) return null
            
            val ssid = wifiInfo.ssid?.removeSurrounding("\"") ?: "Unknown"
            val frequency = wifiInfo.frequency
            val wifiFrequency = when {
                frequency in 2400..2500 -> WifiFrequency.BAND_2_4_GHZ
                frequency in 5150..5900 -> WifiFrequency.BAND_5_GHZ
                frequency in 5925..7125 -> WifiFrequency.BAND_6_GHZ
                else -> WifiFrequency.UNKNOWN
            }
            
            // Calculate signal percentage (rssi typically -100 to -30 dBm)
            val rssi = wifiInfo.rssi
            val signalPercent = WifiManager.calculateSignalLevel(rssi, 100)
            
            // Get encryption type
            val encryptionType = getWifiEncryptionType()
            
            WifiNetworkInfo(
                ssid = if (ssid == "<unknown ssid>") "Hidden Network" else ssid,
                bssid = wifiInfo.bssid,
                encryptionType = encryptionType,
                frequency = wifiFrequency,
                frequencyMhz = frequency,
                signalStrength = rssi,
                signalPercent = signalPercent,
                linkSpeed = wifiInfo.linkSpeed,
                channelWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    getChannelWidth(wifiInfo)
                } else null,
                isHidden = ssid == "<unknown ssid>"
            )
        } catch (e: Exception) {
            null
        }
    }
    
    @SuppressLint("MissingPermission")
    private fun getWifiEncryptionType(): String {
        // Try to get from scan results
        return try {
            if (hasWifiPermission()) {
                @Suppress("DEPRECATION")
                val scanResults = wifiManager.scanResults
                val currentBssid = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val network = connectivityManager.activeNetwork
                    val capabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }
                    @Suppress("DEPRECATION")
                    (capabilities?.transportInfo as? WifiInfo)?.bssid
                } else {
                    @Suppress("DEPRECATION")
                    wifiManager.connectionInfo?.bssid
                }
                
                val currentNetwork = scanResults?.find { it.BSSID == currentBssid }
                currentNetwork?.capabilities?.let { caps ->
                    when {
                        caps.contains("WPA3") -> "WPA3"
                        caps.contains("WPA2") -> "WPA2"
                        caps.contains("WPA") -> "WPA"
                        caps.contains("WEP") -> "WEP"
                        caps.contains("OWE") -> "OWE (Enhanced Open)"
                        else -> "Open"
                    }
                } ?: "Unknown"
            } else {
                "Unknown"
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }
    
    @SuppressLint("NewApi")
    private fun getChannelWidth(wifiInfo: WifiInfo): String? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Not directly available, estimate from frequency
                null
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Get detailed mobile/cellular information
     */
    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getMobileInfo(): MobileNetworkInfo? {
        return try {
            val carrierName = telephonyManager.networkOperatorName.takeIf { it.isNotBlank() } ?: "Unknown"
            val simOperator = telephonyManager.simOperatorName.takeIf { it.isNotBlank() }
            val simCountry = telephonyManager.simCountryIso?.uppercase()
            
            // Network type
            val networkType = getNetworkTypeString()
            val networkTypeTechnical = getTechnicalNetworkType()
            
            // Data state
            val dataState = when (telephonyManager.dataState) {
                TelephonyManager.DATA_CONNECTED -> "Connected"
                TelephonyManager.DATA_CONNECTING -> "Connecting"
                TelephonyManager.DATA_DISCONNECTED -> "Disconnected"
                TelephonyManager.DATA_SUSPENDED -> "Suspended"
                else -> "Unknown"
            }
            
            val isRoaming = telephonyManager.isNetworkRoaming
            
            // Cell info (requires permission)
            val (cellId, lac, mcc, mnc) = getCellInfo()
            
            // Phone number (requires READ_PHONE_NUMBERS or READ_SMS permission)
            val phoneNumber = getPhoneNumber()
            
            // SIM serial (ICCID)
            val simSerial = getSimSerialNumber()
            
            // IMEI
            val imei = getImei()
            
            MobileNetworkInfo(
                carrierName = carrierName,
                networkType = networkType,
                networkTypeTechnical = networkTypeTechnical,
                isRoaming = isRoaming,
                signalStrength = null, // Requires SignalStrengthCallback
                signalBars = null,
                dataState = dataState,
                cellId = cellId,
                lac = lac,
                mcc = mcc,
                mnc = mnc,
                simOperator = simOperator,
                simCountry = simCountry,
                phoneNumber = phoneNumber,
                simSlot = getActiveDataSimSlot(), // Detect actual data SIM slot
                simSerialNumber = simSerial,
                imei = imei
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Get the active data SIM slot (0-indexed)
     * Returns 0 for SIM1, 1 for SIM2, null if unknown
     */
    @SuppressLint("MissingPermission")
    private fun getActiveDataSimSlot(): Int? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager
                val defaultDataSubId = SubscriptionManager.getDefaultDataSubscriptionId()
                
                if (defaultDataSubId != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                    // Get slot index from subscription ID
                    val subscriptionInfo = subscriptionManager?.getActiveSubscriptionInfo(defaultDataSubId)
                    subscriptionInfo?.simSlotIndex
                } else {
                    // Fallback: check which subscription is for data
                    val activeList = subscriptionManager?.activeSubscriptionInfoList
                    activeList?.firstOrNull()?.simSlotIndex
                }
            } else {
                0 // Pre-N devices typically use slot 0
            }
        } catch (e: Exception) {
            android.util.Log.e("NetworkInfoManager", "Error getting SIM slot: ${e.message}")
            null
        }
    }
    
    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getPhoneNumber(): String? {
        return try {
            if (hasPhonePermission()) {
                val line1Number = telephonyManager.line1Number
                if (!line1Number.isNullOrBlank()) {
                    // Format the number nicely
                    line1Number.takeIf { it.length >= 10 }
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getSimSerialNumber(): String? {
        return try {
            if (hasPhonePermission()) {
                telephonyManager.simSerialNumber?.takeIf { it.isNotBlank() }
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getImei(): String? {
        return try {
            if (hasPhonePermission() && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                // IMEI access restricted on Android 10+
                @Suppress("DEPRECATION")
                telephonyManager.deviceId?.takeIf { it.isNotBlank() }
            } else null
        } catch (e: Exception) {
            null
        }
    }

    
    @SuppressLint("MissingPermission")
    private fun getNetworkTypeString(): String {
        val networkType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            telephonyManager.dataNetworkType
        } else {
            @Suppress("DEPRECATION")
            telephonyManager.networkType
        }
        
        return when (networkType) {
            TelephonyManager.NETWORK_TYPE_NR -> "5G"
            TelephonyManager.NETWORK_TYPE_LTE -> "4G LTE"
            TelephonyManager.NETWORK_TYPE_HSPAP,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA -> "3G+"
            TelephonyManager.NETWORK_TYPE_UMTS,
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_EVDO_B -> "3G"
            TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_GPRS,
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_1xRTT -> "2G"
            TelephonyManager.NETWORK_TYPE_UNKNOWN -> "Unknown"
            else -> "Unknown"
        }
    }
    
    @SuppressLint("MissingPermission")
    private fun getTechnicalNetworkType(): String {
        val networkType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            telephonyManager.dataNetworkType
        } else {
            @Suppress("DEPRECATION")
            telephonyManager.networkType
        }
        
        return when (networkType) {
            TelephonyManager.NETWORK_TYPE_NR -> "NR (5G)"
            TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
            TelephonyManager.NETWORK_TYPE_HSPAP -> "HSPA+"
            TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA"
            TelephonyManager.NETWORK_TYPE_HSDPA -> "HSDPA"
            TelephonyManager.NETWORK_TYPE_HSUPA -> "HSUPA"
            TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS"
            TelephonyManager.NETWORK_TYPE_EVDO_0 -> "EVDO Rev.0"
            TelephonyManager.NETWORK_TYPE_EVDO_A -> "EVDO Rev.A"
            TelephonyManager.NETWORK_TYPE_EVDO_B -> "EVDO Rev.B"
            TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE"
            TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS"
            TelephonyManager.NETWORK_TYPE_CDMA -> "CDMA"
            TelephonyManager.NETWORK_TYPE_1xRTT -> "1xRTT"
            else -> "Unknown"
        }
    }
    
    @SuppressLint("MissingPermission")
    private fun getCellInfo(): CellTowerInfo {
        if (!hasPhonePermission()) {
            return CellTowerInfo(null, null, null, null)
        }
        
        return try {
            val cellInfoList = telephonyManager.allCellInfo
            
            for (cellInfo in cellInfoList ?: emptyList()) {
                when (cellInfo) {
                    is CellInfoLte -> {
                        val identity = cellInfo.cellIdentity
                        return CellTowerInfo(
                            cellId = identity.ci.takeIf { it != Int.MAX_VALUE }?.toString(),
                            lac = identity.tac.takeIf { it != Int.MAX_VALUE }?.toString(),
                            mcc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                identity.mccString
                            } else {
                                @Suppress("DEPRECATION")
                                identity.mcc.takeIf { it != Int.MAX_VALUE }?.toString()
                            },
                            mnc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                identity.mncString
                            } else {
                                @Suppress("DEPRECATION")
                                identity.mnc.takeIf { it != Int.MAX_VALUE }?.toString()
                            }
                        )
                    }
                    is CellInfoWcdma -> {
                        val identity = cellInfo.cellIdentity
                        return CellTowerInfo(
                            cellId = identity.cid.takeIf { it != Int.MAX_VALUE }?.toString(),
                            lac = identity.lac.takeIf { it != Int.MAX_VALUE }?.toString(),
                            mcc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                identity.mccString
                            } else {
                                @Suppress("DEPRECATION")
                                identity.mcc.takeIf { it != Int.MAX_VALUE }?.toString()
                            },
                            mnc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                identity.mncString
                            } else {
                                @Suppress("DEPRECATION")
                                identity.mnc.takeIf { it != Int.MAX_VALUE }?.toString()
                            }
                        )
                    }
                    is CellInfoGsm -> {
                        val identity = cellInfo.cellIdentity
                        return CellTowerInfo(
                            cellId = identity.cid.takeIf { it != Int.MAX_VALUE }?.toString(),
                            lac = identity.lac.takeIf { it != Int.MAX_VALUE }?.toString(),
                            mcc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                identity.mccString
                            } else {
                                @Suppress("DEPRECATION")
                                identity.mcc.takeIf { it != Int.MAX_VALUE }?.toString()
                            },
                            mnc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                identity.mncString
                            } else {
                                @Suppress("DEPRECATION")
                                identity.mnc.takeIf { it != Int.MAX_VALUE }?.toString()
                            }
                        )
                    }
                    is CellInfoNr -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val identity = cellInfo.cellIdentity as? CellIdentityNr
                            if (identity != null) {
                                return CellTowerInfo(
                                    cellId = identity.nci.takeIf { it != Long.MAX_VALUE }?.toString(),
                                    lac = identity.tac.takeIf { it != Int.MAX_VALUE }?.toString(),
                                    mcc = identity.mccString,
                                    mnc = identity.mncString
                                )
                            }
                        }
                    }
                }
            }
            
            CellTowerInfo(null, null, null, null)
        } catch (e: Exception) {
            CellTowerInfo(null, null, null, null)
        }
    }
    
    private data class CellTowerInfo(
        val cellId: String?,
        val lac: String?,
        val mcc: String?,
        val mnc: String?
    )
    
    private fun hasWifiPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE) == 
            PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == 
            PackageManager.PERMISSION_GRANTED
    }
    
    private fun hasPhonePermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == 
            PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == 
            PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Data class for ISP information from IP lookup
     */
    data class IspInfo(
        val publicIp: String?,
        val ispName: String?,
        val organization: String?,
        val city: String?,
        val region: String?,
        val country: String?
    )
    
    /**
     * Fetch ISP information using public IP lookup APIs
     * Tries multiple HTTPS APIs with fallback for reliability
     */
    suspend fun fetchIspInfo(): IspInfo {
        return withContext(Dispatchers.IO) {
            // Try primary API: ipinfo.io (HTTPS, free tier)
            tryIpInfoIo() ?: tryIpApiCo() ?: tryIpApiCom() ?: IspInfo(null, null, null, null, null, null)
        }
    }
    
    /**
     * Primary: ipinfo.io (HTTPS, reliable, 50k/month free)
     */
    private fun tryIpInfoIo(): IspInfo? {
        return try {
            val url = URL("https://ipinfo.io/json")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.requestMethod = "GET"
            
            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)
                
                IspInfo(
                    publicIp = json.optString("ip").takeIf { it.isNotBlank() },
                    ispName = json.optString("org").takeIf { it.isNotBlank() }?.let { org ->
                        // org format: "AS12345 Company Name", extract company name
                        if (org.startsWith("AS")) org.substringAfter(" ").trim() else org
                    },
                    organization = json.optString("org").takeIf { it.isNotBlank() },
                    city = json.optString("city").takeIf { it.isNotBlank() },
                    region = json.optString("region").takeIf { it.isNotBlank() },
                    country = json.optString("country").takeIf { it.isNotBlank() }
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Fallback 1: ipapi.co (HTTPS, 30k/month free)
     */
    private fun tryIpApiCo(): IspInfo? {
        return try {
            val url = URL("https://ipapi.co/json/")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.requestMethod = "GET"
            
            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)
                
                IspInfo(
                    publicIp = json.optString("ip").takeIf { it.isNotBlank() },
                    ispName = json.optString("org").takeIf { it.isNotBlank() },
                    organization = json.optString("org").takeIf { it.isNotBlank() },
                    city = json.optString("city").takeIf { it.isNotBlank() },
                    region = json.optString("region").takeIf { it.isNotBlank() },
                    country = json.optString("country_name").takeIf { it.isNotBlank() }
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Fallback 2: ip-api.com (HTTP only - needs network security config)
     */
    private fun tryIpApiCom(): IspInfo? {
        return try {
            val url = URL("http://ip-api.com/json/?fields=status,query,isp,org,city,regionName,country")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.requestMethod = "GET"
            
            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)
                
                if (json.optString("status") == "success") {
                    IspInfo(
                        publicIp = json.optString("query").takeIf { it.isNotBlank() },
                        ispName = json.optString("isp").takeIf { it.isNotBlank() },
                        organization = json.optString("org").takeIf { it.isNotBlank() },
                        city = json.optString("city").takeIf { it.isNotBlank() },
                        region = json.optString("regionName").takeIf { it.isNotBlank() },
                        country = json.optString("country").takeIf { it.isNotBlank() }
                    )
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Get network info with ISP lookup (suspend function for async ISP fetch)
     */
    suspend fun getNetworkInfoWithIsp(): NetworkInfo {
        val basicInfo = getNetworkInfo()
        
        // Only fetch ISP if connected
        return if (basicInfo.isConnected) {
            val ispInfo = fetchIspInfo()
            basicInfo.copy(
                publicIpAddress = ispInfo.publicIp,
                ispName = ispInfo.ispName
            )
        } else {
            basicInfo
        }
    }
}
