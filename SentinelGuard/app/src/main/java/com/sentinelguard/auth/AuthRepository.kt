package com.sentinelguard.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import com.sentinelguard.data.database.dao.UserDao
import com.sentinelguard.data.database.entities.UserEntity
import com.sentinelguard.data.preferences.SecurePreferencesManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Authentication result sealed class.
 */
sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class LockedOut(val unlockTime: Long) : AuthResult()
    data object InvalidCredentials : AuthResult()
    data object UserNotFound : AuthResult()
    data class Error(val message: String) : AuthResult()
}

/**
 * Repository handling all authentication operations.
 * 
 * Password Security:
 * - Uses bcrypt for password hashing
 * - Cost factor of 12 (adjustable based on device performance)
 * - No plaintext passwords ever stored
 */
@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val securePrefs: SecurePreferencesManager
) {

    companion object {
        /** bcrypt cost factor - higher is more secure but slower */
        private const val BCRYPT_COST = 12
        
        /** Maximum failed attempts before lockout */
        private const val MAX_FAILED_ATTEMPTS = 5
        
        /** Lockout durations in milliseconds (progressive) */
        private val LOCKOUT_DURATIONS = listOf(
            30_000L,    // 30 seconds
            60_000L,    // 1 minute
            300_000L,   // 5 minutes
            900_000L,   // 15 minutes
            3600_000L   // 1 hour
        )
    }

    /**
     * Creates a new user account.
     * 
     * @param email User's email address
     * @param password Plaintext password (will be hashed)
     * @return Created user or error
     */
    suspend fun createAccount(email: String, password: String): Result<UserEntity> {
        return try {
            // Check if user already exists
            val existingUser = userDao.getByEmail(email)
            if (existingUser != null) {
                return Result.failure(Exception("Account already exists"))
            }

            // Hash password with bcrypt
            val passwordHash = BCrypt.withDefaults()
                .hashToString(BCRYPT_COST, password.toCharArray())

            // Create user entity
            val user = UserEntity(
                email = email,
                passwordHash = passwordHash,
                createdAt = System.currentTimeMillis()
            )

            val userId = userDao.insert(user)
            val createdUser = user.copy(id = userId)

            // Mark setup and onboarding as complete, start learning
            securePrefs.isSetupComplete = true
            securePrefs.hasCompletedOnboarding = true
            securePrefs.learningStartDate = System.currentTimeMillis()
            securePrefs.alertRecipient = email

            Result.success(createdUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Authenticates a user with email and password.
     * 
     * Implements:
     * - Rate limiting via lockout
     * - Progressive lockout duration
     * - Secure password verification
     */
    suspend fun authenticate(email: String, password: String): AuthResult {
        val user = userDao.getByEmail(email) ?: return AuthResult.UserNotFound

        // Check for active lockout
        user.lockoutUntil?.let { lockoutTime ->
            if (System.currentTimeMillis() < lockoutTime) {
                return AuthResult.LockedOut(lockoutTime)
            }
        }

        // Verify password
        val result = BCrypt.verifyer()
            .verify(password.toCharArray(), user.passwordHash)

        return if (result.verified) {
            // Successful login
            userDao.updateSuccessfulLogin(user.id, System.currentTimeMillis())
            securePrefs.isSessionActive = true
            securePrefs.lastAuthTime = System.currentTimeMillis()
            securePrefs.isAppLocked = false
            AuthResult.Success(user)
        } else {
            // Failed login - increment attempts
            val newAttempts = user.failedLoginAttempts + 1
            val lockoutUntil = if (newAttempts >= MAX_FAILED_ATTEMPTS) {
                calculateLockoutTime(newAttempts)
            } else null

            userDao.updateLoginAttempts(user.id, newAttempts, lockoutUntil)

            if (lockoutUntil != null) {
                AuthResult.LockedOut(lockoutUntil)
            } else {
                AuthResult.InvalidCredentials
            }
        }
    }

    /**
     * Verifies password for sensitive operations.
     */
    suspend fun verifyPassword(userId: Long, password: String): Boolean {
        val user = userDao.getById(userId) ?: return false
        return BCrypt.verifyer()
            .verify(password.toCharArray(), user.passwordHash)
            .verified
    }

    /**
     * Changes user password.
     */
    suspend fun changePassword(
        userId: Long, 
        currentPassword: String, 
        newPassword: String
    ): Result<Unit> {
        if (!verifyPassword(userId, currentPassword)) {
            return Result.failure(Exception("Current password is incorrect"))
        }

        val newHash = BCrypt.withDefaults()
            .hashToString(BCRYPT_COST, newPassword.toCharArray())

        userDao.updatePassword(userId, newHash)
        return Result.success(Unit)
    }

    /**
     * Enables or disables biometric authentication.
     */
    suspend fun setBiometricEnabled(userId: Long, enabled: Boolean) {
        userDao.updateBiometricEnabled(userId, enabled)
    }

    /**
     * Gets the current logged-in user.
     */
    suspend fun getCurrentUser(): UserEntity? {
        return userDao.getFirstUser()
    }

    /**
     * Checks if a user account exists.
     */
    suspend fun hasAccount(): Boolean {
        return userDao.getUserCount() > 0
    }

    /**
     * Checks if an email is registered.
     */
    suspend fun isEmailRegistered(email: String): Boolean {
        return userDao.getByEmail(email) != null
    }

    /**
     * Starts a session for the given user (used for biometric login).
     */
    fun startSession(userId: Long) {
        securePrefs.isSessionActive = true
        securePrefs.lastAuthTime = System.currentTimeMillis()
        securePrefs.isAppLocked = false
    }

    /**
     * Ends the current session.
     */
    fun logout() {
        securePrefs.clearSession()
    }

    /**
     * Deletes all user data.
     */
    suspend fun deleteAccount() {
        userDao.deleteAll()
        securePrefs.clearAll()
    }

    /**
     * Resets password after recovery code validation.
     * Does NOT require current password.
     */
    suspend fun resetPassword(email: String, newPassword: String): Result<Unit> {
        val user = userDao.getByEmail(email) ?: return Result.failure(Exception("User not found"))

        val newHash = BCrypt.withDefaults()
            .hashToString(BCRYPT_COST, newPassword.toCharArray())

        userDao.updatePassword(user.id, newHash)
        
        // Clear any lockout
        userDao.updateLoginAttempts(user.id, 0, null)
        
        return Result.success(Unit)
    }

    private fun calculateLockoutTime(attempts: Int): Long {
        val index = minOf(attempts - MAX_FAILED_ATTEMPTS, LOCKOUT_DURATIONS.size - 1)
        return System.currentTimeMillis() + LOCKOUT_DURATIONS[index]
    }
}
