package com.sentinelguard.domain.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for SecureIdGenerator.
 */
class SecureIdGeneratorTest {

    @Test
    fun `generateId returns valid UUID format`() {
        val id = SecureIdGenerator.generateId()
        
        assertTrue(SecureIdGenerator.isValidUuid(id))
        assertEquals(36, id.length)
        assertTrue(id.contains("-"))
    }

    @Test
    fun `generateId returns unique values`() {
        val ids = (1..100).map { SecureIdGenerator.generateId() }
        val unique = ids.toSet()
        
        assertEquals("All IDs should be unique", 100, unique.size)
    }

    @Test
    fun `generateShortId returns 12 character hex string`() {
        val shortId = SecureIdGenerator.generateShortId()
        
        assertEquals(12, shortId.length)
        assertTrue(shortId.all { it in '0'..'9' || it in 'a'..'f' })
    }

    @Test
    fun `isValidUuid correctly validates`() {
        assertTrue(SecureIdGenerator.isValidUuid("123e4567-e89b-12d3-a456-426614174000"))
        assertFalse(SecureIdGenerator.isValidUuid("not-a-uuid"))
        assertFalse(SecureIdGenerator.isValidUuid(""))
        assertFalse(SecureIdGenerator.isValidUuid("123"))
    }

    @Test
    fun `generateSecureLong returns values`() {
        val values = (1..10).map { SecureIdGenerator.generateSecureLong() }
        
        // Should have some variety (not all zeros)
        assertTrue(values.any { it != 0L })
    }
}
