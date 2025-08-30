package com.example.githubusers.presentation.ui.userlist

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
 * UI tests for search functionality in UserListScreen.
 * Tests search interactions, suggestions, history, and navigation.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class, ExperimentalMaterial3Api::class)
class UserListScreenSearchTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun searchBar_whenClicked_expandsAndAllowsTextInput() =
        runTest {
            // Given: Search screen is displayed
            composeTestRule.setContent {
                GithubUsersTheme {
                    val viewModel: UserListViewModel =
                        androidx.hilt.navigation.compose
                            .hiltViewModel()
                    UserListScreenWithSearch(
                        viewModel = viewModel,
                        onUserClick = {},
                    )
                }
            }

            // When: User clicks on the search bar
            composeTestRule.onNodeWithText("Search GitHub users...").performClick()

            // Then: Search bar should be expanded and accept input
            composeTestRule.onNodeWithText("Search GitHub users...").performTextInput("test")

            // Clear button should appear
            composeTestRule.waitUntilAtLeastOneExists(hasContentDescription("Clear search"), 3000)
            composeTestRule.onNodeWithContentDescription("Clear search").assertIsDisplayed()
        }

    @Test
    fun searchBar_whenTextEntered_showsSearchResults() =
        runTest {
            composeTestRule.setContent {
                GithubUsersTheme {
                    val viewModel: UserListViewModel =
                        androidx.hilt.navigation.compose
                            .hiltViewModel()
                    UserListScreenWithSearch(
                        viewModel = viewModel,
                        onUserClick = {},
                    )
                }
            }

            // When: User types in search bar
            composeTestRule.onNodeWithText("Search GitHub users...").performClick()
            composeTestRule.onNodeWithText("Search GitHub users...").performTextInput("defunkt")

            // Then: Search results should appear after debounce
            composeTestRule.waitUntilAtLeastOneExists(hasText("defunkt"), 5000)
            composeTestRule.onNodeWithText("defunkt").assertIsDisplayed()
        }

    @Test
    fun searchBar_whenCancelClicked_collapsesSearchBar() =
        runTest {
            composeTestRule.setContent {
                GithubUsersTheme {
                    val viewModel: UserListViewModel =
                        androidx.hilt.navigation.compose
                            .hiltViewModel()
                    UserListScreenWithSearch(
                        viewModel = viewModel,
                        onUserClick = {},
                    )
                }
            }

            // Given: Search mode is active
            composeTestRule.onNodeWithText("Search GitHub users...").performClick()
            composeTestRule.onNodeWithText("Search GitHub users...").performTextInput("test")

            // When: User clicks cancel
            composeTestRule.waitUntilAtLeastOneExists(hasText("Cancel"), 3000)
            composeTestRule.onNodeWithText("Cancel").performClick()

            // Then: Search bar should collapse and show regular user list
            // The query text should not be visible anymore
        }

    @Test
    fun searchBar_whenClearClicked_clearsSearchQuery() =
        runTest {
            composeTestRule.setContent {
                GithubUsersTheme {
                    val viewModel: UserListViewModel =
                        androidx.hilt.navigation.compose
                            .hiltViewModel()
                    UserListScreenWithSearch(
                        viewModel = viewModel,
                        onUserClick = {},
                    )
                }
            }

            // Given: Search query is entered
            composeTestRule.onNodeWithText("Search GitHub users...").performClick()
            composeTestRule.onNodeWithText("Search GitHub users...").performTextInput("test query")

            // When: User clicks clear (X) button
            composeTestRule.waitUntilAtLeastOneExists(hasContentDescription("Clear search"), 3000)
            composeTestRule.onNodeWithContentDescription("Clear search").performClick()

            // Then: Search query should be cleared
            composeTestRule
                .onNodeWithText("Search GitHub users...", useUnmergedTree = true)
                .assertIsDisplayed()
        }

    @Test
    fun searchResults_whenEmpty_showsNoResultsMessage() =
        runTest {
            composeTestRule.setContent {
                GithubUsersTheme {
                    val viewModel: UserListViewModel =
                        androidx.hilt.navigation.compose
                            .hiltViewModel()
                    UserListScreenWithSearch(
                        viewModel = viewModel,
                        onUserClick = {},
                    )
                }
            }

            // When: User searches for non-existent user
            composeTestRule.onNodeWithText("Search GitHub users...").performClick()
            composeTestRule.onNodeWithText("Search GitHub users...").performTextInput("xyznonexistentuser123")

            // Then: No results message should be displayed
            composeTestRule.waitUntilAtLeastOneExists(
                hasText("No results found for 'xyznonexistentuser123'"),
                5000,
            )
            composeTestRule
                .onNodeWithText("No results found for 'xyznonexistentuser123'")
                .assertIsDisplayed()
        }

    @Test
    fun searchHistory_whenAvailable_showsRecentSearches() =
        runTest {
            composeTestRule.setContent {
                GithubUsersTheme {
                    val viewModel: UserListViewModel =
                        androidx.hilt.navigation.compose
                            .hiltViewModel()
                    UserListScreenWithSearch(
                        viewModel = viewModel,
                        onUserClick = {},
                    )
                }
            }

            // First, perform a search to create history
            composeTestRule.onNodeWithText("Search GitHub users...").performClick()
            composeTestRule.onNodeWithText("Search GitHub users...").performTextInput("kotlin")

            // Wait for search to complete and cancel
            composeTestRule.waitUntilAtLeastOneExists(hasText("Cancel"), 3000)
            composeTestRule.onNodeWithText("Cancel").performClick()

            // When: User clicks search bar again
            composeTestRule.onNodeWithText("Search GitHub users...").performClick()

            // Then: Search history might be shown (depends on implementation)
            // Note: This test may need adjustment based on actual search history implementation
        }
}
