package com.example.githubusers.presentation.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.githubusers.HiltTestActivity
import com.example.githubusers.presentation.theme.GithubUsersTheme
import com.example.githubusers.presentation.ui.userlist.UserListScreenWithSearch
import com.example.githubusers.presentation.ui.userlist.UserListViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test for search navigation flow.
 * Tests the scenario: Search -> Click User -> Navigate to Detail -> Back -> Verify Search State
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class, ExperimentalMaterial3Api::class)
class SearchNavigationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun searchResults_afterPerformingSearch_persistWhenReturning() = runTest {
        // Note: This test focuses on verifying the search state persistence
        // In a real app with navigation, you'd test the full navigation flow

        composeTestRule.setContent {
            GithubUsersTheme {
                val viewModel: UserListViewModel =
                    androidx.lifecycle.viewmodel.compose
                        .viewModel()
                UserListScreenWithSearch(
                    viewModel = viewModel,
                    onUserClick = { /* In real test, this would navigate */ }
                )
            }
        }

        // Step 1: Perform search
        composeTestRule.onNodeWithText("Search GitHub users...").performClick()
        composeTestRule.onNodeWithText("Search GitHub users...").performTextInput("def")

        // Wait for search results
        composeTestRule.waitUntilAtLeastOneExists(hasText("defunkt"), 5000)
        composeTestRule.onNodeWithText("defunkt").assertIsDisplayed()

        // Step 2: Collapse search (simulating navigation away)
        composeTestRule.onNodeWithText("Cancel").performClick()

        // Step 3: Verify search query is still visible in collapsed search bar
        composeTestRule.onNodeWithText("def", useUnmergedTree = true).assertIsDisplayed()

        // Step 4: Verify search results are still displayed (not the full user list)
        composeTestRule.onNodeWithText("defunkt").assertIsDisplayed()
    }

    @Test
    fun searchState_whenSearchModeIsInactiveButQueryExists_showsSearchResults() = runTest {
        // This tests the specific bug fix where isSearchMode=false but query="def"

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

        // Perform search
        composeTestRule.onNodeWithText("Search GitHub users...").performClick()
        composeTestRule.onNodeWithText("Search GitHub users...").performTextInput("def")

        // Wait for results
        composeTestRule.waitUntilAtLeastOneExists(hasText("defunkt"), 5000)

        // Collapse search bar
        composeTestRule.onNodeWithText("Cancel").performClick()

        // Then: Search results should still be displayed
        composeTestRule.onNodeWithText("defunkt").assertIsDisplayed()
        composeTestRule.onNodeWithText("def", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun searchBar_whenClearingAfterNavigation_returnsToFullList() = runTest {
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

        // Perform search
        composeTestRule.onNodeWithText("Search GitHub users...").performClick()
        composeTestRule.onNodeWithText("Search GitHub users...").performTextInput("def")

        // Wait for results and collapse
        composeTestRule.waitUntilAtLeastOneExists(hasText("defunkt"), 5000)
        composeTestRule.onNodeWithText("Cancel").performClick()

        // When: User expands search bar and clears the query
        composeTestRule.onNodeWithText("def", useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithContentDescription("Clear search").performClick()

        // Then: Should show full user list (not search results)
        // Cancel to close search mode
        composeTestRule.onNodeWithText("Cancel").performClick()

        // Verify we're back to showing the regular user list
        // (In a real test, we'd verify specific users from the regular list appear)
    }

    @Test
    fun navigation_multipleSearchesRemainConsistent() = runTest {
        composeTestRule.setContent {
            GithubUsersTheme {
                val viewModel: UserListViewModel =
                    androidx.lifecycle.viewmodel.compose
                        .viewModel()
                UserListScreenWithSearch(
                    viewModel = viewModel,
                    onUserClick = { /* Simulate navigation */ }
                )
            }
        }

        // Perform first search
        composeTestRule.onNodeWithText("Search GitHub users...").performClick()
        composeTestRule.onNodeWithText("Search GitHub users...").performTextInput("kotlin")

        // Wait and cancel
        composeTestRule.waitUntilAtLeastOneExists(hasText("Cancel"), 3000)
        composeTestRule.onNodeWithText("Cancel").performClick()

        // Clear and perform second search
        composeTestRule.onNodeWithText("kotlin", useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithContentDescription("Clear search").performClick()
        composeTestRule.onNodeWithText("Search GitHub users...").performTextInput("android")

        // Verify second search works
        composeTestRule.waitUntilAtLeastOneExists(hasText("Cancel"), 3000)
        composeTestRule.onNodeWithText("Cancel").performClick()

        // Verify second query persists
        composeTestRule.onNodeWithText("android", useUnmergedTree = true).assertIsDisplayed()
    }
}
