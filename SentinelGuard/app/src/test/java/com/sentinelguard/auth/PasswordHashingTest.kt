package com.sentinelguard.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for password hashing.
 */
class PasswordHashingTest {

    companion object {
        private const val BCRYPT_COST = 12
    }

    @Test
    fun `bcrypt hash is generated correctly`() {
        val password = "SecurePassword123!"
        
        val hash = BCrypt.withDefaults()
            .hashToString(BCRYPT_COST, password.toCharArray())
        
        assertNotNull(hash)
        assertTrue(hash.startsWith("\$2a\$") || hash.startsWith("\$2b\$"))
    }

    @Test
    fun `bcrypt verification succeeds for correct password`() {
        val password = "SecurePassword123!"
        
        val hash = BCrypt.withDefaults()
            .hashToString(BCRYPT_COST, password.toCharArray())
        
        val result = BCrypt.verifyer()
            .verify(password.toCharArray(), hash)
        
        assertTrue(result.verified)
    }

    @Test
    fun `bcrypt verification fails for incorrect password`() {
        val password = "SecurePassword123!"
        val wrongPassword = "WrongPassword456!"
        
        val hash = BCrypt.withDefaults()
            .hashToString(BCRYPT_COST, password.toCharArray())
        
        val result = BCrypt.verifyer()
            .verify(wrongPassword.toCharArray(), hash)
        
        assertFalse(result.verified)
    }

    @Test
    fun `same password produces different hashes`() {
        val password = "SecurePassword123!"
        
        val hash1 = BCrypt.withDefaults()
            .hashToString(BCRYPT_COST, password.toCharArray())
        val hash2 = BCrypt.withDefaults()
            .hashToString(BCRYPT_COST, password.toCharArray())
        
        // Hashes should be different due to random salt
        assertNotEquals(hash1, hash2)
        
        // But both should verify
        assertTrue(BCrypt.verifyer().verify(password.toCharArray(), hash1).verified)
        assertTrue(BCrypt.verifyer().verify(password.toCharArray(), hash2).verified)
    }

    @Test
    fun `empty password can be hashed and verified`() {
        val password = ""
        
        val hash = BCrypt.withDefaults()
            .hashToString(BCRYPT_COST, password.toCharArray())
        
        val result = BCrypt.verifyer()
            .verify(password.toCharArray(), hash)
        
        assertTrue(result.verified)
    }

    @Test
    fun `unicode password can be hashed and verified`() {
        val password = "密码测试🔐"
        
        val hash = BCrypt.withDefaults()
            .hashToString(BCRYPT_COST, password.toCharArray())
        
        val result = BCrypt.verifyer()
            .verify(password.toCharArray(), hash)
        
        assertTrue(result.verified)
    }

    @Test
    fun `very long password can be hashed`() {
        val password = "A".repeat(72) // bcrypt max is 72 bytes
        
        val hash = BCrypt.withDefaults()
            .hashToString(BCRYPT_COST, password.toCharArray())
        
        val result = BCrypt.verifyer()
            .verify(password.toCharArray(), hash)
        
        assertTrue(result.verified)
    }
}
