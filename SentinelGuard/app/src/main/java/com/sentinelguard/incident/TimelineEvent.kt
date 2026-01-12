package com.sentinelguard.incident

import com.sentinelguard.domain.model.*

/**
 * TimelineEvent: Unified Event for Forensic Timeline
 * 
 * WHY THIS EXISTS:
 * Provides a single view combining signals, incidents, and sessions.
 * Makes it easy to display a chronological security history.
 */
data class TimelineEvent(
    val id: String,
    val type: TimelineEventType,
    val title: String,
    val description: String,
    val severity: EventSeverity,
    val timestamp: Long,
    val metadata: Map<String, String> = emptyMap()
)

enum class TimelineEventType {
    SIGNAL,         // Raw security signal
    INCIDENT,       // Responded incident
    SESSION_START,  // Login
    SESSION_END,    // Logout
    RISK_CHANGE,    // Risk level changed
    ACTION_TAKEN    // Response action executed
}

enum class EventSeverity {
    INFO,       // Normal activity
    LOW,        // Minor signal
    MEDIUM,     // Notable event
    HIGH,       // Concerning
    CRITICAL    // Major threat
}

/**
 * Extension to convert SecuritySignal to TimelineEvent.
 */
fun SecuritySignal.toTimelineEvent(): TimelineEvent {
    return TimelineEvent(
        id = id,
        type = TimelineEventType.SIGNAL,
        title = formatSignalTitle(type),
        description = formatSignalDescription(type, value),
        severity = getSignalSeverity(type),
        timestamp = timestamp,
        metadata = buildMap {
            value?.let { put("value", it) }
            metadata?.let { put("metadata", it) }
        }
    )
}

/**
 * Extension to convert Incident to TimelineEvent.
 */
fun Incident.toTimelineEvent(): TimelineEvent {
    return TimelineEvent(
        id = id,
        type = TimelineEventType.INCIDENT,
        title = "Security Incident: ${severity.name}",
        description = summary,
        severity = incidentToEventSeverity(severity),
        timestamp = timestamp,
        metadata = buildMap {
            put("riskScore", riskScore.toString())
            put("actions", actionsTaken.joinToString(", ") { it.name })
        }
    )
}

private fun formatSignalTitle(type: SignalType): String {
    return when (type) {
        // App lifecycle
        SignalType.APP_OPEN -> "App Opened"
        SignalType.APP_CLOSE -> "App Closed"
        SignalType.APP_SESSION -> "Session Ended"
        
        // Authentication - Detailed
        SignalType.LOGIN_SUCCESS -> "🔓 Login Successful"
        SignalType.LOGIN_FAILURE -> "🔐 Login Failed"
        SignalType.BIOMETRIC_SUCCESS -> "👆 Biometric Verified"
        SignalType.BIOMETRIC_FAILED -> "👆 Biometric Failed"
        SignalType.PIN_FAILED -> "🔐 PIN Entry Failed"
        
        // Device state
        SignalType.DEVICE_BOOT -> "📱 Device Rebooted"
        SignalType.SCREEN_ON -> "Screen Unlocked"
        SignalType.SCREEN_OFF -> "Screen Locked"
        
        // Network
        SignalType.NETWORK_WIFI -> "📶 WiFi Connected"
        SignalType.NETWORK_MOBILE -> "📶 Mobile Data"
        SignalType.NETWORK_NONE -> "📵 No Network"
        SignalType.NETWORK_CHANGE -> "📶 Network Changed"
        SignalType.NETWORK_SIM_SWITCH -> "📱 Carrier Switch"
        
        // Cell Tower
        SignalType.CELL_TOWER_CHANGE -> "📡 Tower Changed"
        
        // SIM
        SignalType.SIM_PRESENT -> "SIM Detected"
        SignalType.SIM_REMOVED -> "⚠️ SIM Removed"
        SignalType.SIM_CHANGED -> "⚠️ SIM Changed"
        
        // Location
        SignalType.LOCATION_UPDATE -> "📍 Location Captured"
        SignalType.LOCATION_ANOMALY -> "⚠️ Unknown Location"
        
        // Environment
        SignalType.TIMEZONE_CHANGE -> "🌍 Timezone Changed"
        SignalType.LOCALE_CHANGE -> "🌐 Locale Changed"
        
        // Security threats
        SignalType.ROOT_DETECTED -> "🚨 Root Detected"
        SignalType.EMULATOR_DETECTED -> "🚨 Emulator Detected"
        SignalType.DEBUGGER_DETECTED -> "🚨 Debugger Attached"
        SignalType.SCREEN_RECORDING_DETECTED -> "🎥 Screen Recording"
        
        // Security scans
        SignalType.SECURITY_SCAN_COMPLETE -> "🔍 Scan Complete"
        SignalType.SECURITY_SCAN_THREAT -> "🚨 Threat Detected"
    }
}

private fun formatSignalDescription(type: SignalType, value: String?): String {
    // Value often contains detailed context passed during logging
    return when (type) {
        // Authentication
        SignalType.LOGIN_SUCCESS -> value ?: "User authenticated successfully"
        SignalType.LOGIN_FAILURE -> value ?: "Failed login attempt"
        SignalType.BIOMETRIC_SUCCESS -> value ?: "Fingerprint/Face verified"
        SignalType.BIOMETRIC_FAILED -> value ?: "Biometric not recognized"
        SignalType.PIN_FAILED -> value ?: "Wrong PIN entered"
        
        // Network
        SignalType.NETWORK_CHANGE -> value ?: "Network connection type changed"
        SignalType.NETWORK_WIFI -> value ?: "Connected to WiFi network"
        SignalType.NETWORK_MOBILE -> value ?: "Connected to mobile data"
        SignalType.NETWORK_NONE -> "Device disconnected from all networks"
        SignalType.NETWORK_SIM_SWITCH -> value ?: "Switched between SIM cards"
        
        // Cell Tower
        SignalType.CELL_TOWER_CHANGE -> value ?: "Connected to different cell tower"
        
        // SIM
        SignalType.SIM_REMOVED -> "SIM card was removed from device"
        SignalType.SIM_CHANGED -> value ?: "Different SIM card detected"
        SignalType.SIM_PRESENT -> value ?: "SIM card detected"
        
        // Device
        SignalType.DEVICE_BOOT -> "Device was restarted"
        SignalType.SCREEN_ON -> "Screen was unlocked"
        SignalType.SCREEN_OFF -> "Screen was locked"
        
        // Location
        SignalType.LOCATION_UPDATE -> value ?: "Location recorded"
        SignalType.LOCATION_ANOMALY -> "App opened from unknown location"
        
        // Security threats
        SignalType.ROOT_DETECTED -> "Device appears to be rooted - security risk"
        SignalType.EMULATOR_DETECTED -> "Running in emulator environment"
        SignalType.DEBUGGER_DETECTED -> "Debugger attached to app - potential tampering"
        SignalType.SCREEN_RECORDING_DETECTED -> "Screen recording active - sensitive data may be captured"
        
        // Scans
        SignalType.SECURITY_SCAN_COMPLETE -> value ?: "Security scan finished - no threats found"
        SignalType.SECURITY_SCAN_THREAT -> value ?: "Security scan detected threats"
        
        // Default
        else -> value ?: type.name.replace("_", " ").lowercase()
    }
}

private fun getSignalSeverity(type: SignalType): EventSeverity {
    return when (type) {
        // Critical - Major security issues
        SignalType.ROOT_DETECTED,
        SignalType.EMULATOR_DETECTED,
        SignalType.SECURITY_SCAN_THREAT -> EventSeverity.CRITICAL
        
        // High - Significant security concerns
        SignalType.SIM_REMOVED,
        SignalType.SIM_CHANGED,
        SignalType.DEBUGGER_DETECTED -> EventSeverity.HIGH
        
        // Medium - Notable events requiring attention
        SignalType.LOGIN_FAILURE,
        SignalType.PIN_FAILED,
        SignalType.BIOMETRIC_FAILED,
        SignalType.LOCATION_ANOMALY,
        SignalType.SCREEN_RECORDING_DETECTED -> EventSeverity.MEDIUM
        
        // Low - Informational but noteworthy
        SignalType.DEVICE_BOOT,
        SignalType.NETWORK_CHANGE,
        SignalType.NETWORK_SIM_SWITCH,
        SignalType.CELL_TOWER_CHANGE,
        SignalType.SECURITY_SCAN_COMPLETE -> EventSeverity.LOW
        
        // Info - Normal activity
        SignalType.LOGIN_SUCCESS,
        SignalType.BIOMETRIC_SUCCESS -> EventSeverity.INFO
        
        else -> EventSeverity.INFO
    }
}

private fun incidentToEventSeverity(severity: IncidentSeverity): EventSeverity {
    return when (severity) {
        IncidentSeverity.INFO -> EventSeverity.INFO
        IncidentSeverity.WARNING -> EventSeverity.MEDIUM
        IncidentSeverity.HIGH -> EventSeverity.HIGH
        IncidentSeverity.CRITICAL -> EventSeverity.CRITICAL
    }
}
