package com.sentinelguard.security.collector.detectors

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for EmulatorDetector.
 */
class EmulatorDetectorTest {

    @Test
    fun `detection details returns valid JSON`() {
        val detector = EmulatorDetector()
        detector.isEmulator() // Run detection
        
        val details = detector.getDetectionDetails()
        
        assertNotNull(details)
        assertTrue(details.contains("build_fingerprint"))
        assertTrue(details.contains("build_model"))
    }
}

/**
 * Unit tests for RootDetector.
 */
class RootDetectorTest {

    @Test
    fun `detection details returns valid JSON`() {
        val detector = RootDetector()
        detector.isRooted() // Run detection
        
        val details = detector.getDetectionDetails()
        
        assertNotNull(details)
        assertTrue(details.contains("reasons"))
        assertTrue(details.contains("build_tags"))
    }
}

/**
 * Unit tests for DebuggerDetector.
 */
class DebuggerDetectorTest {

    @Test
    fun `detection details returns valid JSON`() {
        val detector = DebuggerDetector()
        detector.isDebuggerAttached() // Run detection
        
        val details = detector.getDetectionDetails()
        
        assertNotNull(details)
        assertTrue(details.contains("debugger_connected"))
    }
}
