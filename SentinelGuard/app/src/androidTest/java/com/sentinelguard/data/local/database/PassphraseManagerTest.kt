package com.sentinelguard.data.local.database

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for PassphraseManager.
 * 
 * Tests:
 * - Passphrase generation is 32 bytes (256-bit)
 * - Passphrase persists across instances
 * - Encrypted storage works correctly
 */
@RunWith(AndroidJUnit4::class)
class PassphraseManagerTest {

    private lateinit var context: Context
    private lateinit var passphraseManager: PassphraseManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        passphraseManager = PassphraseManager(context)
        passphraseManager.clearPassphrase()
    }

    @After
    fun tearDown() {
        passphraseManager.clearPassphrase()
    }

    @Test
    fun passphrase_is_32_bytes() {
        val passphrase = passphraseManager.getOrCreatePassphrase()
        
        assertEquals("Passphrase should be 32 bytes (256-bit)", 32, passphrase.size)
    }

    @Test
    fun passphrase_persists_across_instances() {
        // Create passphrase with first instance
        val passphrase1 = passphraseManager.getOrCreatePassphrase()
        
        // Create new instance and get passphrase
        val manager2 = PassphraseManager(context)
        val passphrase2 = manager2.getOrCreatePassphrase()
        
        assertArrayEquals(
            "Passphrase should be same across instances",
            passphrase1,
            passphrase2
        )
    }

    @Test
    fun passphrase_is_random() {
        // Create two separate passphrases
        val passphrase1 = passphraseManager.getOrCreatePassphrase()
        passphraseManager.clearPassphrase()
        val passphrase2 = passphraseManager.getOrCreatePassphrase()
        
        assertFalse(
            "New passphrases should be different",
            passphrase1.contentEquals(passphrase2)
        )
    }

    @Test
    fun hasPassphrase_returns_correct_state() {
        assertFalse("Should not have passphrase initially", passphraseManager.hasPassphrase())
        
        passphraseManager.getOrCreatePassphrase()
        
        assertTrue("Should have passphrase after creation", passphraseManager.hasPassphrase())
    }

    @Test
    fun clearPassphrase_removes_stored_data() {
        passphraseManager.getOrCreatePassphrase()
        assertTrue(passphraseManager.hasPassphrase())
        
        passphraseManager.clearPassphrase()
        
        assertFalse("Should not have passphrase after clear", passphraseManager.hasPassphrase())
    }
}
