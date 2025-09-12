package com.example.githubusers.presentation.ui.userlist

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.githubusers.HiltTestActivity
import com.example.githubusers.presentation.theme.GithubUsersTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Debug test to understand the actual UI elements present in search mode.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class, ExperimentalMaterial3Api::class)
class SearchUIDebugTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun debugSearchUI_printSemanticTree() = runTest {
        composeTestRule.setContent {
            GithubUsersTheme {
                val viewModel: UserListViewModel =
                    androidx.lifecycle.viewmodel.compose
                        .viewModel()
                UserListScreenWithSearch(
                    viewModel = viewModel,
                    onUserClick = {}
                )
            }
        }

        // Print initial state
        println("=== Initial State ===")
        composeTestRule.onRoot().printToLog("DEBUG_INITIAL")

        // Click search bar
        composeTestRule.onNodeWithText("Search GitHub users...").performClick()
        Thread.sleep(1000)

        println("\n=== After clicking search bar ===")
        composeTestRule.onRoot().printToLog("DEBUG_SEARCH_EXPANDED")

        // Type text
        composeTestRule.onNodeWithText("Search GitHub users...").performTextInput("test")
        Thread.sleep(1000)

        println("\n=== After typing text ===")
        composeTestRule.onRoot().printToLog("DEBUG_WITH_TEXT")
    }
}
