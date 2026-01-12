package com.sentinelguard.data.database

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Manages the database encryption passphrase using Android Keystore.
 * 
 * The passphrase is generated once and stored encrypted using a key from Android Keystore.
 * This provides hardware-backed security on supported devices.
 * 
 * Security Properties:
 * - Key is stored in Android Keystore (hardware-backed when available)
 * - Passphrase is generated with secure random
 * - AES-GCM encryption for passphrase storage
 */
class DatabasePassphraseManager(private val context: Context) {

    companion object {
        private const val KEYSTORE_ALIAS = "sentinel_guard_db_key"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val PREFS_NAME = "sentinel_guard_db_prefs"
        private const val PREFS_KEY_PASSPHRASE = "encrypted_passphrase"
        private const val PREFS_KEY_IV = "passphrase_iv"
        private const val PASSPHRASE_LENGTH = 32
    }

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    /**
     * Gets the existing passphrase or creates a new one.
     * Returns the passphrase as a ByteArray for SQLCipher.
     */
    fun getOrCreatePassphrase(): ByteArray {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        val encryptedPassphrase = prefs.getString(PREFS_KEY_PASSPHRASE, null)
        val ivBase64 = prefs.getString(PREFS_KEY_IV, null)
        
        return if (encryptedPassphrase != null && ivBase64 != null) {
            // Decrypt existing passphrase
            decryptPassphrase(
                Base64.decode(encryptedPassphrase, Base64.DEFAULT),
                Base64.decode(ivBase64, Base64.DEFAULT)
            )
        } else {
            // Generate new passphrase
            createAndStorePassphrase(prefs)
        }
    }

    private fun createAndStorePassphrase(
        prefs: android.content.SharedPreferences
    ): ByteArray {
        // Generate random passphrase
        val passphrase = ByteArray(PASSPHRASE_LENGTH).apply {
            java.security.SecureRandom().nextBytes(this)
        }
        
        // Ensure key exists
        if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
            generateKey()
        }
        
        // Encrypt passphrase
        val (encryptedData, iv) = encryptPassphrase(passphrase)
        
        // Store encrypted passphrase
        prefs.edit()
            .putString(PREFS_KEY_PASSPHRASE, Base64.encodeToString(encryptedData, Base64.DEFAULT))
            .putString(PREFS_KEY_IV, Base64.encodeToString(iv, Base64.DEFAULT))
            .apply()
        
        return passphrase
    }

    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        
        val keySpec = KeyGenParameterSpec.Builder(
            KEYSTORE_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setUserAuthenticationRequired(false) // Required for background access
            .build()
        
        keyGenerator.init(keySpec)
        keyGenerator.generateKey()
    }

    private fun getSecretKey(): SecretKey {
        return (keyStore.getEntry(KEYSTORE_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
    }

    private fun encryptPassphrase(passphrase: ByteArray): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        
        val encryptedData = cipher.doFinal(passphrase)
        return Pair(encryptedData, cipher.iv)
    }

    private fun decryptPassphrase(encryptedData: ByteArray, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
        
        return cipher.doFinal(encryptedData)
    }

    /**
     * Deletes the passphrase and key. 
     * WARNING: This will make the database unrecoverable!
     */
    fun deletePassphrase() {
        // Delete encrypted passphrase
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
        
        // Delete key from keystore
        if (keyStore.containsAlias(KEYSTORE_ALIAS)) {
            keyStore.deleteEntry(KEYSTORE_ALIAS)
        }
    }
}
