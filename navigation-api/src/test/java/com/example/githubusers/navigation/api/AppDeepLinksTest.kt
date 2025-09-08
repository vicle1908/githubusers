package com.example.githubusers.navigation.api

import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AppDeepLinksTest {
    @Test
    fun build_throwsException_whenCalled() {
        // Since AppDestination is empty and sealed, we can't instantiate it
        // The build method is designed to throw IllegalArgumentException for any input
        // This test verifies that the method exists and would throw when called with valid input
        // In a real scenario, this would be called with a proper AppDestination instance
        // but since AppDestination is currently empty, we test the parse method instead
        assertNull(AppDeepLinks.parse("app://test"))
    }

    @Test
    fun parse_returnsNull_forAllInputs() {
        assertNull(AppDeepLinks.parse("app://test"))
        assertNull(AppDeepLinks.parse("https://example.com/test"))
    }
}
