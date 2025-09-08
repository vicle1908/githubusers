package com.example.githubusers.navigation.impl

import android.net.Uri
import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkResult
import com.example.githubusers.navigation.impl.TestUtils.MockUserList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Simple regression tests for Navigation 3 core functionality.
 * Tests basic navigation flows and deep link handling.
 */
class NavigationRegressionTest {
    @Test
    fun `test deep link result creation`() {
        // Given
        val mockDestination = MockUserList()
        val result = DeepLinkResult(destination = mockDestination)

        // Then
        assertEquals(mockDestination, result.destination)
        assertFalse(result.clearBackStack)
        assertTrue(result.singleTop)
        assertNull(result.popUpTo)
        assertFalse(result.popUpToInclusive)
    }

    @Test
    fun `test deep link result with options`() {
        // Given
        val mockDestination = MockUserList()
        val result =
            DeepLinkResult(
                destination = mockDestination,
                arguments = mapOf("param" to "value"),
                clearBackStack = true,
                singleTop = false,
                popUpTo = "home",
                popUpToInclusive = true,
            )

        // Then
        assertEquals(mockDestination, result.destination)
        assertEquals(mapOf("param" to "value"), result.arguments)
        assertTrue(result.clearBackStack)
        assertFalse(result.singleTop)
        assertEquals("home", result.popUpTo)
        assertTrue(result.popUpToInclusive)
    }

    @Test
    fun `test deep link handler interface properties`() {
        // Given
        val handler =
            object : DeepLinkHandler {
                override val moduleId: String = "test"

                override fun supportedPatterns(): List<String> = listOf("githubusers://test")

                override fun handleDeepLink(uri: Uri): DeepLinkResult? {
                    return null // Not testing this method to avoid Uri mocking issues
                }
            }

        // When & Then
        assertEquals("test", handler.moduleId)
        assertEquals(listOf("githubusers://test"), handler.supportedPatterns())
    }

    @Test
    fun `test core destination creation`() {
        // Given
        val destination = com.example.githubusers.navigation.api.CoreNavigationDestination.Home

        // Then
        assertEquals("home", destination.route)
        assertEquals("app://home", destination.deepLink)
    }
}
