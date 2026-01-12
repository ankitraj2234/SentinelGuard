package com.sentinelguard.security.trust

import com.sentinelguard.data.local.preferences.SecurePreferences
import com.sentinelguard.domain.model.SecuritySignal
import com.sentinelguard.domain.model.SignalType
import com.sentinelguard.domain.repository.SecuritySignalRepository
import com.sentinelguard.domain.util.SecureIdGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * TrustedDeviceManager: Handles Explicit Trust Overrides
 * 
 * WHY THIS EXISTS:
 * Some legitimate users trigger false warnings:
 * - Power users with rooted devices
 * - Users who change their own SIM
 * 
 * This allows EXPLICIT trust acknowledgment WITHOUT disabling detection.
 * 
 * KEY DESIGN:
 * - Trust is TEMPORARY (24-hour window after acknowledgment)
 * - Trust only REDUCES signal weight, not eliminates it
 * - All trust actions are LOGGED to timeline
 * - Compound detection remains ACTIVE
 * - User can REVOKE trust anytime
 */
class TrustedDeviceManager(
    private val securePreferences: SecurePreferences,
    private val signalRepository: SecuritySignalRepository
) {
    companion object {
        const val TRUST_DURATION_MS = 24 * 60 * 60 * 1000L // 24 hours
        const val TRUST_WEIGHT_REDUCTION = 0.5 // 50% reduction
    }

    private val _rootTrusted = MutableStateFlow(false)
    val rootTrusted: StateFlow<Boolean> = _rootTrusted.asStateFlow()

    private val _simChangeTrusted = MutableStateFlow(false)
    val simChangeTrusted: StateFlow<Boolean> = _simChangeTrusted.asStateFlow()

    init {
        loadTrustState()
    }

    // ============ Root Trust ============

    /**
     * Explicitly trusts rooted state.
     * Requires password verification first (done by caller).
     */
    suspend fun acknowledgeRootedDevice() {
        val now = System.currentTimeMillis()
        securePreferences.rootTrustedUntil = now + TRUST_DURATION_MS
        _rootTrusted.value = true
        
        // Log to timeline
        logTrustAction("ROOT_TRUSTED", "User acknowledged rooted device")
    }

    /**
     * Checks if root trust is active.
     */
    fun isRootTrusted(): Boolean {
        val until = securePreferences.rootTrustedUntil
        val trusted = until > 0 && System.currentTimeMillis() < until
        _rootTrusted.value = trusted
        return trusted
    }

    /**
     * Revokes root trust.
     */
    suspend fun revokeRootTrust() {
        securePreferences.rootTrustedUntil = 0
        _rootTrusted.value = false
        logTrustAction("ROOT_TRUST_REVOKED", "User revoked root trust")
    }

    // ============ SIM Change Trust ============

    /**
     * Acknowledges intentional SIM change by owner.
     * Requires password verification first (done by caller).
     */
    suspend fun acknowledgeSIMChange() {
        val now = System.currentTimeMillis()
        securePreferences.simChangeTrustedUntil = now + TRUST_DURATION_MS
        _simChangeTrusted.value = true
        
        logTrustAction("SIM_CHANGE_TRUSTED", "User acknowledged SIM change")
    }

    /**
     * Checks if SIM change trust is active.
     */
    fun isSIMChangeTrusted(): Boolean {
        val until = securePreferences.simChangeTrustedUntil
        val trusted = until > 0 && System.currentTimeMillis() < until
        _simChangeTrusted.value = trusted
        return trusted
    }

    /**
     * Revokes SIM change trust.
     */
    suspend fun revokeSIMChangeTrust() {
        securePreferences.simChangeTrustedUntil = 0
        _simChangeTrusted.value = false
        logTrustAction("SIM_TRUST_REVOKED", "User revoked SIM change trust")
    }

    // ============ Weight Adjustment ============

    /**
     * Gets adjusted weight for a signal type based on trust state.
     * 
     * @return Multiplier (1.0 = full, 0.5 = reduced)
     */
    fun getWeightMultiplier(signalType: SignalType): Double {
        return when (signalType) {
            SignalType.ROOT_DETECTED -> if (isRootTrusted()) TRUST_WEIGHT_REDUCTION else 1.0
            SignalType.SIM_CHANGED -> if (isSIMChangeTrusted()) TRUST_WEIGHT_REDUCTION else 1.0
            SignalType.SIM_REMOVED -> if (isSIMChangeTrusted()) TRUST_WEIGHT_REDUCTION else 1.0
            else -> 1.0
        }
    }

    // ============ Utilities ============

    /**
     * Revokes all trust settings.
     */
    suspend fun revokeAllTrust() {
        securePreferences.rootTrustedUntil = 0
        securePreferences.simChangeTrustedUntil = 0
        _rootTrusted.value = false
        _simChangeTrusted.value = false
        logTrustAction("ALL_TRUST_REVOKED", "User revoked all trust settings")
    }

    /**
     * Gets remaining trust time in human-readable format.
     */
    fun getRootTrustRemaining(): String = formatRemaining(securePreferences.rootTrustedUntil)
    fun getSIMTrustRemaining(): String = formatRemaining(securePreferences.simChangeTrustedUntil)

    private fun formatRemaining(until: Long): String {
        val remaining = until - System.currentTimeMillis()
        if (remaining <= 0) return "Expired"
        val hours = remaining / (60 * 60 * 1000)
        val minutes = (remaining % (60 * 60 * 1000)) / (60 * 1000)
        return "${hours}h ${minutes}m"
    }

    private fun loadTrustState() {
        _rootTrusted.value = isRootTrusted()
        _simChangeTrusted.value = isSIMChangeTrusted()
    }

    private suspend fun logTrustAction(action: String, description: String) {
        val signal = SecuritySignal(
            id = SecureIdGenerator.generateId(),
            type = SignalType.APP_SESSION, // Using session type for trust log
            value = action,
            metadata = """{"action":"$action","description":"$description"}""",
            timestamp = System.currentTimeMillis()
        )
        signalRepository.insert(signal)
    }
}
