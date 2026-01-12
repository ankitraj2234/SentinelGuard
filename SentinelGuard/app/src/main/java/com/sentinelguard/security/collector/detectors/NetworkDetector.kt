package com.sentinelguard.security.collector.detectors

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import org.json.JSONObject

/**
 * Network connection types.
 */
enum class NetworkType {
    WIFI,
    MOBILE,
    NONE
}

/**
 * Current network state.
 */
data class NetworkState(
    val type: NetworkType,
    val isConnected: Boolean,
    val isVpn: Boolean = false,
    val networkName: String? = null
) {
    fun toJson(): String = JSONObject().apply {
        put("type", type.name)
        put("connected", isConnected)
        put("vpn", isVpn)
        put("name", networkName)
    }.toString()
}

/**
 * Detects network state and changes.
 * 
 * Monitors:
 * - Wi-Fi / Mobile data transitions
 * - VPN connections
 * - Network availability
 */
class NetworkDetector(private val context: Context) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) 
        as ConnectivityManager

    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var lastNetworkState: NetworkState? = null

    /**
     * Gets the current network state.
     */
    fun getCurrentNetworkState(): NetworkState {
        val network = connectivityManager.activeNetwork
        val capabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }

        val type = when {
            capabilities == null -> NetworkType.NONE
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.MOBILE
            else -> NetworkType.NONE
        }

        val isConnected = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        val isVpn = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true

        return NetworkState(
            type = type,
            isConnected = isConnected,
            isVpn = isVpn
        ).also { lastNetworkState = it }
    }

    /**
     * Checks if network type changed since last check.
     */
    fun hasNetworkChanged(previousType: NetworkType?): Boolean {
        val currentState = getCurrentNetworkState()
        return previousType != null && previousType != currentState.type
    }

    /**
     * Returns true if currently connected to any network.
     */
    fun isConnected(): Boolean {
        return getCurrentNetworkState().isConnected
    }

    /**
     * Registers a callback for network changes.
     */
    fun registerNetworkCallback(onNetworkChange: (NetworkState) -> Unit) {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                onNetworkChange(getCurrentNetworkState())
            }

            override fun onLost(network: Network) {
                onNetworkChange(NetworkState(NetworkType.NONE, false))
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                onNetworkChange(getCurrentNetworkState())
            }
        }

        connectivityManager.registerNetworkCallback(request, networkCallback!!)
    }

    /**
     * Unregisters the network callback.
     */
    fun unregisterNetworkCallback() {
        networkCallback?.let {
            try {
                connectivityManager.unregisterNetworkCallback(it)
            } catch (e: Exception) {
                // Ignore if not registered
            }
            networkCallback = null
        }
    }
}
