package com.sentinelguard.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sentinelguard.network.ConnectionType
import com.sentinelguard.network.WifiFrequency
import com.sentinelguard.ui.components.ElevatedGlassCard
import com.sentinelguard.ui.theme.*
import com.sentinelguard.ui.viewmodels.NetworkInfoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkInfoScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCellTowerDetails: () -> Unit = {},
    viewModel: NetworkInfoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val networkInfo = uiState.networkInfo

    Box(
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
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            TopAppBar(
                title = { 
                    Text(
                        "Network Information",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshNetworkInfo() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Connection Status Card
                    ConnectionStatusCard(
                        connectionType = networkInfo.connectionType,
                        isConnected = networkInfo.isConnected,
                        isVpnActive = networkInfo.isVpnActive
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // WiFi Info
                    if (networkInfo.connectionType == ConnectionType.WIFI && networkInfo.wifiInfo != null) {
                        val wifi = networkInfo.wifiInfo
                        
                        SectionHeader("WiFi Details")
                        
                        InfoCard {
                            InfoRow(Icons.Default.Wifi, "Network Name", wifi.ssid)
                            InfoRow(Icons.Default.Security, "Encryption", wifi.encryptionType)
                            InfoRow(Icons.Default.Speed, "Frequency", when (wifi.frequency) {
                                WifiFrequency.BAND_2_4_GHZ -> "2.4 GHz"
                                WifiFrequency.BAND_5_GHZ -> "5 GHz"
                                WifiFrequency.BAND_6_GHZ -> "6 GHz (WiFi 6E)"
                                WifiFrequency.UNKNOWN -> "${wifi.frequencyMhz} MHz"
                            })
                            InfoRow(Icons.Default.SignalCellularAlt, "Signal Strength", "${wifi.signalPercent}% (${wifi.signalStrength} dBm)")
                            InfoRow(Icons.Default.NetworkCheck, "Link Speed", "${wifi.linkSpeed} Mbps")
                            wifi.bssid?.let {
                                InfoRow(Icons.Default.Router, "Router (BSSID)", it)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // Mobile Info
                    if (networkInfo.connectionType == ConnectionType.MOBILE && networkInfo.mobileInfo != null) {
                        val mobile = networkInfo.mobileInfo
                        
                        // Use carrier name, fallback to ISP from API if Unknown
                        val carrierDisplay = if (mobile.carrierName.isBlank() || mobile.carrierName == "Unknown") {
                            networkInfo.ispName ?: "Unknown"
                        } else {
                            mobile.carrierName
                        }
                        
                        SectionHeader("Mobile Network Details")
                        
                        InfoCard {
                            InfoRow(Icons.Default.SimCard, "Carrier", carrierDisplay)
                            InfoRow(Icons.Default.NetworkCell, "Network Type", mobile.networkType)
                            InfoRow(Icons.Default.Speed, "Technology", mobile.networkTypeTechnical)
                            InfoRow(Icons.Default.Info, "Data State", mobile.dataState)
                            if (mobile.isRoaming) {
                                InfoRow(Icons.Default.TravelExplore, "Roaming", "Yes")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Cell Tower Info
                        if (mobile.cellId != null || mobile.lac != null) {
                            // Section header with More button
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Cell Tower Info",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Primary
                                )
                                TextButton(
                                    onClick = onNavigateToCellTowerDetails,
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        "More",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Primary
                                    )
                                    Icon(
                                        Icons.Default.ChevronRight,
                                        contentDescription = "View details",
                                        tint = Primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            
                            InfoCard {
                                mobile.cellId?.let {
                                    InfoRow(Icons.Default.CellTower, "Cell ID", it)
                                }
                                mobile.lac?.let {
                                    InfoRow(Icons.Default.LocationOn, "LAC/TAC", it)
                                }
                                mobile.mcc?.let { mcc ->
                                    mobile.mnc?.let { mnc ->
                                        InfoRow(Icons.Default.Public, "MCC/MNC", "$mcc / $mnc")
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        // SIM Info
                        SectionHeader("SIM Information")
                        
                        InfoCard {
                            mobile.phoneNumber?.let {
                                InfoRow(Icons.Default.Phone, "Phone Number", it)
                            }
                            mobile.simOperator?.let {
                                InfoRow(Icons.Default.SimCard, "SIM Operator", it)
                            }
                            mobile.simCountry?.let {
                                InfoRow(Icons.Default.Flag, "SIM Country", it)
                            }
                            mobile.simSlot?.let {
                                InfoRow(Icons.Default.Dialpad, "SIM Slot", "Slot ${it + 1}")
                            }
                            mobile.simSerialNumber?.let {
                                InfoRow(Icons.Default.Numbers, "SIM Serial (ICCID)", it)
                            }
                            mobile.imei?.let {
                                InfoRow(Icons.Default.Smartphone, "IMEI", it)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // IP & Network Configuration
                    if (networkInfo.isConnected) {
                        SectionHeader("IP Configuration")
                        
                        InfoCard {
                            networkInfo.ipAddress?.let {
                                InfoRow(Icons.Default.Language, "Local IP", it)
                            }
                            networkInfo.publicIpAddress?.let {
                                InfoRow(Icons.Default.Public, "Public IP", it)
                            }
                            networkInfo.ispName?.let {
                                InfoRow(Icons.Default.Business, "ISP Provider", it)
                            }
                            // Gateway - show if available, or show carrier gateway for mobile
                            if (networkInfo.gateway != null) {
                                InfoRow(Icons.Default.Hub, "Gateway", networkInfo.gateway)
                            } else if (networkInfo.connectionType == ConnectionType.MOBILE) {
                                InfoRow(Icons.Default.Hub, "Gateway", "Carrier Network")
                            }
                            networkInfo.subnetMask?.let {
                                InfoRow(Icons.Default.GridOn, "Subnet Mask", it)
                            }
                            if (networkInfo.dnsServers.isNotEmpty()) {
                                InfoRow(Icons.Default.Dns, "DNS Servers", networkInfo.dnsServers.joinToString(", "))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // VPN Status
                    if (networkInfo.isVpnActive) {
                        SectionHeader("VPN")
                        
                        InfoCard {
                            InfoRow(Icons.Default.VpnKey, "VPN Status", "Active")
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // No Connection
                    if (networkInfo.connectionType == ConnectionType.NONE) {
                        ElevatedGlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.SignalWifiOff,
                                    contentDescription = null,
                                    tint = StatusDanger,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No Network Connection",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    "Connect to WiFi or mobile data",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun ConnectionStatusCard(
    connectionType: ConnectionType,
    isConnected: Boolean,
    isVpnActive: Boolean
) {
    val (icon, label, color) = when (connectionType) {
        ConnectionType.WIFI -> Triple(Icons.Default.Wifi, "WiFi Connected", StatusSecure)
        ConnectionType.MOBILE -> Triple(Icons.Default.SignalCellular4Bar, "Mobile Data", Primary)
        ConnectionType.ETHERNET -> Triple(Icons.Default.Cable, "Ethernet", StatusSecure)
        ConnectionType.VPN -> Triple(Icons.Default.VpnKey, "VPN Connected", StatusWarning)
        ConnectionType.NONE -> Triple(Icons.Default.SignalWifiOff, "Not Connected", StatusDanger)
    }
    
    ElevatedGlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isConnected) StatusSecure else StatusDanger)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (isConnected) "Online" else "Offline",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        if (isVpnActive && connectionType != ConnectionType.VPN) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.VpnKey,
                                contentDescription = "VPN Active",
                                tint = StatusWarning,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = Primary,
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

@Composable
private fun InfoCard(
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            content = content
        )
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
