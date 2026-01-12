package com.sentinelguard.security.risk

import com.sentinelguard.data.database.dao.BehavioralAnomalyDao
import com.sentinelguard.domain.model.RiskLevel
import com.sentinelguard.domain.model.RiskScore
import com.sentinelguard.domain.model.SecuritySignal
import com.sentinelguard.domain.model.SignalType
import com.sentinelguard.domain.repository.RiskScoreRepository
import com.sentinelguard.domain.repository.SecuritySignalRepository
import com.sentinelguard.domain.util.SecureIdGenerator
import com.sentinelguard.security.baseline.AnomalyResult
import com.sentinelguard.security.baseline.AnomalyType
import com.sentinelguard.security.baseline.AppUsagePatternAnalyzer
import com.sentinelguard.security.baseline.BaselineEngine
import com.sentinelguard.security.baseline.LocationClusterManager
import com.sentinelguard.security.baseline.UnlockPatternAnalyzer
import com.sentinelguard.security.collector.NetworkBehaviorTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * RiskScoringEngine: Calculates and Manages Risk Scores
 * 
 * WHY THIS EXISTS:
 * Converts raw signals and anomalies into actionable risk levels.
 * Uses weighted scoring with compound scenario detection.
 * 
 * SCORING:
 * - Each signal type has a base weight
 * - Behavioral anomalies add points
 * - Compound scenarios (multiple related signals) add bonus
 * - Score decays 10% per hour without new signals
 * 
 * THRESHOLDS:
 * - NORMAL: 0-39 (no action)
 * - WARNING: 40-69 (UI indicator)
 * - HIGH: 70-89 (app lock)
 * - CRITICAL: 90+ (lock + email alert)
 */
@Singleton
class RiskScoringEngine @Inject constructor(
    private val riskScoreRepository: RiskScoreRepository,
    private val signalRepository: SecuritySignalRepository,
    private val baselineEngine: BaselineEngine,
    private val anomalyDao: BehavioralAnomalyDao,
    private val appUsageAnalyzer: AppUsagePatternAnalyzer,
    private val locationClusterManager: LocationClusterManager,
    private val networkTracker: NetworkBehaviorTracker,
    private val unlockAnalyzer: UnlockPatternAnalyzer
) {
    companion object {
        // ============ Signal Weights ============
        private val SIGNAL_WEIGHTS = mapOf(
            SignalType.ROOT_DETECTED to 50,
            SignalType.EMULATOR_DETECTED to 50,
            SignalType.DEBUGGER_DETECTED to 45,
            SignalType.SIM_REMOVED to 40,
            SignalType.SIM_CHANGED to 35,
            SignalType.SCREEN_RECORDING_DETECTED to 30,
            SignalType.LOCATION_ANOMALY to 30,
            SignalType.LOGIN_FAILURE to 15,
            SignalType.DEVICE_BOOT to 10,
            SignalType.NETWORK_CHANGE to 10,
            SignalType.TIMEZONE_CHANGE to 10,
            SignalType.LOCALE_CHANGE to 10
        )

        // ============ Thresholds ============
        const val THRESHOLD_WARNING = 40
        const val THRESHOLD_HIGH = 70
        const val THRESHOLD_CRITICAL = 90

        // ============ Decay ============
        const val DECAY_PERCENT_PER_HOUR = 10.0

        // ============ Time Windows ============
        private const val COMPOUND_WINDOW_MS = 30 * 60 * 1000L // 30 minutes
        private const val RECENT_SIGNALS_WINDOW_MS = 60 * 60 * 1000L // 1 hour
    }

    /**
     * Calculates current risk score from all sources:
     * - Security signals
     * - Baseline anomalies
     * - Behavioral pattern anomalies (new!)
     */
    suspend fun calculateRiskScore(): RiskScore = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        
        // Get recent signals (last hour)
        val recentSignals = signalRepository.getInRange(
            now - RECENT_SIGNALS_WINDOW_MS,
            now
        )

        // Get behavioral anomalies from all analyzers
        val behavioralRiskPoints = collectBehavioralRiskPoints()

        // Get baseline anomalies
        val anomalies = baselineEngine.getCurrentAnomalies()

        // Calculate base score from signals
        val contributions = mutableMapOf<SignalType, Int>()
        var totalScore = 0

        for (signal in recentSignals) {
            val weight = SIGNAL_WEIGHTS[signal.type] ?: 0
            if (weight > 0) {
                contributions[signal.type] = (contributions[signal.type] ?: 0) + weight
                totalScore += weight
            }
        }

        // Add anomaly contributions
        for (anomaly in anomalies) {
            val (signalType, score) = anomalyToSignal(anomaly)
            contributions[signalType] = (contributions[signalType] ?: 0) + score
            totalScore += score
        }

        // Add behavioral risk points
        totalScore += behavioralRiskPoints

        // Check for compound scenarios
        val compoundBonus = calculateCompoundBonus(recentSignals, contributions.keys)
        totalScore += compoundBonus.first
        val compoundReasons = compoundBonus.second

        // Apply decay from previous score
        val previousScore = riskScoreRepository.getLatest()
        if (previousScore != null && recentSignals.isEmpty() && behavioralRiskPoints == 0) {
            val decayedScore = applyDecay(previousScore)
            if (decayedScore < totalScore) {
                totalScore = decayedScore
            }
        }

        // Cap at 100
        totalScore = totalScore.coerceIn(0, 100)

        // Determine risk level
        val level = when {
            totalScore >= THRESHOLD_CRITICAL -> RiskLevel.CRITICAL
            totalScore >= THRESHOLD_HIGH -> RiskLevel.HIGH
            totalScore >= THRESHOLD_WARNING -> RiskLevel.WARNING
            else -> RiskLevel.NORMAL
        }

        // Build trigger reason
        val triggerReason = buildTriggerReason(contributions, compoundReasons, behavioralRiskPoints)

        // Create and save score
        val riskScore = RiskScore(
            id = SecureIdGenerator.generateId(),
            totalScore = totalScore,
            level = level,
            contributions = contributions,
            triggerReason = triggerReason,
            decayed = false,
            timestamp = now
        )

        riskScoreRepository.insert(riskScore)
        riskScore
    }

    /**
     * Collect risk points from all behavioral analyzers.
     */
    private suspend fun collectBehavioralRiskPoints(): Int {
        var totalBehavioralRisk = 0

        try {
            // App usage patterns
            totalBehavioralRisk += appUsageAnalyzer.analyzeCurrentUsage()
            
            // Location clusters
            totalBehavioralRisk += locationClusterManager.recordLocation()
            
            // Network behavior
            totalBehavioralRisk += networkTracker.recordNetworkConnection()
            
            // Get unresolved behavioral anomalies from database
            val last6Hours = System.currentTimeMillis() - (6 * 60 * 60 * 1000)
            val unresolvedRisk = anomalyDao.getTotalRiskPoints(last6Hours) ?: 0
            totalBehavioralRisk += unresolvedRisk
            
        } catch (e: Exception) {
            // Log error but don't crash
        }

        return totalBehavioralRisk.coerceAtMost(50) // Cap behavioral at 50 points
    }

    /**
     * Calculates compound scenario bonuses.
     */
    private fun calculateCompoundBonus(
        signals: List<SecuritySignal>,
        activeTypes: Set<SignalType>
    ): Pair<Int, List<String>> {
        var bonus = 0
        val reasons = mutableListOf<String>()

        // Theft Pattern: DEVICE_BOOT + SIM_REMOVED + NETWORK_CHANGE
        if (hasCompound(activeTypes, SignalType.DEVICE_BOOT, SignalType.SIM_REMOVED, SignalType.NETWORK_CHANGE)) {
            bonus += 40
            reasons.add("Theft Pattern Detected")
        }

        // Remote Analysis: EMULATOR + DEBUGGER
        if (hasCompound(activeTypes, SignalType.EMULATOR_DETECTED, SignalType.DEBUGGER_DETECTED)) {
            bonus += 30
            reasons.add("Remote Analysis Environment")
        }

        // Credential Theft: SCREEN_RECORDING + LOGIN_FAILURE
        if (hasCompound(activeTypes, SignalType.SCREEN_RECORDING_DETECTED, SignalType.LOGIN_FAILURE)) {
            bonus += 25
            reasons.add("Credential Capture Attempt")
        }

        // SIM Swap Attack: SIM_CHANGED + NETWORK_CHANGE
        if (hasCompound(activeTypes, SignalType.SIM_CHANGED, SignalType.NETWORK_CHANGE)) {
            bonus += 20
            reasons.add("SIM Swap Detected")
        }

        // Unusual Access: LOCATION_ANOMALY + (ROOT or EMULATOR)
        if (activeTypes.contains(SignalType.LOCATION_ANOMALY) &&
            (activeTypes.contains(SignalType.ROOT_DETECTED) || activeTypes.contains(SignalType.EMULATOR_DETECTED))) {
            bonus += 25
            reasons.add("Suspicious Location + Environment")
        }

        return Pair(bonus, reasons)
    }

    private fun hasCompound(types: Set<SignalType>, vararg required: SignalType): Boolean {
        return required.all { types.contains(it) }
    }

    private fun anomalyToSignal(anomaly: AnomalyResult): Pair<SignalType, Int> {
        return when (anomaly.type) {
            AnomalyType.UNUSUAL_HOUR -> Pair(SignalType.APP_OPEN, 20)
            AnomalyType.UNUSUAL_SESSION_COUNT -> Pair(SignalType.APP_SESSION, 15)
            AnomalyType.UNUSUAL_SESSION_DURATION -> Pair(SignalType.APP_SESSION, 10)
            AnomalyType.UNKNOWN_LOCATION -> Pair(SignalType.LOCATION_ANOMALY, 30)
        }
    }

    private fun applyDecay(previousScore: RiskScore): Int {
        val hoursSince = (System.currentTimeMillis() - previousScore.timestamp) / (60 * 60 * 1000.0)
        val decayFactor = 1.0 - (DECAY_PERCENT_PER_HOUR / 100.0 * hoursSince)
        return (previousScore.totalScore * decayFactor.coerceAtLeast(0.0)).toInt()
    }

    private fun buildTriggerReason(
        contributions: Map<SignalType, Int>,
        compoundReasons: List<String>,
        behavioralRiskPoints: Int
    ): String {
        val parts = mutableListOf<String>()
        
        contributions.entries
            .sortedByDescending { it.value }
            .take(3)
            .forEach { (type, score) ->
                parts.add("${type.name}: $score")
            }
        
        if (behavioralRiskPoints > 0) {
            parts.add("Behavioral: $behavioralRiskPoints")
        }
        
        if (compoundReasons.isNotEmpty()) {
            parts.addAll(compoundReasons)
        }

        return parts.joinToString("; ")
    }

    // ============ Convenience Methods ============

    /**
     * Gets current risk level.
     */
    suspend fun getCurrentRiskLevel(): RiskLevel = withContext(Dispatchers.IO) {
        riskScoreRepository.getLatest()?.level ?: RiskLevel.NORMAL
    }

    /**
     * Gets current risk score value.
     */
    suspend fun getCurrentScore(): Int = withContext(Dispatchers.IO) {
        riskScoreRepository.getLatest()?.totalScore ?: 0
    }

    /**
     * Checks if current risk requires action.
     */
    suspend fun requiresAction(): Boolean {
        return getCurrentRiskLevel() != RiskLevel.NORMAL
    }

    /**
     * Gets weight for a signal type.
     */
    fun getSignalWeight(type: SignalType): Int {
        return SIGNAL_WEIGHTS[type] ?: 0
    }

    /**
     * Gets all signal weights for display.
     */
    fun getAllWeights(): Map<SignalType, Int> {
        return SIGNAL_WEIGHTS.toMap()
    }
    
    /**
     * Get overall learning progress across all behavioral analyzers.
     */
    suspend fun getBehavioralLearningProgress(): Float {
        val appProgress = appUsageAnalyzer.getLearningProgress()
        val locationProgress = locationClusterManager.getLearningProgress()
        val networkProgress = networkTracker.getLearningProgress()
        val unlockProgress = unlockAnalyzer.getLearningProgress()
        
        return (appProgress + locationProgress + networkProgress + unlockProgress) / 4f
    }
}
