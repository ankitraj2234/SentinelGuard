package com.sentinelguard.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User account entity.
 * 
 * Stores:
 * - Email (for identity and alerts)
 * - Password hash (bcrypt)
 * - Biometric enrollment status
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val email: String,
    
    /** bcrypt hash of the password */
    val passwordHash: String,
    
    /** Whether biometric authentication is enabled */
    val biometricEnabled: Boolean = false,
    
    /** Timestamp when account was created */
    val createdAt: Long = System.currentTimeMillis(),
    
    /** Last successful login timestamp */
    val lastLoginAt: Long? = null,
    
    /** Number of failed login attempts (for rate limiting) */
    val failedLoginAttempts: Int = 0,
    
    /** Timestamp when lockout expires (if locked out) */
    val lockoutUntil: Long? = null
)
