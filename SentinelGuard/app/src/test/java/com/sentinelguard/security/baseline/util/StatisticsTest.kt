package com.sentinelguard.security.baseline.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Statistics utility.
 */
class StatisticsTest {

    @Test
    fun `mean calculates correctly`() {
        val values = listOf(1.0, 2.0, 3.0, 4.0, 5.0)
        assertEquals(3.0, Statistics.mean(values), 0.001)
    }

    @Test
    fun `mean of empty list returns zero`() {
        assertEquals(0.0, Statistics.mean(emptyList()), 0.001)
    }

    @Test
    fun `variance calculates correctly`() {
        val values = listOf(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0)
        // Mean = 5, variance should be 4
        assertEquals(4.0, Statistics.variance(values), 0.001)
    }

    @Test
    fun `standard deviation is sqrt of variance`() {
        val values = listOf(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0)
        assertEquals(2.0, Statistics.standardDeviation(values), 0.001)
    }

    @Test
    fun `zScore calculates correctly`() {
        val mean = 100.0
        val stdDev = 15.0
        val value = 130.0
        
        assertEquals(2.0, Statistics.zScore(value, mean, stdDev), 0.001)
    }

    @Test
    fun `isAnomaly detects outliers`() {
        val mean = 50.0
        val stdDev = 10.0
        
        // Value 2 stddev away is not anomaly (threshold = 2)
        assertFalse(Statistics.isAnomaly(70.0, mean, stdDev, 2.0))
        
        // Value > 2 stddev away is anomaly
        assertTrue(Statistics.isAnomaly(75.0, mean, stdDev, 2.0))
    }

    @Test
    fun `haversine distance calculates correctly`() {
        // Known distance: New York to Los Angeles ≈ 3935 km
        val nyLat = 40.7128
        val nyLng = -74.0060
        val laLat = 34.0522
        val laLng = -118.2437
        
        val distance = Statistics.haversineDistance(nyLat, nyLng, laLat, laLng)
        
        // Should be approximately 3935 km = 3,935,000 m (within 5% tolerance)
        assertTrue(distance > 3700000 && distance < 4200000)
    }

    @Test
    fun `haversine distance same point is zero`() {
        val distance = Statistics.haversineDistance(40.0, -74.0, 40.0, -74.0)
        assertEquals(0.0, distance, 0.001)
    }

    @Test
    fun `confidence returns 0 below min samples`() {
        assertEquals(0.0, Statistics.confidence(10, minSamples = 20), 0.001)
    }

    @Test
    fun `confidence returns 1 at saturation`() {
        assertEquals(1.0, Statistics.confidence(100, minSamples = 20, saturationSamples = 100), 0.001)
    }

    @Test
    fun `confidence scales linearly`() {
        // At 60 samples (halfway between 20 and 100)
        assertEquals(0.5, Statistics.confidence(60, minSamples = 20, saturationSamples = 100), 0.001)
    }

    @Test
    fun `median calculates correctly for odd count`() {
        val values = listOf(1.0, 3.0, 5.0, 7.0, 9.0)
        assertEquals(5.0, Statistics.median(values), 0.001)
    }

    @Test
    fun `median calculates correctly for even count`() {
        val values = listOf(1.0, 3.0, 5.0, 7.0)
        assertEquals(4.0, Statistics.median(values), 0.001)
    }
}
