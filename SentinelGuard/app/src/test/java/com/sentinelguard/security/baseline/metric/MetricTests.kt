package com.sentinelguard.security.baseline.metric

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for baseline metrics.
 */
class MetricTests {

    // ============ UsageHourMetric Tests ============

    @Test
    fun `usage hour histogram has 24 buckets`() {
        val metric = UsageHourMetric()
        assertEquals(24, metric.getHistogram().size)
    }

    @Test
    fun `peak hours returns top 3`() {
        val metric = UsageHourMetric()
        val peaks = metric.getPeakHours()
        assertTrue(peaks.size <= 3)
    }

    // ============ SessionMetric Tests ============

    @Test
    fun `average sessions per day starts at zero`() {
        val metric = SessionMetric()
        assertEquals(0.0, metric.getAverageSessionsPerDay(), 0.001)
    }

    // ============ LocationMetric Tests ============

    @Test
    fun `location metric starts with no clusters`() {
        val metric = LocationMetric()
        assertTrue(metric.getClusters().isEmpty())
    }

    @Test
    fun `adding location creates cluster`() {
        val metric = LocationMetric()
        metric.addLocation(40.7128, -74.0060) // NYC
        
        assertEquals(1, metric.getClusters().size)
    }

    @Test
    fun `nearby locations merge into cluster`() {
        val metric = LocationMetric()
        
        // Add two points within 500m
        metric.addLocation(40.7128, -74.0060)
        metric.addLocation(40.7130, -74.0062) // Very close
        
        // Should still be 1 cluster with 2 hits
        assertEquals(1, metric.getClusters().size)
        assertEquals(2, metric.getClusters()[0].hitCount)
    }

    @Test
    fun `distant locations create separate clusters`() {
        val metric = LocationMetric()
        
        // NYC
        metric.addLocation(40.7128, -74.0060)
        // Los Angeles (far away)
        metric.addLocation(34.0522, -118.2437)
        
        assertEquals(2, metric.getClusters().size)
    }

    @Test
    fun `unknown location detected when outside all clusters`() {
        val metric = LocationMetric()
        
        // Build up a cluster with enough hits
        repeat(4) {
            metric.addLocation(40.7128, -74.0060) // NYC
        }
        repeat(4) {
            metric.addLocation(34.0522, -118.2437) // LA
        }
        
        // Check location far from both
        val isAnomaly = metric.isLocationAnomaly(51.5074, -0.1278) // London
        assertTrue(isAnomaly)
    }

    @Test
    fun `known location not detected as anomaly`() {
        val metric = LocationMetric()
        
        // Build up clusters
        repeat(4) {
            metric.addLocation(40.7128, -74.0060)
        }
        
        // Check same location
        val isKnown = metric.isLocationKnown(40.7128, -74.0060)
        assertTrue(isKnown)
    }
}
