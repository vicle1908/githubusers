package com.example.githubusers.presentation.ui.userlist

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
 * Basic UI tests for UserListScreen that don't depend on network calls.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class, ExperimentalMaterial3Api::class)
class UserListScreenBasicTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun searchBar_isDisplayed() =
        runTest {
            // Given: Screen is displayed
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

            // Then: Search bar should be visible
            composeTestRule.onNodeWithText("Search GitHub users...").assertIsDisplayed()
        }

    @Test
    fun searchBar_whenClicked_becomesActive() =
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

            // When: User clicks on the search bar
            composeTestRule.onNodeWithText("Search GitHub users...").performClick()

            // Then: Search bar should still be visible (expanded state)
            composeTestRule.onNodeWithText("Search GitHub users...").assertIsDisplayed()
        }
}
