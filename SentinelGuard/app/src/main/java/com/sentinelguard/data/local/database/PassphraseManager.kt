package com.sentinelguard.data.local.database

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * PassphraseManager: Secure SQLCipher Passphrase Storage
 * 
 * WHY THIS EXISTS:
 * SQLCipher requires a passphrase to encrypt the database. This passphrase
 * must be stored securely and survive app restarts. We use Android Keystore
 * to generate an AES-256 key that encrypts the passphrase. The encrypted
 * passphrase is stored in EncryptedSharedPreferences.
 * 
 * SECURITY PROPERTIES:
 * - Passphrase is 32 random bytes (256-bit)
 * - Keystore key never leaves secure hardware
 * - AES-GCM provides authenticated encryption
 * - Tampering is detected via authentication tag
 */
class PassphraseManager(private val context: Context) {

    companion object {
        private const val KEYSTORE_ALIAS = "sentinel_db_key"
        private const val PREFS_NAME = "sentinel_db_prefs"
        private const val KEY_ENCRYPTED_PASSPHRASE = "encrypted_passphrase"
        private const val KEY_IV = "passphrase_iv"
        
        // Passphrase length in bytes (256-bit)
        private const val PASSPHRASE_LENGTH = 32
        
        // GCM authentication tag length
        private const val GCM_TAG_LENGTH = 128
    }

    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    /**
     * Gets or creates the database passphrase.
     * 
     * On first call: Generates a new random passphrase, encrypts it, stores it.
     * On subsequent calls: Retrieves and decrypts the stored passphrase.
     * 
     * @throws SecurityException if Keystore operations fail
     * @throws CorruptedPassphraseException if stored passphrase is corrupted
     */
    fun getOrCreatePassphrase(): ByteArray {
        val prefs = getEncryptedPrefs()
        val storedEncryptedPassphrase = prefs.getString(KEY_ENCRYPTED_PASSPHRASE, null)
        
        return if (storedEncryptedPassphrase != null) {
            // Decrypt existing passphrase
            decryptPassphrase(prefs)
        } else {
            // Generate new passphrase
            createAndStorePassphrase(prefs)
        }
    }

    /**
     * Checks if a passphrase already exists.
     */
    fun hasPassphrase(): Boolean {
        return getEncryptedPrefs().contains(KEY_ENCRYPTED_PASSPHRASE)
    }

    /**
     * Clears the stored passphrase. 
     * WARNING: This will make the database unreadable!
     */
    fun clearPassphrase() {
        getEncryptedPrefs().edit().clear().apply()
        try {
            keyStore.deleteEntry(KEYSTORE_ALIAS)
        } catch (e: Exception) {
            // Key may not exist
        }
    }

    private fun createAndStorePassphrase(prefs: android.content.SharedPreferences): ByteArray {
        // Generate random passphrase
        val passphrase = ByteArray(PASSPHRASE_LENGTH)
        SecureRandom().nextBytes(passphrase)

        // Generate Keystore key
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        
        val keySpec = KeyGenParameterSpec.Builder(
            KEYSTORE_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setRandomizedEncryptionRequired(true)
            .build()
        
        keyGenerator.init(keySpec)
        keyGenerator.generateKey()

        // Encrypt passphrase
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getKeystoreKey())
        
        val encryptedPassphrase = cipher.doFinal(passphrase)
        val iv = cipher.iv

        // Store encrypted passphrase and IV
        prefs.edit()
            .putString(KEY_ENCRYPTED_PASSPHRASE, Base64.encodeToString(encryptedPassphrase, Base64.NO_WRAP))
            .putString(KEY_IV, Base64.encodeToString(iv, Base64.NO_WRAP))
            .apply()

        return passphrase
    }

    private fun decryptPassphrase(prefs: android.content.SharedPreferences): ByteArray {
        val encryptedPassphraseBase64 = prefs.getString(KEY_ENCRYPTED_PASSPHRASE, null)
            ?: throw CorruptedPassphraseException("No encrypted passphrase found")
        
        val ivBase64 = prefs.getString(KEY_IV, null)
            ?: throw CorruptedPassphraseException("No IV found")

        return try {
            val encryptedPassphrase = Base64.decode(encryptedPassphraseBase64, Base64.NO_WRAP)
            val iv = Base64.decode(ivBase64, Base64.NO_WRAP)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, getKeystoreKey(), spec)

            cipher.doFinal(encryptedPassphrase)
        } catch (e: Exception) {
            throw CorruptedPassphraseException("Failed to decrypt passphrase: ${e.message}", e)
        }
    }

    private fun getKeystoreKey(): SecretKey {
        val entry = keyStore.getEntry(KEYSTORE_ALIAS, null) as? KeyStore.SecretKeyEntry
            ?: throw SecurityException("Keystore key not found")
        return entry.secretKey
    }

    private fun getEncryptedPrefs(): android.content.SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}

/**
 * Exception thrown when passphrase storage is corrupted.
 */
class CorruptedPassphraseException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
