package com.sentinelguard.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sentinelguard.data.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for User operations.
 */
@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: UserEntity): Long

    @Update
    suspend fun update(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getFirstUser(): UserEntity?

    @Query("SELECT * FROM users LIMIT 1")
    fun observeFirstUser(): Flow<UserEntity?>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query("UPDATE users SET failedLoginAttempts = :attempts, lockoutUntil = :lockoutUntil WHERE id = :userId")
    suspend fun updateLoginAttempts(userId: Long, attempts: Int, lockoutUntil: Long?)

    @Query("UPDATE users SET lastLoginAt = :timestamp, failedLoginAttempts = 0, lockoutUntil = NULL WHERE id = :userId")
    suspend fun updateSuccessfulLogin(userId: Long, timestamp: Long)

    @Query("UPDATE users SET biometricEnabled = :enabled WHERE id = :userId")
    suspend fun updateBiometricEnabled(userId: Long, enabled: Boolean)

    @Query("UPDATE users SET passwordHash = :newHash WHERE id = :userId")
    suspend fun updatePassword(userId: Long, newHash: String)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteById(userId: Long)

    @Query("DELETE FROM users")
    suspend fun deleteAll()
}
