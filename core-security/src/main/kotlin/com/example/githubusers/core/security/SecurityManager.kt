package com.example.githubusers.core.security

import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

internal const val SECURITY_MANAGER_SKIP_NATIVE_LOAD_PROPERTY = "com.example.githubusers.core.security.skipNativeLoad"
private const val INTEGRITY_FAILURE_CODE = -1001
private const val DEBUGGER_DETECTED_CODE = -1002
private const val DEFAULT_KEY_ID = 0

/**
 * Core security manager providing native-backed integrity checks and obfuscated key access.
 */
@Singleton
class SecurityManager @Inject constructor() {

    fun isDeviceCompromised(): Boolean = nativeIsDeviceCompromised()

    fun isDebuggingDetected(): Boolean = nativeIsDebuggingDetected()

    fun getObfuscatedKeySeed(): String = nativeGetObfuscatedKey(DEFAULT_KEY_ID) ?: ""

    fun performSecurityCheck(): SecurityResult {
        val integrityResult = nativeVerifyIntegrity()
        val isCompromised = integrityResult == INTEGRITY_FAILURE_CODE
        val isDebugging = nativeIsDebuggingDetected()
        val keySeed = nativeGetObfuscatedKey(DEFAULT_KEY_ID).orEmpty()

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

    private external fun nativeIsDeviceCompromised(): Boolean

    private external fun nativeIsDebuggingDetected(): Boolean

    private external fun nativeVerifyIntegrity(): Int

    private external fun nativeGetObfuscatedKey(keyId: Int): String?

    companion object {
        private const val NATIVE_LIBRARY_NAME = "security_core"
        private val nativeLibraryLoaded = AtomicBoolean(false)

        init {
            loadNativeLibraryIfNeeded()
        }

        private fun loadNativeLibraryIfNeeded() {
            if (shouldSkipNativeLoad()) return

            if (nativeLibraryLoaded.compareAndSet(false, true)) {
                System.loadLibrary(NATIVE_LIBRARY_NAME)
            }
        }

        private fun shouldSkipNativeLoad(): Boolean {
            val flag = System.getProperty(SECURITY_MANAGER_SKIP_NATIVE_LOAD_PROPERTY) ?: return false
            return flag.equals("true", ignoreCase = true)
        }
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
