package com.sentinelguard.data.local.database.converter

import androidx.room.TypeConverter
import com.sentinelguard.domain.model.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * Room Type Converters
 * 
 * Converts complex types to/from database-storable formats.
 * All enums stored as strings for readability and future compatibility.
 */
class Converters {

    // ============ Signal Type ============
    
    @TypeConverter
    fun fromSignalType(type: SignalType): String = type.name
    
    @TypeConverter
    fun toSignalType(value: String): SignalType = SignalType.valueOf(value)
    
    // ============ Baseline Metric Type ============
    
    @TypeConverter
    fun fromBaselineMetricType(type: BaselineMetricType): String = type.name
    
    @TypeConverter
    fun toBaselineMetricType(value: String): BaselineMetricType = BaselineMetricType.valueOf(value)
    
    // ============ Risk Level ============
    
    @TypeConverter
    fun fromRiskLevel(level: RiskLevel): String = level.name
    
    @TypeConverter
    fun toRiskLevel(value: String): RiskLevel = RiskLevel.valueOf(value)
    
    // ============ Incident Severity ============
    
    @TypeConverter
    fun fromIncidentSeverity(severity: IncidentSeverity): String = severity.name
    
    @TypeConverter
    fun toIncidentSeverity(value: String): IncidentSeverity = IncidentSeverity.valueOf(value)
    
    // ============ Response Action ============
    
    @TypeConverter
    fun fromResponseAction(action: ResponseAction): String = action.name
    
    @TypeConverter
    fun toResponseAction(value: String): ResponseAction = ResponseAction.valueOf(value)
    
    // ============ Alert Status ============
    
    @TypeConverter
    fun fromAlertStatus(status: AlertStatus): String = status.name
    
    @TypeConverter
    fun toAlertStatus(value: String): AlertStatus = AlertStatus.valueOf(value)
    
    // ============ List<SignalType> ============
    
    @TypeConverter
    fun fromSignalTypeList(list: List<SignalType>): String {
        return JSONArray(list.map { it.name }).toString()
    }
    
    @TypeConverter
    fun toSignalTypeList(value: String): List<SignalType> {
        val array = JSONArray(value)
        return (0 until array.length()).map { SignalType.valueOf(array.getString(it)) }
    }
    
    // ============ List<ResponseAction> ============
    
    @TypeConverter
    fun fromResponseActionList(list: List<ResponseAction>): String {
        return JSONArray(list.map { it.name }).toString()
    }
    
    @TypeConverter
    fun toResponseActionList(value: String): List<ResponseAction> {
        val array = JSONArray(value)
        return (0 until array.length()).map { ResponseAction.valueOf(array.getString(it)) }
    }
    
    // ============ Map<SignalType, Int> ============
    
    @TypeConverter
    fun fromContributionsMap(map: Map<SignalType, Int>): String {
        val json = JSONObject()
        map.forEach { (type, score) -> json.put(type.name, score) }
        return json.toString()
    }
    
    @TypeConverter
    fun toContributionsMap(value: String): Map<SignalType, Int> {
        val json = JSONObject(value)
        val result = mutableMapOf<SignalType, Int>()
        json.keys().forEach { key ->
            result[SignalType.valueOf(key)] = json.getInt(key)
        }
        return result
    }
    
    // ============ LocationData ============
    
    @TypeConverter
    fun fromLocationData(location: LocationData?): String? {
        return location?.let {
            JSONObject().apply {
                put("lat", it.latitude)
                put("lng", it.longitude)
                put("accuracy", it.accuracy)
            }.toString()
        }
    }
    
    @TypeConverter
    fun toLocationData(value: String?): LocationData? {
        return value?.let {
            val json = JSONObject(it)
            LocationData(
                latitude = json.getDouble("lat"),
                longitude = json.getDouble("lng"),
                accuracy = json.getDouble("accuracy").toFloat()
            )
        }
    }
}
