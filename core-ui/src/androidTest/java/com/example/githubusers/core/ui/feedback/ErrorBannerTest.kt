package com.example.githubusers.core.ui.feedback

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ErrorBannerTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun shows_message_for_network_and_can_dismiss() {
composeRule.setContent {
            ErrorBanner(message = "Network error. Check your connection and try again.", onRetry = {})
        }
        composeRule.onNodeWithContentDescription("Error banner").assertIsDisplayed()
        composeRule.onNodeWithText("Network error. Check your connection and try again.").assertIsDisplayed()
// Dismiss icon should be present via contentDescription
        composeRule.onNodeWithContentDescription("Dismiss").assertIsDisplayed()
    }

    @Test
    fun shows_retry_button() {
composeRule.setContent {
            ErrorBanner(message = "Request timed out. Please retry.", onRetry = {})
        }
        composeRule.onNodeWithText("Retry").assertIsDisplayed()
    }
}