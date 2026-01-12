package com.sentinelguard.data.database

import androidx.room.TypeConverter
import com.sentinelguard.data.database.entities.*

/**
 * Room type converters for enum types.
 */
class Converters {

    // SignalType
    @TypeConverter
    fun fromSignalType(value: SignalType): String = value.name

    @TypeConverter
    fun toSignalType(value: String): SignalType = SignalType.valueOf(value)

    // BaselineMetricType
    @TypeConverter
    fun fromBaselineMetricType(value: BaselineMetricType): String = value.name

    @TypeConverter
    fun toBaselineMetricType(value: String): BaselineMetricType = BaselineMetricType.valueOf(value)

    // RiskLevel
    @TypeConverter
    fun fromRiskLevel(value: RiskLevel): String = value.name

    @TypeConverter
    fun toRiskLevel(value: String): RiskLevel = RiskLevel.valueOf(value)

    // IncidentSeverity
    @TypeConverter
    fun fromIncidentSeverity(value: IncidentSeverity): String = value.name

    @TypeConverter
    fun toIncidentSeverity(value: String): IncidentSeverity = IncidentSeverity.valueOf(value)

    // AlertStatus
    @TypeConverter
    fun fromAlertStatus(value: AlertStatus): String = value.name

    @TypeConverter
    fun toAlertStatus(value: String): AlertStatus = AlertStatus.valueOf(value)
}
