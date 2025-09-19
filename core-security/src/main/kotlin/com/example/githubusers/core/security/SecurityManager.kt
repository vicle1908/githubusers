package com.example.githubusers.core.security

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Core security manager providing native security functionality.
 * Uses JNI bridge pattern with RegisterNatives for optimal performance.
 */
@Singleton
class SecurityManager @Inject constructor() {

    companion object {
        init {
            // Load the native security library
            System.loadLibrary("security_core")
        }
    }

    /**
     * Checks if the device is compromised (rooted, debuggable, etc.)
     * @return true if device shows signs of compromise
     */
    external fun isDeviceCompromised(): Boolean

    /**
     * Detects if debugging tools are attached
     * @return true if debugging is detected
     */
    external fun isDebuggingDetected(): Boolean

    /**
     * Gets obfuscated key seed for cryptographic operations
     * @return obfuscated key material as string
     */
    external fun getObfuscatedKeySeed(): String

    /**
     * Comprehensive security check combining all native checks
     * @return SecurityResult with detailed findings
     */
    fun performSecurityCheck(): SecurityResult {
        val isCompromised = isDeviceCompromised()
        val isDebugging = isDebuggingDetected()
        val keySeed = getObfuscatedKeySeed()
        
        return SecurityResult(
            isDeviceCompromised = isCompromised,
            isDebuggingDetected = isDebugging,
            hasValidKeySeed = keySeed.isNotEmpty(),
            securityLevel = when {
                isCompromised || isDebugging -> SecurityLevel.HIGH_RISK
                keySeed.isEmpty() -> SecurityLevel.MEDIUM_RISK
                else -> SecurityLevel.SECURE
            }
        )
    }
}

/**
 * Security assessment result
 */
data class SecurityResult(
    val isDeviceCompromised: Boolean,
    val isDebuggingDetected: Boolean,
    val hasValidKeySeed: Boolean,
    val securityLevel: SecurityLevel
)

/**
 * Security risk levels
 */
enum class SecurityLevel {
    SECURE,
    MEDIUM_RISK,
    HIGH_RISK
}