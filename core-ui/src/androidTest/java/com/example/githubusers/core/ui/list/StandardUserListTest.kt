package com.example.githubusers.core.ui.list

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.githubusers.core.users.domain.UserSummary
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StandardUserListTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun rendersHeaderAndItems() {
        val users = listOf(fakeUser(1, "octocat"), fakeUser(2, "hubber"))

        composeRule.setContent {
            TestTheme {
                val lazyItems = remember { flowOf(PagingData.from(users)) }.collectAsLazyPagingItems()
                StandardUserList(
                    pagingItems = lazyItems,
                    header = { TextHeader("Header") },
                    itemContent = { user -> StandardUserRow(user = user, onClick = {}) }
                )
            }
        }

        composeRule.onNodeWithText("Header").assertIsDisplayed()
        composeRule.onNodeWithText("octocat").assertIsDisplayed()
        composeRule.onNodeWithText("hubber").assertIsDisplayed()
    }

    @Test
    fun showsEmptyContentWhenNoItems() {
        composeRule.setContent {
            TestTheme {
                val lazyItems = remember { flowOf(PagingData.empty<UserSummary>()) }.collectAsLazyPagingItems()
                StandardUserList(
                    pagingItems = lazyItems,
                    emptyContent = { TextHeader("Nothing here") }
                ) { user -> StandardUserRow(user = user, onClick = {}) }
            }
        }

        composeRule.onNodeWithText("Nothing here").assertIsDisplayed()
    }

    @Composable
    private fun TestTheme(content: @Composable () -> Unit) {
        MaterialTheme { content() }
    }

    @Composable
    private fun TextHeader(text: String) {
        androidx.compose.material3.Text(text = text)
    }

    private fun fakeUser(id: Int, login: String) = UserSummary(
        id = id.toLong(),
        login = login,
        avatarUrl = "https://avatars.githubusercontent.com/$login",
        htmlUrl = "https://github.com/$login"
    )
}
