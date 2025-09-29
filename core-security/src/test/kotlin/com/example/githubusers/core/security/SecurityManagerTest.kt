package com.example.githubusers.core.security

import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

/**
 * Unit tests for SecurityManager.
 */
class SecurityManagerTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun disableNativeLibraryLoading() {
            System.setProperty(SECURITY_MANAGER_SKIP_NATIVE_LOAD_PROPERTY, "true")
        }

        @JvmStatic
        @AfterClass
        fun clearNativeLibraryFlag() {
            System.clearProperty(SECURITY_MANAGER_SKIP_NATIVE_LOAD_PROPERTY)
        }
    }

    private lateinit var securityManager: SecurityManager

    @Before
    fun setUp() {
        // Create a spy to mock native method calls
        securityManager = spyk(SecurityManager())
    }

    @Test
    fun test_performSecurityCheck_allSecure_returnsSecureLevel() {
        // Arrange
        every { securityManager.isDeviceCompromised() } returns false
        every { securityManager.isDebuggingDetected() } returns false
        every { securityManager.getObfuscatedKeySeed() } returns "secure_key_seed"

        // Act
        val result = securityManager.performSecurityCheck()

        // Assert
        assertFalse("Device should not be compromised", result.isDeviceCompromised)
        assertFalse("Debugging should not be detected", result.isDebuggingDetected)
        assertTrue("Should have valid key seed", result.hasValidKeySeed)
        assertEquals("Security level should be SECURE", SecurityLevel.SECURE, result.securityLevel)
    }

    @Test
    fun test_performSecurityCheck_deviceCompromised_returnsHighRisk() {
        // Arrange
        every { securityManager.isDeviceCompromised() } returns true
        every { securityManager.isDebuggingDetected() } returns false
        every { securityManager.getObfuscatedKeySeed() } returns "secure_key_seed"

        // Act
        val result = securityManager.performSecurityCheck()

        // Assert
        assertTrue("Device should be compromised", result.isDeviceCompromised)
        assertEquals("Security level should be HIGH_RISK", SecurityLevel.HIGH_RISK, result.securityLevel)
    }

    @Test
    fun test_performSecurityCheck_debuggingDetected_returnsHighRisk() {
        // Arrange
        every { securityManager.isDeviceCompromised() } returns false
        every { securityManager.isDebuggingDetected() } returns true
        every { securityManager.getObfuscatedKeySeed() } returns "secure_key_seed"

        // Act
        val result = securityManager.performSecurityCheck()

        // Assert
        assertTrue("Debugging should be detected", result.isDebuggingDetected)
        assertEquals("Security level should be HIGH_RISK", SecurityLevel.HIGH_RISK, result.securityLevel)
    }

    @Test
    fun test_performSecurityCheck_emptyKeySeed_returnsMediumRisk() {
        // Arrange
        every { securityManager.isDeviceCompromised() } returns false
        every { securityManager.isDebuggingDetected() } returns false
        every { securityManager.getObfuscatedKeySeed() } returns ""

        // Act
        val result = securityManager.performSecurityCheck()

        // Assert
        assertFalse("Should not have valid key seed", result.hasValidKeySeed)
        assertEquals("Security level should be MEDIUM_RISK", SecurityLevel.MEDIUM_RISK, result.securityLevel)
    }

    @Test
    fun test_performSecurityCheck_multipleIssues_returnsHighRisk() {
        // Arrange - multiple security issues
        every { securityManager.isDeviceCompromised() } returns true
        every { securityManager.isDebuggingDetected() } returns true
        every { securityManager.getObfuscatedKeySeed() } returns ""

        // Act
        val result = securityManager.performSecurityCheck()

        // Assert
        assertTrue("Device should be compromised", result.isDeviceCompromised)
        assertTrue("Debugging should be detected", result.isDebuggingDetected)
        assertFalse("Should not have valid key seed", result.hasValidKeySeed)
        assertEquals("Security level should be HIGH_RISK", SecurityLevel.HIGH_RISK, result.securityLevel)
    }

    @Test
    fun test_securityManager_callsNativeMethods() {
        // Arrange
        every { securityManager.isDeviceCompromised() } returns false
        every { securityManager.isDebuggingDetected() } returns false
        every { securityManager.getObfuscatedKeySeed() } returns "test_seed"

        // Act
        securityManager.performSecurityCheck()

        // Assert - verify all native methods are called
        verify(exactly = 1) { securityManager.isDeviceCompromised() }
        verify(exactly = 1) { securityManager.isDebuggingDetected() }
        verify(exactly = 1) { securityManager.getObfuscatedKeySeed() }
    }

    @Test
    fun test_securityLevel_enum_hasAllValues() {
        // Assert all expected security levels exist
        val levels = SecurityLevel.entries.toTypedArray()
        assertTrue("Should contain SECURE level", levels.contains(SecurityLevel.SECURE))
        assertTrue("Should contain MEDIUM_RISK level", levels.contains(SecurityLevel.MEDIUM_RISK))
        assertTrue("Should contain HIGH_RISK level", levels.contains(SecurityLevel.HIGH_RISK))
        assertEquals("Should have exactly 3 security levels", 3, levels.size)
    }

    @Test
    fun test_securityResult_dataClass_properties() {
        // Arrange
        val result = SecurityResult(
            isDeviceCompromised = true,
            isDebuggingDetected = false,
            hasValidKeySeed = true,
            securityLevel = SecurityLevel.HIGH_RISK
        )

        // Assert data class properties
        assertTrue("isDeviceCompromised should be true", result.isDeviceCompromised)
        assertFalse("isDebuggingDetected should be false", result.isDebuggingDetected)
        assertTrue("hasValidKeySeed should be true", result.hasValidKeySeed)
        assertEquals("securityLevel should be HIGH_RISK", SecurityLevel.HIGH_RISK, result.securityLevel)
    }
}
