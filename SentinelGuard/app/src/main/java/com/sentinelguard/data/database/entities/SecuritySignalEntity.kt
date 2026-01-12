package com.sentinelguard.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Security signal types that can be detected.
 */
enum class SignalType {
    // App usage signals
    APP_OPEN,
    APP_CLOSE,
    APP_SESSION,
    
    // Authentication - Detailed
    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    BIOMETRIC_SUCCESS,
    BIOMETRIC_FAILED,
    PIN_FAILED,
    
    // Device state signals
    DEVICE_BOOT,
    SCREEN_UNLOCK,
    SCREEN_ON,
    SCREEN_OFF,
    
    // Network signals
    NETWORK_CHANGE,
    NETWORK_WIFI,
    NETWORK_MOBILE,
    NETWORK_NONE,
    NETWORK_SIM_SWITCH,
    
    // Cell Tower
    CELL_TOWER_CHANGE,
    
    // SIM signals
    SIM_PRESENT,
    SIM_REMOVED,
    SIM_CHANGED,
    
    // Environment signals
    TIMEZONE_CHANGE,
    LOCALE_CHANGE,
    
    // Security threat signals
    EMULATOR_DETECTED,
    ROOT_DETECTED,
    SCREEN_RECORDING_DETECTED,
    DEBUGGER_DETECTED,
    
    // Location signals
    LOCATION_UPDATE,
    LOCATION_ANOMALY,
    
    // Security scans
    SECURITY_SCAN_COMPLETE,
    SECURITY_SCAN_THREAT
}

/**
 * Raw security signal captured from the device.
 * 
 * Each signal represents a single security-relevant event.
 * These are aggregated to build behavioral baselines and calculate risk scores.
 */
@Entity(tableName = "security_signals")
data class SecuritySignalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /** Type of security signal */
    val signalType: SignalType,
    
    /** Optional value associated with the signal (JSON for complex data) */
    val value: String? = null,
    
    /** When the signal was captured */
    val timestamp: Long = System.currentTimeMillis(),
    
    /** Optional metadata as JSON */
    val metadata: String? = null,
    
    /** Whether this signal was processed for baseline/risk */
    val processed: Boolean = false
)
