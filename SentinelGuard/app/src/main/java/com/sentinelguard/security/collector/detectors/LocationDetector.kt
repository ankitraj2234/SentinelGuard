package com.sentinelguard.security.collector.detectors

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Location data.
 */
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val timestamp: Long
) {
    fun toJson(): String = JSONObject().apply {
        put("lat", latitude)
        put("lng", longitude)
        put("accuracy", accuracy)
        put("timestamp", timestamp)
    }.toString()
    
    companion object {
        fun fromJson(json: String): LocationData? {
            return try {
                val obj = JSONObject(json)
                LocationData(
                    latitude = obj.getDouble("lat"),
                    longitude = obj.getDouble("lng"),
                    accuracy = obj.getDouble("accuracy").toFloat(),
                    timestamp = obj.getLong("timestamp")
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

/**
 * Location detector.
 * 
 * CRITICAL PRIVACY CONSTRAINTS:
 * - Location is ONLY collected when the app opens (foreground)
 * - NO background location tracking
 * - NO continuous GPS monitoring
 * - NO WorkManager/Service for location
 * 
 * This is a single-shot location fetch that happens only when
 * the user actively opens the app.
 */
class LocationDetector(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) {

    companion object {
        private const val LOCATION_TIMEOUT_MS = 5000L
    }

    /**
     * Gets current location if permission is granted.
     * Returns null if permission denied or location unavailable.
     * 
     * This is a ONE-SHOT request, not continuous tracking.
     */
    suspend fun getCurrentLocation(): LocationData? {
        // Check permission
        if (!hasLocationPermission()) {
            return null
        }

        return withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
            try {
                getLocation()
            } catch (e: Exception) {
                null
            }
        }
    }

    private suspend fun getLocation(): LocationData? = suspendCancellableCoroutine { continuation ->
        try {
            val cancellationTokenSource = CancellationTokenSource()
            
            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    continuation.resume(
                        LocationData(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            accuracy = location.accuracy,
                            timestamp = location.time
                        )
                    )
                } else {
                    // Try last known location as fallback
                    getLastLocation(continuation)
                }
            }.addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
        } catch (e: SecurityException) {
            continuation.resume(null)
        }
    }

    private fun getLastLocation(continuation: kotlin.coroutines.Continuation<LocationData?>) {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    (continuation as kotlin.coroutines.Continuation<LocationData?>).resume(
                        LocationData(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            accuracy = location.accuracy,
                            timestamp = location.time
                        )
                    )
                } else {
                    (continuation as kotlin.coroutines.Continuation<LocationData?>).resume(null)
                }
            }.addOnFailureListener {
                (continuation as kotlin.coroutines.Continuation<LocationData?>).resume(null)
            }
        } catch (e: SecurityException) {
            (continuation as kotlin.coroutines.Continuation<LocationData?>).resume(null)
        }
    }

    /**
     * Checks if location permission is granted.
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Calculates distance between two locations in meters.
     */
    fun calculateDistance(loc1: LocationData, loc2: LocationData): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            loc1.latitude, loc1.longitude,
            loc2.latitude, loc2.longitude,
            results
        )
        return results[0]
    }
}
