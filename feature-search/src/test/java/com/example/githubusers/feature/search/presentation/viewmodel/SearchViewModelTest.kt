package com.example.githubusers.feature.search.presentation.viewmodel

import androidx.paging.PagingData
import com.example.githubusers.core.search.domain.SearchFilter
import com.example.githubusers.core.search.domain.SearchRepository
import com.example.githubusers.core.search.domain.SearchResult
import com.example.githubusers.core.search.domain.SearchResultType
import com.example.githubusers.feature.search.domain.usecase.GetTrendingUsersUseCase
import com.example.githubusers.feature.search.domain.usecase.ManageSearchHistoryUseCase
import com.example.githubusers.feature.search.domain.usecase.SearchUseCase
import com.example.githubusers.feature.search.presentation.intent.SearchIntent
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeSearchRepository
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        repository = FakeSearchRepository()
        viewModel =
            SearchViewModel(
                searchUseCase = SearchUseCase(repository),
                searchHistoryUseCase = ManageSearchHistoryUseCase(repository),
                trendingUsersUseCase = GetTrendingUsersUseCase(repository)
            )
    }

    @Test
    fun `initial load surfaces recent searches`() = runTest {
        advanceUntilIdle()
        assertEquals(repository.recentSearchesFlow.value, viewModel.state.value.recentSearches)
        assertTrue(viewModel.state.value.showTrending)
    }

    @Test
    fun `execute search caches query and hides trending`() = runTest {
        viewModel.processIntent(SearchIntent.ExecuteSearch("octocat"))
        advanceUntilIdle()

        assertEquals(listOf("octocat"), repository.savedQueries)
        assertEquals("octocat", viewModel.state.value.query)
        assertTrue(!viewModel.state.value.showTrending)
    }

    @Test
    fun `clear search restores trending visibility`() = runTest {
        viewModel.processIntent(SearchIntent.ExecuteSearch("hub"))
        advanceUntilIdle()

        viewModel.processIntent(SearchIntent.ClearSearch)
        advanceUntilIdle()

        assertEquals("", viewModel.state.value.query)
        assertTrue(viewModel.state.value.showTrending)
    }

    @Test
    fun `clear history empties recent searches`() = runTest {
        advanceUntilIdle()

        viewModel.processIntent(SearchIntent.ClearSearchHistory)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.recentSearches.isEmpty())
    }

    private class FakeSearchRepository : SearchRepository {
        val recentSearchesFlow = MutableStateFlow(listOf("alice", "bob"))
        val savedQueries = mutableListOf<String>()

        override fun searchUsers(query: String, filter: SearchFilter): Flow<PagingData<SearchResult>> =
            flowOf(PagingData.empty())

        override suspend fun getRecentSearches(): List<String> = recentSearchesFlow.value

        override suspend fun saveSearchQuery(query: String) {
            savedQueries += query
            recentSearchesFlow.value = (listOf(query) + recentSearchesFlow.value).distinct()
        }

        override suspend fun clearSearchHistory() {
            recentSearchesFlow.value = emptyList()
        }

        override fun getTrendingUsers(): Flow<PagingData<SearchResult>> = flowOf(
            PagingData.from(
                listOf(
                    SearchResult(
                        id = 1L,
                        login = "alice",
                        name = null,
                        avatarUrl = "https://example.com/alice.png",
                        type = SearchResultType.USER,
                        score = 0f
                    )
                )
            )
        )
    }

    class MainDispatcherRule(private val dispatcher: TestDispatcher = StandardTestDispatcher()) : TestWatcher() {
        override fun starting(description: Description) {
            super.starting(description)
            Dispatchers.setMain(dispatcher)
        }

        override fun finished(description: Description) {
            super.finished(description)
            resetMain()
        }
    }
}
