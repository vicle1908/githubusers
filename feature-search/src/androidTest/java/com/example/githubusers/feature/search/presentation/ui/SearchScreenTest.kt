package com.example.githubusers.feature.search.presentation.ui

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.githubusers.core.analytics.AnalyticsEvent
import com.example.githubusers.core.analytics.AnalyticsFacade
import com.example.githubusers.core.search.domain.SearchDomain
import com.example.githubusers.core.search.domain.SearchFilter
import com.example.githubusers.core.search.domain.SearchRepository
import com.example.githubusers.core.search.domain.SearchResult
import com.example.githubusers.core.search.domain.SearchResultType
import com.example.githubusers.feature.search.domain.usecase.GetTrendingUsersUseCase
import com.example.githubusers.feature.search.domain.usecase.ManageSearchHistoryUseCase
import com.example.githubusers.feature.search.domain.usecase.SearchUseCase
import com.example.githubusers.feature.search.presentation.viewmodel.SearchViewModel
import kotlin.time.Duration.Companion.seconds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private val SAMPLE_USER_RESULT = SearchResult(
    id = 1L,
    login = "octocat",
    name = "The Octocat",
    avatarUrl = "https://avatars.githubusercontent.com/u/1?v=4",
    type = SearchResultType.USER,
    score = 1f
)

private val SAMPLE_REPOSITORY_RESULT = SearchResult(
    id = 2L,
    login = "octocat",
    name = "Hello-World",
    avatarUrl = null,
    type = SearchResultType.REPOSITORY,
    score = 1f,
    repositoryFullName = "octocat/Hello-World",
    repositoryOwnerLogin = "octocat",
    repositoryDescription = "Demo repository",
    repositoryHtmlUrl = "https://github.com/octocat/Hello-World",
    stargazersCount = 42,
    primaryLanguage = "Kotlin",
    forksCount = 5,
    openIssuesCount = 1
)

@RunWith(AndroidJUnit4::class)
class SearchScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var repository: FakeSearchRepository
    private lateinit var analytics: RecordingAnalytics
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        repository = FakeSearchRepository(
            initialUserResults = listOf(SAMPLE_USER_RESULT),
            initialRepositoryResults = listOf(SAMPLE_REPOSITORY_RESULT),
            initialTrendingResults = listOf(SAMPLE_USER_RESULT)
        )
        analytics = RecordingAnalytics()
        viewModel = createViewModel()
    }

    @Test
    fun submittingQuery_updatesHistoryAndAnalytics() {
        composeRule.setSearchContent()

        composeRule.waitUntilDisplayedByContent("search_field")
        composeRule.onNodeWithContentDescription("search_field")
            .performTextInput("octocat")
        composeRule.onNodeWithContentDescription("Submit search")
            .performClick()

        composeRule.waitUntilCondition { repository.savedQueries.contains("octocat") }

        assertTrue(repository.savedQueries.contains("octocat"))
        assertTrue(analytics.events.any { (name, payload) ->
            name == "query_submitted" && payload["query"] == "octocat"
        })
        assertEquals(false, viewModel.state.value.showTrending)
    }

    @Test
    fun switchingDomain_requestsRepositoryResults() {
        composeRule.setSearchContent()

        composeRule.waitUntilDisplayedByContent("search_field")
        composeRule.onNodeWithContentDescription("search_field")
            .performTextInput("ktor")
        composeRule.onNodeWithContentDescription("Submit search")
            .performImeAction()

        composeRule.waitUntilCondition { repository.savedQueries.contains("ktor") }

        composeRule.onNodeWithText("Repositories")
            .assertIsDisplayed()
            .performClick()

        composeRule.waitUntilCondition { repository.lastRepositoryQuery == "ktor" }
        assertEquals("ktor", repository.lastRepositoryQuery)
    }

    @Test
    fun selectingRecentSearch_replaysQueryAndAnalytics() {
        repository = FakeSearchRepository(
            initialUserResults = listOf(SAMPLE_USER_RESULT),
            initialRepositoryResults = listOf(SAMPLE_REPOSITORY_RESULT),
            initialTrendingResults = listOf(SAMPLE_USER_RESULT),
            initialRecentSearches = mutableListOf("rocket")
        )
        analytics = RecordingAnalytics()
        viewModel = createViewModel()

        composeRule.setSearchContent()

        composeRule.waitUntilDisplayedByContent("search_field")
        composeRule.onNodeWithContentDescription("search_field").performClick()
        composeRule.waitUntilHasText("rocket")

        composeRule.onNodeWithText("rocket")
            .assertIsDisplayed()
            .performClick()

        composeRule.waitUntilCondition { viewModel.state.value.query == "rocket" }
        assertTrue(analytics.events.any { (name, payload) ->
            name == "query_submitted" && payload["query"] == "rocket"
        })
    }

    private fun createViewModel(): SearchViewModel =
        SearchViewModel(
            searchUseCase = SearchUseCase(repository),
            searchHistoryUseCase = ManageSearchHistoryUseCase(repository),
            trendingUsersUseCase = GetTrendingUsersUseCase(repository),
            analytics = analytics
        )

    private fun ComposeContentTestRule.setSearchContent(
        onNavigateToUser: (String) -> Unit = {},
        onNavigateToRepository: (String, String) -> Unit = { _, _ -> },
        onNavigateBack: () -> Unit = {}
    ) {
        setContent {
            val state = viewModel.state.collectAsState()
            val listUiState = viewModel.listUiState.collectAsState()
            val userResults = viewModel.userResults.collectAsLazyPagingItems()
            val repositoryResults = viewModel.repositoryResults.collectAsLazyPagingItems()
            val trendingUsers = viewModel.trendingUsers.collectAsLazyPagingItems()

            SearchScreen(
                state = state.value,
                listUiState = listUiState.value,
                userResults = userResults,
                repositoryResults = repositoryResults,
                trendingUsers = trendingUsers,
                onIntent = viewModel::processIntent,
                onNavigateToUser = onNavigateToUser,
                onNavigateToRepository = onNavigateToRepository,
                onNavigateBack = onNavigateBack
            )
        }
    }

    private fun ComposeContentTestRule.waitUntilDisplayedByContent(description: String) {
        waitUntilCondition {
            try {
                onAllNodesWithContentDescription(description).fetchSemanticsNodes().isNotEmpty()
            } catch (_: AssertionError) {
                false
            }
        }
    }

    private fun ComposeContentTestRule.waitUntilHasText(text: String) {
        waitUntilCondition {
            try {
                onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
            } catch (_: AssertionError) {
                false
            }
        }
    }

    private fun ComposeContentTestRule.waitUntilCondition(condition: () -> Boolean) {
        val timeoutMillis = 2.seconds.inWholeMilliseconds
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeoutMillis) {
            try {
                if (condition()) return
            } catch (_: AssertionError) {
                // Ignore and retry until timeout.
            }
            waitForIdle()
        }
        throw AssertionError("Condition not met within ${'$'}timeoutMillis ms")
    }
}

private class FakeSearchRepository(
    initialUserResults: List<SearchResult> = emptyList(),
    initialRepositoryResults: List<SearchResult> = emptyList(),
    initialTrendingResults: List<SearchResult> = emptyList(),
    initialRecentSearches: MutableList<String> = mutableListOf()
) : SearchRepository {

    private val usersFlow = MutableStateFlow(PagingData.from(initialUserResults))
    private val repositoriesFlow = MutableStateFlow(PagingData.from(initialRepositoryResults))
    private val trendingFlow = MutableStateFlow(PagingData.from(initialTrendingResults))

    val savedQueries = mutableListOf<String>()
    var lastUsersQuery: String? = null
        private set
    var lastRepositoryQuery: String? = null
        private set

    var recentSearches: MutableList<String> = initialRecentSearches

    fun setUserResults(results: List<SearchResult>) {
        usersFlow.value = PagingData.from(results)
    }

    fun setRepositoryResults(results: List<SearchResult>) {
        repositoriesFlow.value = PagingData.from(results)
    }

    fun setTrendingUsers(results: List<SearchResult>) {
        trendingFlow.value = PagingData.from(results)
    }

    override fun searchUsers(query: String, filter: SearchFilter): Flow<PagingData<SearchResult>> {
        lastUsersQuery = query
        return usersFlow
    }

    override fun searchRepositories(query: String, filter: SearchFilter): Flow<PagingData<SearchResult>> {
        lastRepositoryQuery = query
        return repositoriesFlow
    }

    override suspend fun getRecentSearches(): List<String> = recentSearches.toList()

    override suspend fun saveSearchQuery(query: String) {
        savedQueries += query
        recentSearches.remove(query)
        recentSearches.add(0, query)
    }

    override suspend fun clearSearchHistory() {
        recentSearches.clear()
    }

    override fun getTrendingUsers(): Flow<PagingData<SearchResult>> = trendingFlow.asStateFlow()
}

private class RecordingAnalytics : AnalyticsFacade {
    private val _events = mutableListOf<Pair<String, Map<String, Any?>>>()
    val events: List<Pair<String, Map<String, Any?>>> get() = _events

    override fun track(event: AnalyticsEvent) {
        _events += event.name to event.payload
    }
}
