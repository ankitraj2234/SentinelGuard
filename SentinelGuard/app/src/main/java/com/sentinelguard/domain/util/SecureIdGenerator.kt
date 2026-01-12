package com.sentinelguard.domain.util

import java.security.SecureRandom
import java.util.UUID

/**
 * Secure ID Generator
 * 
 * Generates cryptographically secure UUIDs using SecureRandom.
 * Used for all entity primary keys to prevent predictable IDs.
 * 
 * WHY: Standard UUID.randomUUID() uses a PRNG that may be predictable.
 * SecureRandom uses the system's best available entropy source.
 */
object SecureIdGenerator {

    private val secureRandom = SecureRandom()

    /**
     * Generates a cryptographically secure UUID v4.
     * 
     * Format: xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx
     * where x is random hex and y is 8, 9, A, or B
     */
    fun generateId(): String {
        val randomBytes = ByteArray(16)
        secureRandom.nextBytes(randomBytes)
        
        // Set version to 4 (random UUID)
        randomBytes[6] = (randomBytes[6].toInt() and 0x0F or 0x40).toByte()
        
        // Set variant to RFC 4122
        randomBytes[8] = (randomBytes[8].toInt() and 0x3F or 0x80).toByte()
        
        return formatAsUuid(randomBytes)
    }

    /**
     * Generates a shorter secure ID (12 chars) for less critical uses.
     */
    fun generateShortId(): String {
        val randomBytes = ByteArray(9)
        secureRandom.nextBytes(randomBytes)
        return randomBytes.joinToString("") { "%02x".format(it) }.take(12)
    }

    /**
     * Generates a secure random long (for timestamps with jitter).
     */
    fun generateSecureLong(): Long {
        return secureRandom.nextLong()
    }

    private fun formatAsUuid(bytes: ByteArray): String {
        val sb = StringBuilder(36)
        for (i in bytes.indices) {
            if (i == 4 || i == 6 || i == 8 || i == 10) {
                sb.append('-')
            }
            sb.append("%02x".format(bytes[i]))
        }
        return sb.toString()
    }

    /**
     * Validates if a string is a valid UUID format.
     */
    fun isValidUuid(id: String): Boolean {
        return try {
            UUID.fromString(id)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}
