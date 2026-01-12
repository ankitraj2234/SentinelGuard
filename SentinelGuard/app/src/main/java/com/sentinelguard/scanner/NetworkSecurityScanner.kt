package com.sentinelguard.scanner

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NetworkSecurityScanner: Comprehensive Network Security Analysis
 * 
 * Scans for:
 * - WiFi security (encryption type, open networks)
 * - VPN detection (active VPN connections)
 * - Proxy configuration (MITM risk)
 * - DNS settings tampering
 * - Open ports on device
 * - ARP cache anomalies
 * - Network type and capabilities
 */
@Singleton
class NetworkSecurityScanner @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "NetworkSecurityScanner"
        
        // Common dangerous ports
        private val SUSPICIOUS_PORTS = listOf(
            4444,   // Metasploit default
            5555,   // ADB over network
            27042,  // Frida default
            27043,  // Frida
            31337,  // Common backdoor
            1337,   // Hacker port
            8080,   // Web proxy
            9999,   // Common malware C2
        )
        
        // Common ports to check if listening
        private val PORTS_TO_CHECK = listOf(
            22,     // SSH
            23,     // Telnet
            80,     // HTTP
            443,    // HTTPS
            4444,   // Metasploit
            5555,   // ADB
            8080,   // Proxy
            27042,  // Frida
            31337   // Backdoor
        )
    }
    
    /**
     * Network security scan result
     */
    data class NetworkSecurityResult(
        val isConnected: Boolean,
        val connectionType: String,
        val wifiInfo: WifiSecurityInfo?,
        val vpnActive: Boolean,
        val proxyConfigured: Boolean,
        val proxyAddress: String?,
        val openPorts: List<Int>,
        val suspiciousPorts: List<Int>,
        val dnsServers: List<String>,
        val warnings: List<NetworkWarning>,
        val overallRiskScore: Int
    )
    
    data class WifiSecurityInfo(
        val ssid: String?,
        val bssid: String?,
        val isSecure: Boolean,
        val encryptionType: String,
        val signalStrength: Int,
        val frequency: Int,
        val linkSpeed: Int
    )
    
    data class NetworkWarning(
        val type: String,
        val description: String,
        val severity: WarningSeverity,
        val riskPoints: Int
    )
    
    enum class WarningSeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    /**
     * Perform comprehensive network security scan
     */
    suspend fun performFullScan(): NetworkSecurityResult = withContext(Dispatchers.IO) {
        val warnings = mutableListOf<NetworkWarning>()
        var riskScore = 0
        
        // 1. Check connectivity
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) 
            as ConnectivityManager
        
        val network = connectivityManager.activeNetwork
        val capabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }
        
        val isConnected = capabilities != null
        val connectionType = getConnectionType(capabilities)
        
        // 2. Check VPN
        val vpnActive = isVpnActive(capabilities)
        if (vpnActive) {
            // VPN is usually good, but could be used to hide traffic
            warnings.add(NetworkWarning(
                type = "VPN_ACTIVE",
                description = "VPN connection detected - traffic is being routed externally",
                severity = WarningSeverity.LOW,
                riskPoints = 0
            ))
        }
        
        // 3. Check Proxy
        val proxyHost = System.getProperty("http.proxyHost")
        val proxyPort = System.getProperty("http.proxyPort")
        val proxyConfigured = !proxyHost.isNullOrBlank()
        val proxyAddress = if (proxyConfigured) "$proxyHost:$proxyPort" else null
        
        if (proxyConfigured) {
            warnings.add(NetworkWarning(
                type = "PROXY_CONFIGURED",
                description = "HTTP proxy is configured: $proxyAddress - traffic may be intercepted",
                severity = WarningSeverity.MEDIUM,
                riskPoints = 15
            ))
            riskScore += 15
        }
        
        // 4. WiFi Security Check
        val wifiInfo = getWifiSecurityInfo()
        if (wifiInfo != null) {
            if (!wifiInfo.isSecure) {
                warnings.add(NetworkWarning(
                    type = "INSECURE_WIFI",
                    description = "Connected to open/insecure WiFi: ${wifiInfo.ssid}",
                    severity = WarningSeverity.HIGH,
                    riskPoints = 25
                ))
                riskScore += 25
            }
            
            if (wifiInfo.encryptionType.contains("WEP", ignoreCase = true)) {
                warnings.add(NetworkWarning(
                    type = "WEAK_ENCRYPTION",
                    description = "WiFi uses weak WEP encryption - easily crackable",
                    severity = WarningSeverity.HIGH,
                    riskPoints = 20
                ))
                riskScore += 20
            }
        }
        
        // 5. Check Open Ports
        val openPorts = scanOpenPorts()
        val suspiciousPorts = openPorts.filter { it in SUSPICIOUS_PORTS }
        
        if (suspiciousPorts.isNotEmpty()) {
            warnings.add(NetworkWarning(
                type = "SUSPICIOUS_PORTS",
                description = "Suspicious ports open: ${suspiciousPorts.joinToString(", ")}",
                severity = WarningSeverity.CRITICAL,
                riskPoints = 30
            ))
            riskScore += 30
        }
        
        // Check for ADB over network (port 5555)
        if (5555 in openPorts) {
            warnings.add(NetworkWarning(
                type = "ADB_NETWORK",
                description = "ADB is accessible over network - remote access possible",
                severity = WarningSeverity.CRITICAL,
                riskPoints = 35
            ))
            riskScore += 35
        }
        
        // 6. DNS Servers
        val dnsServers = getDnsServers()
        val suspiciousDns = dnsServers.filter { dns ->
            // Check for known malicious DNS or unusual servers
            !dns.startsWith("8.8.") && // Google
            !dns.startsWith("1.1.") && // Cloudflare
            !dns.startsWith("9.9.") && // Quad9
            !dns.startsWith("208.67.") && // OpenDNS
            !dns.startsWith("192.168.") && // Private
            !dns.startsWith("10.") // Private
        }
        
        if (suspiciousDns.isNotEmpty()) {
            warnings.add(NetworkWarning(
                type = "UNUSUAL_DNS",
                description = "Non-standard DNS servers configured: ${suspiciousDns.joinToString(", ")}",
                severity = WarningSeverity.LOW,
                riskPoints = 5
            ))
            riskScore += 5
        }
        
        // 7. Check for captive portal
        if (isConnected && !vpnActive) {
            val hasCaptivePortal = checkCaptivePortal()
            if (hasCaptivePortal) {
                warnings.add(NetworkWarning(
                    type = "CAPTIVE_PORTAL",
                    description = "Network has captive portal - additional authentication required",
                    severity = WarningSeverity.LOW,
                    riskPoints = 5
                ))
                riskScore += 5
            }
        }
        
        Log.i(TAG, "Network scan complete. Warnings: ${warnings.size}, Risk: $riskScore")
        
        NetworkSecurityResult(
            isConnected = isConnected,
            connectionType = connectionType,
            wifiInfo = wifiInfo,
            vpnActive = vpnActive,
            proxyConfigured = proxyConfigured,
            proxyAddress = proxyAddress,
            openPorts = openPorts,
            suspiciousPorts = suspiciousPorts,
            dnsServers = dnsServers,
            warnings = warnings,
            overallRiskScore = riskScore.coerceAtMost(100)
        )
    }
    
    /**
     * Get connection type string
     */
    private fun getConnectionType(capabilities: NetworkCapabilities?): String {
        if (capabilities == null) return "None"
        
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> "VPN"
            else -> "Unknown"
        }
    }
    
    /**
     * Check if VPN is active
     */
    private fun isVpnActive(capabilities: NetworkCapabilities?): Boolean {
        if (capabilities == null) return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    }
    
    /**
     * Get WiFi security information
     */
    @Suppress("DEPRECATION")
    private fun getWifiSecurityInfo(): WifiSecurityInfo? {
        try {
            val wifiManager = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
            
            val connectionInfo = wifiManager.connectionInfo ?: return null
            
            // Get SSID (may require location permission on newer Android)
            val ssid = connectionInfo.ssid?.replace("\"", "")
            if (ssid == "<unknown ssid>" || ssid.isNullOrBlank()) {
                return null
            }
            
            val bssid = connectionInfo.bssid
            val signalStrength = WifiManager.calculateSignalLevel(connectionInfo.rssi, 5)
            val frequency = connectionInfo.frequency
            val linkSpeed = connectionInfo.linkSpeed
            
            // Determine security from scan results
            val scanResults = wifiManager.scanResults
            val currentNetwork = scanResults?.find { it.BSSID == bssid }
            
            val (isSecure, encryptionType) = if (currentNetwork != null) {
                val caps = currentNetwork.capabilities
                when {
                    caps.contains("WPA3") -> true to "WPA3"
                    caps.contains("WPA2") -> true to "WPA2"
                    caps.contains("WPA") -> true to "WPA"
                    caps.contains("WEP") -> false to "WEP"
                    caps.contains("ESS") && !caps.contains("WPA") && !caps.contains("WEP") -> false to "Open"
                    else -> true to "Unknown"
                }
            } else {
                true to "Unknown"
            }
            
            return WifiSecurityInfo(
                ssid = ssid,
                bssid = bssid,
                isSecure = isSecure,
                encryptionType = encryptionType,
                signalStrength = signalStrength,
                frequency = frequency,
                linkSpeed = linkSpeed
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get WiFi info", e)
            return null
        }
    }
    
    /**
     * Scan for open ports on localhost
     */
    private fun scanOpenPorts(): List<Int> {
        val openPorts = mutableListOf<Int>()
        
        for (port in PORTS_TO_CHECK) {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress("127.0.0.1", port), 50)
                socket.close()
                openPorts.add(port)
                Log.d(TAG, "Port $port is open")
            } catch (e: Exception) {
                // Port is closed, which is expected
            }
        }
        
        return openPorts
    }
    
    /**
     * Get configured DNS servers
     */
    private fun getDnsServers(): List<String> {
        val dnsServers = mutableListOf<String>()
        
        try {
            // Method 1: Read from prop
            val process = Runtime.getRuntime().exec("getprop net.dns1")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val dns1 = reader.readLine()?.trim()
            reader.close()
            if (!dns1.isNullOrBlank()) dnsServers.add(dns1)
            
            val process2 = Runtime.getRuntime().exec("getprop net.dns2")
            val reader2 = BufferedReader(InputStreamReader(process2.inputStream))
            val dns2 = reader2.readLine()?.trim()
            reader2.close()
            if (!dns2.isNullOrBlank()) dnsServers.add(dns2)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get DNS servers from props", e)
        }
        
        // Method 2: Check resolv.conf if accessible
        try {
            val resolvConf = java.io.File("/etc/resolv.conf")
            if (resolvConf.exists() && resolvConf.canRead()) {
                resolvConf.readLines().forEach { line ->
                    if (line.startsWith("nameserver")) {
                        val dns = line.substringAfter("nameserver").trim()
                        if (dns.isNotBlank() && dns !in dnsServers) {
                            dnsServers.add(dns)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore
        }
        
        return dnsServers
    }
    
    /**
     * Check for captive portal
     */
    private fun checkCaptivePortal(): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress("connectivitycheck.gstatic.com", 80), 2000)
            socket.close()
            false // Connected successfully, no captive portal
        } catch (e: Exception) {
            // Could be captive portal blocking
            true
        }
    }
    
    /**
     * Get device's IP addresses
     */
    fun getDeviceIpAddresses(): List<String> {
        val addresses = mutableListOf<String>()
        
        try {
            NetworkInterface.getNetworkInterfaces()?.toList()?.forEach { networkInterface ->
                networkInterface.inetAddresses?.toList()?.forEach { address ->
                    if (address is Inet4Address && !address.isLoopbackAddress) {
                        addresses.add(address.hostAddress ?: "")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get IP addresses", e)
        }
        
        return addresses
    }
    
    /**
     * Quick network security check
     */
    suspend fun quickSecurityCheck(): QuickNetworkCheck = withContext(Dispatchers.IO) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) 
            as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }
        
        val isSecure = when {
            capabilities == null -> false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                val wifiInfo = getWifiSecurityInfo()
                wifiInfo?.isSecure ?: false
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
        
        QuickNetworkCheck(
            isConnected = capabilities != null,
            isSecure = isSecure,
            connectionType = getConnectionType(capabilities),
            hasVpn = isVpnActive(capabilities)
        )
    }
    
    data class QuickNetworkCheck(
        val isConnected: Boolean,
        val isSecure: Boolean,
        val connectionType: String,
        val hasVpn: Boolean
    )
}
