package com.example.githubusers.core.ui.feedback

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.githubusers.core.search.domain.SearchError
import com.example.githubusers.core.search.domain.SearchException
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ErrorBannerTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun shows_message_for_network_and_can_dismiss() {
        val throwable = SearchException(SearchError.Network)
        composeRule.setContent {
            ErrorBanner(throwable = throwable, onRetry = {})
        }
        composeRule.onNodeWithContentDescription("Error banner").assertIsDisplayed()
        composeRule.onNodeWithText("Network error. Check your connection and try again.").assertIsDisplayed()
// Dismiss icon should be present via contentDescription
        composeRule.onNodeWithContentDescription("Dismiss").assertIsDisplayed()
    }

    @Test
    fun shows_retry_button() {
        val throwable = SearchException(SearchError.Timeout)
        composeRule.setContent {
            ErrorBanner(throwable = throwable, onRetry = {})
        }
        composeRule.onNodeWithText("Retry").assertIsDisplayed()
    }
}