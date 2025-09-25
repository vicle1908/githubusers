package com.example.githubusers.feature.users.list.presentation.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.paging.PagingData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.githubusers.core.analytics.AnalyticsEvent
import com.example.githubusers.core.analytics.AnalyticsFacade
import com.example.githubusers.core.search.SearchQueryNormalizer
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.users.domain.model.UserDetail
import com.example.githubusers.feature.users.domain.repository.UserRepository
import com.example.githubusers.feature.users.list.domain.usecase.ObserveUserListUseCase
import com.example.githubusers.feature.users.list.presentation.analytics.DefaultUserListAnalytics
import com.example.githubusers.feature.users.list.presentation.analytics.UserListAnalytics
import com.example.githubusers.feature.users.list.presentation.viewmodel.UserListViewModel
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private val TEST_USER = UserSummary(
    id = 1L,
    login = "octocat",
    avatarUrl = "https://avatars.githubusercontent.com/u/1?v=4",
    htmlUrl = "https://github.com/octocat"
)

@RunWith(AndroidJUnit4::class)
class UserListScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var recordingAnalytics: RecordingAnalytics
    private lateinit var analytics: UserListAnalytics
    private lateinit var repository: FakeUserRepository

    @Before
    fun setUp() {
        recordingAnalytics = RecordingAnalytics()
        analytics = DefaultUserListAnalytics(recordingAnalytics)
        repository = FakeUserRepository()
    }

    @Test
    fun searchIcon_emitsAnalyticsAndCallback() {
        val viewModel = createViewModel()
        var invokedCount = 0
        var lastQuery: String? = "sentinel"

        composeRule.setContent {
            UserListScreen(
                viewModel = viewModel,
                onOpenSearch = { query ->
                    invokedCount += 1
                    lastQuery = query
                }
            )
        }

        composeRule.waitUntilDisplayed("Search GitHub users")
        val initialEvents = recordingAnalytics.events.size

        composeRule.onNodeWithContentDescription("Search GitHub users")
            .assertIsDisplayed()
            .performClick()
        composeRule.waitForIdle()

        assertEquals(1, invokedCount)
        assertNull(lastQuery)

        val events: List<Pair<String, Map<String, Any?>>> = recordingAnalytics.events
        assertEquals(initialEvents + 1, events.size)
        val (eventName, searchPayload) = events.last() as Pair<String, Map<String, Any?>>
        assertEquals("user_list_open_search", eventName)
        assertNull(searchPayload["query"])
    }

    @Test
    fun settingsIcon_emitsAnalyticsAndCallback() {
        val viewModel = createViewModel()
        var settingsInvoked = false

        composeRule.setContent {
            UserListScreen(
                viewModel = viewModel,
                onOpenSettings = { settingsInvoked = true }
            )
        }

        composeRule.waitUntilDisplayed("Open settings")
        val initialEvents = recordingAnalytics.events.size

        composeRule.onNodeWithContentDescription("Open settings")
            .assertIsDisplayed()
            .performClick()
        composeRule.waitForIdle()

        assertTrue(settingsInvoked)
        val (settingsName, _) = recordingAnalytics.events.last() as Pair<String, Map<String, Any?>>
        assertEquals(initialEvents + 1, recordingAnalytics.events.size)
        assertEquals("user_list_open_settings", settingsName)
    }

    @Test
    fun typingQuery_tracksAnalyticsWithLatestPayload() {
        val viewModel = createViewModel()

        composeRule.setContent {
            UserListScreen(viewModel = viewModel)
        }

        composeRule.waitUntilDisplayed("search_field")
        val initialEvents = recordingAnalytics.events.size

        composeRule.onNodeWithContentDescription("search_field")
            .performTextInput("octocat")
        composeRule.waitForIdle()

        val events: List<Pair<String, Map<String, Any?>>> = recordingAnalytics.events
        assertTrue(events.size > initialEvents)
        val queryEvent = events.asReversed().firstOrNull { it.first == "user_list_query_changed" }
            ?: fail("Expected user_list_query_changed event")
        val (_, queryPayload) = queryEvent as Pair<String, Map<String, Any?>>
        assertEquals("octocat", queryPayload["query"])
        assertEquals(false, queryPayload["is_blank"])
    }

    @Test
    fun clickingUserRow_notifiesHostAndAnalytics() {
        val viewModel = createViewModel()
        var selectedUser: UserSummary? = null

        composeRule.setContent {
            UserListScreen(
                viewModel = viewModel,
                onUserClick = { selectedUser = it }
            )
        }

        composeRule.waitUntil(
            timeoutMillis = 2.seconds.inWholeMilliseconds
        ) {
            composeRule.onAllNodesWithText(TEST_USER.login).fetchSemanticsNodes().isNotEmpty()
        }

        val initialEvents = recordingAnalytics.events.size

        composeRule.onNodeWithText(TEST_USER.login)
            .assertIsDisplayed()
            .performClick()
        composeRule.waitForIdle()

        assertEquals(TEST_USER.login, selectedUser?.login)
        val events: List<Pair<String, Map<String, Any?>>> = recordingAnalytics.events
        assertEquals(initialEvents + 1, events.size)
        val (selectName, selectPayload) = events.last() as Pair<String, Map<String, Any?>>
        assertEquals("user_list_user_selected", selectName)
        assertEquals(TEST_USER.login, selectPayload["login"])
    }

    private fun createViewModel(): UserListViewModel {
        val useCase = ObserveUserListUseCase(repository)
        return UserListViewModel(observeUserListUseCase = useCase, analytics = analytics)
    }

    private fun ComposeContentTestRule.waitUntilDisplayed(contentDescription: String) {
        waitUntil(timeoutMillis = 1.seconds.inWholeMilliseconds) {
            onAllNodesWithContentDescription(contentDescription).fetchSemanticsNodes().isNotEmpty()
        }
    }
}

private class FakeUserRepository : UserRepository {

    private val usersFlow = MutableStateFlow(PagingData.from(listOf(TEST_USER)))

    override fun observeUsers(query: SearchQueryNormalizer.NormalizedSearchQuery): Flow<PagingData<UserSummary>> =
        usersFlow.asStateFlow()

    override suspend fun fetchUserDetail(username: String): Result<UserDetail> =
        Result.failure(UnsupportedOperationException("Not used in tests"))

    fun updateUsers(users: List<UserSummary>) {
        usersFlow.update { PagingData.from(users) }
    }
}

private class RecordingAnalytics : AnalyticsFacade {
    private val _events = mutableListOf<Pair<String, Map<String, Any?>>>()
    val events: List<Pair<String, Map<String, Any?>>> get() = _events

    override fun track(event: AnalyticsEvent) {
        _events += event.name to event.payload
    }
}
