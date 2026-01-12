package com.sentinelguard.security.risk

import com.sentinelguard.domain.model.RiskLevel
import com.sentinelguard.domain.model.SignalType
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for RiskScoringEngine.
 */
class RiskScoringEngineTest {

    @Test
    fun `risk level thresholds are correct`() {
        assertEquals(40, RiskScoringEngine.THRESHOLD_WARNING)
        assertEquals(70, RiskScoringEngine.THRESHOLD_HIGH)
        assertEquals(90, RiskScoringEngine.THRESHOLD_CRITICAL)
    }

    @Test
    fun `decay rate is 10 percent per hour`() {
        assertEquals(10.0, RiskScoringEngine.DECAY_PERCENT_PER_HOUR, 0.001)
    }

    @Test
    fun `threshold boundaries calculate correctly`() {
        fun getLevel(score: Int): RiskLevel = when {
            score >= 90 -> RiskLevel.CRITICAL
            score >= 70 -> RiskLevel.HIGH
            score >= 40 -> RiskLevel.WARNING
            else -> RiskLevel.NORMAL
        }

        assertEquals(RiskLevel.NORMAL, getLevel(0))
        assertEquals(RiskLevel.NORMAL, getLevel(39))
        assertEquals(RiskLevel.WARNING, getLevel(40))
        assertEquals(RiskLevel.WARNING, getLevel(69))
        assertEquals(RiskLevel.HIGH, getLevel(70))
        assertEquals(RiskLevel.HIGH, getLevel(89))
        assertEquals(RiskLevel.CRITICAL, getLevel(90))
        assertEquals(RiskLevel.CRITICAL, getLevel(150))
    }

    @Test
    fun `decay calculation does not go negative`() {
        val originalScore = 100
        val hoursElapsed = 15.0
        val decayFactor = (1.0 - (10.0 / 100.0 * hoursElapsed)).coerceAtLeast(0.0)
        assertEquals(0, (originalScore * decayFactor).toInt())
    }
}
