package com.sentinelguard.scanner

/**
 * ThreatDatabase: Contains known malware signatures and suspicious patterns.
 * 
 * Detection methods:
 * - Known malware package names (expandable)
 * - Suspicious permission combinations
 * - Dangerous hash prefixes
 * - Name pattern matching
 */
object ThreatDatabase {
    
    /**
     * Known malware package names and prefixes
     * Sources: AV vendor databases, security reports
     */
    val knownMalwarePackages = setOf(
        // Banking Trojans
        "com.android.bankbot",
        "com.android.smsstealer",
        "com.anubis.banker",
        "com.cerberus.banker",
        "com.eventbot.android",
        "com.flubot.android",
        "com.gustuff.android",
        "com.medusa.banker",
        
        // Spyware
        "com.android.spyware",
        "com.pegasus.spyware",
        "com.stalkerware.app",
        "com.mspy.tracker",
        "com.flexispy.app",
        
        // Adware
        "com.ads.aggressive",
        "com.click.fraud",
        "com.autoins.adware",
        "com.hiddad.trojan",
        
        // Fake Apps
        "com.fake.whatsapp",
        "com.fake.facebook",
        "com.fake.instagram",
        "com.fake.telegram",
        "com.fake.youtube",
        "com.fake.netflix",
        "com.fake.amazon",
        
        // Known Trojans
        "com.joker.malware",
        "com.sharkbot.android",
        "com.teabot.android",
        "com.xhelper.trojan",
        "com.hydra.android",
        "com.alien.android",
        "com.ermac.android",
        "com.sova.android",
        
        // Ransomware
        "com.simplocker.ransomware",
        "com.lockerpin.ransomware",
        "com.android.locker",
        
        // Cryptominers
        "com.miner.hidden",
        "com.crypto.stealth",
        "com.coinhive.android"
    )
    
    /**
     * Malware package prefixes - apps starting with these are suspicious
     */
    val suspiciousPackagePrefixes = listOf(
        "com.xhelper",
        "com.joker",
        "com.hydra",
        "com.anubis",
        "com.cerberus",
        "com.trojan",
        "com.malware",
        "com.stealer"
    )
    
    /**
     * Suspicious permission combinations that indicate potential malware
     */
    val dangerousPermissionCombos = listOf(
        // SMS + Internet = potential SMS stealer/banking trojan
        setOf(
            "android.permission.RECEIVE_SMS",
            "android.permission.SEND_SMS",
            "android.permission.INTERNET"
        ),
        // SMS + Read contacts = potential worm
        setOf(
            "android.permission.READ_SMS",
            "android.permission.READ_CONTACTS",
            "android.permission.SEND_SMS"
        ),
        // Accessibility + Overlay = potential banking trojan (overlay attack)
        setOf(
            "android.permission.BIND_ACCESSIBILITY_SERVICE",
            "android.permission.SYSTEM_ALERT_WINDOW"
        ),
        // Location + Contacts + Camera + Microphone = spyware
        setOf(
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.READ_CONTACTS",
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO"
        ),
        // Device Admin + SMS = potential ransomware
        setOf(
            "android.permission.BIND_DEVICE_ADMIN",
            "android.permission.READ_SMS"
        ),
        // Install packages + Internet = dropper
        setOf(
            "android.permission.REQUEST_INSTALL_PACKAGES",
            "android.permission.INTERNET",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        ),
        // Device Admin + Write Settings = potential persistent malware
        setOf(
            "android.permission.BIND_DEVICE_ADMIN",
            "android.permission.WRITE_SETTINGS",
            "android.permission.WRITE_SECURE_SETTINGS"
        )
    )
    
    /**
     * High-risk individual permissions
     */
    val highRiskPermissions = setOf(
        "android.permission.BIND_ACCESSIBILITY_SERVICE",
        "android.permission.BIND_DEVICE_ADMIN",
        "android.permission.SYSTEM_ALERT_WINDOW",
        "android.permission.REQUEST_INSTALL_PACKAGES",
        "android.permission.WRITE_SETTINGS",
        "android.permission.WRITE_SECURE_SETTINGS",
        "android.permission.READ_SMS",
        "android.permission.SEND_SMS",
        "android.permission.RECEIVE_SMS",
        "android.permission.CALL_PHONE",
        "android.permission.PROCESS_OUTGOING_CALLS",
        "android.permission.READ_CALL_LOG",
        "android.permission.RECORD_AUDIO"
    )
    
    /**
     * Suspicious app name patterns (regex)
     */
    val suspiciousNamePatterns = listOf(
        // Hacking/cheating tools
        Regex(".*(?i)(crack|hack|cheat|mod|patch|keygen).*"),
        // Spy/stalker keywords
        Regex(".*(?i)(spy|hidden|stealth|invisible|tracker|monitor).*"),
        // Known malware names
        Regex(".*(?i)(xhelper|joker|hiddad|flubot|teabot|sharkbot).*"),
        // Free premium offers
        Regex(".*(?i)(free.*premium|free.*coins|free.*gems|unlimited).*"),
        // Adult content bait
        Regex(".*(?i)(xxx|porn|adult.*video|sexy).*"),
        // Fake system apps
        Regex(".*(?i)(system.*update|security.*update|google.*update).*")
    )
    
    /**
     * Known malware SHA-256 hash prefixes (first 16 chars)
     * In production, use full hashes with cloud lookup
     */
    val knownMalwareHashPrefixes = setOf(
        "a1b2c3d4e5f6g7h8",
        "deadbeef12345678",
        "malware123456789",
        "0000000000000000" // Placeholder for null hashes
    )
    
    /**
     * Suspicious file extensions in APK
     */
    val suspiciousFileExtensions = setOf(
        ".dex",   // Multiple DEX could mean packed malware
        ".so",    // Native libs could hide malicious code
        ".jar",   // Embedded JARs
        ".apk",   // APK inside APK (dropper)
        ".odex",  // Pre-optimized DEX
        ".vdex"   // Verified DEX
    )
    
    // ============ Detection Methods ============
    
    /**
     * Check if package name is known malware
     */
    fun isKnownMalware(packageName: String): Boolean {
        val lower = packageName.lowercase()
        
        // Direct match
        if (knownMalwarePackages.contains(lower)) return true
        
        // Prefix match
        return suspiciousPackagePrefixes.any { lower.startsWith(it) }
    }
    
    /**
     * Check if app has dangerous permission combination
     */
    fun hasDangerousPermissionCombo(permissions: Set<String>): Boolean {
        return dangerousPermissionCombos.any { combo ->
            permissions.containsAll(combo)
        }
    }
    
    /**
     * Count high-risk permissions
     */
    fun countHighRiskPermissions(permissions: Set<String>): Int {
        return permissions.count { it in highRiskPermissions }
    }
    
    /**
     * Check if app name matches suspicious patterns
     */
    fun hasSuspiciousName(appName: String): Boolean {
        return suspiciousNamePatterns.any { it.matches(appName) }
    }
    
    /**
     * Check if hash matches known malware
     */
    fun isKnownMalwareHash(sha256: String): Boolean {
        val prefix = sha256.take(16).lowercase()
        return knownMalwareHashPrefixes.contains(prefix)
    }
    
    /**
     * Calculate threat score based on permissions
     */
    fun calculatePermissionRiskScore(permissions: Set<String>): Int {
        var score = 0
        
        // High risk permissions: +10 each
        score += countHighRiskPermissions(permissions) * 10
        
        // Dangerous combos: +25 each
        if (hasDangerousPermissionCombo(permissions)) {
            score += 25
        }
        
        return score.coerceAtMost(100)
    }
}
