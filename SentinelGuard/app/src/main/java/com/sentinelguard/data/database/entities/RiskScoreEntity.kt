package com.sentinelguard.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Risk level thresholds.
 */
enum class RiskLevel {
    /** Score 0-39: Normal operation */
    NORMAL,
    
    /** Score 40-69: Warning, show indicator */
    WARNING,
    
    /** Score 70-89: App lock triggered */
    HIGH,
    
    /** Score 90-100: Critical, lock + alert sent */
    CRITICAL
}

/**
 * Risk score calculation result.
 * 
 * Captures a point-in-time risk assessment with all contributing signals.
 */
@Entity(tableName = "risk_scores")
data class RiskScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /** Total calculated risk score (0-100+) */
    val totalScore: Int,
    
    /** Derived risk level based on thresholds */
    val riskLevel: RiskLevel,
    
    /** JSON map of signal types to their individual contributions */
    val signalContributions: String,
    
    /** Whether this triggered a response action */
    val triggeredAction: Boolean = false,
    
    /** Description of what triggered the score */
    val triggerReason: String? = null,
    
    /** Timestamp of calculation */
    val timestamp: Long = System.currentTimeMillis(),
    
    /** Has the score decayed since calculation */
    val decayed: Boolean = false,
    
    /** Current decayed score (may be lower than totalScore) */
    val currentScore: Int? = null
)
