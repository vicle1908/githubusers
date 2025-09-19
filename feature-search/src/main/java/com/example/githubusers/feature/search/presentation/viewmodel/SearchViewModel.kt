package com.example.githubusers.feature.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.githubusers.core.analytics.AnalyticsEvent
import com.example.githubusers.core.analytics.AnalyticsFacade
import com.example.githubusers.core.search.domain.SearchFilter
import com.example.githubusers.core.search.domain.SearchResult
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.search.data.mapper.toUserSummary
import com.example.githubusers.feature.search.domain.usecase.GetTrendingUsersUseCase
import com.example.githubusers.feature.search.domain.usecase.ManageSearchHistoryUseCase
import com.example.githubusers.feature.search.domain.usecase.SearchUseCase
import com.example.githubusers.feature.search.presentation.intent.SearchIntent
import com.example.githubusers.feature.search.presentation.state.SearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for the search screen
 */
@HiltViewModel
class SearchViewModel
@Inject
constructor(
    private val searchUseCase: SearchUseCase,
    private val searchHistoryUseCase: ManageSearchHistoryUseCase,
    private val trendingUsersUseCase: GetTrendingUsersUseCase,
    private val analytics: AnalyticsFacade
) : ViewModel() {
    companion object {
        private const val TAG = "SearchViewModel"
        private const val DEBOUNCE_MS: Long = 500L
    }

    private var trendingShownLogged: Boolean = false

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val searchQueryInternal = MutableStateFlow("")
    private val currentFilterInternal = MutableStateFlow(SearchFilter())

    /**
     * Flow of paginated search results
     */
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults =
        combine(searchQueryInternal, currentFilterInternal) { query, filter ->
            Pair(query, filter)
        }.debounce(DEBOUNCE_MS)
            .flatMapLatest { (query, filter) ->
                if (query.isNotBlank() && query.length >= 2) {
                    searchUseCase(query, filter).map { pagingData: PagingData<SearchResult> ->
                        pagingData.map { result -> result.toUserSummary() }
                    }
                } else {
                    flowOf(PagingData.empty<UserSummary>())
                }
            }.cachedIn(viewModelScope)

    /**
     * Flow of trending users
     */
    val trendingUsers =
        trendingUsersUseCase()
            .map { pagingData: PagingData<SearchResult> ->
                pagingData.map { result -> result.toUserSummary() }
            }
            .cachedIn(viewModelScope)

    init {
        loadRecentSearches()
    }

    fun initialize(initialQuery: String?, initialFilter: SearchFilter?, origin: String?) {
        Timber.tag(TAG).d(
            "Initialize called with query=%s origin=%s filter=%s",
            initialQuery,
            origin,
            initialFilter
        )
        val currentState = state.value

        if (origin != null && currentState.origin != origin) {
            _state.update { it.copy(origin = origin) }
        }

        if (initialFilter != null && currentState.currentFilter != initialFilter) {
            updateFilter(initialFilter)
        }

        if (!initialQuery.isNullOrBlank() && currentState.query != initialQuery) {
            processIntent(SearchIntent.ExecuteSearch(initialQuery))
        }
    }

    fun processIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.UpdateQuery -> updateQuery(intent.query)
            is SearchIntent.ExecuteSearch -> executeSearch(intent.query)
            is SearchIntent.SelectRecentSearch -> selectRecentSearch(intent.query)
            SearchIntent.ClearSearch -> clearSearch()
            SearchIntent.ClearSearchHistory -> clearSearchHistory()
            SearchIntent.ActivateSearch -> activateSearch()
            SearchIntent.DismissSearch -> dismissSearch()
            is SearchIntent.UpdateFilter -> updateFilter(intent.filter)
            SearchIntent.ToggleFilterExpanded -> toggleFilterExpanded()
            is SearchIntent.UserClicked -> handleUserClick(intent.user)
            SearchIntent.RefreshTrending -> refreshTrending()
            SearchIntent.BackToBrowse -> backToBrowse()
            SearchIntent.TrendingShown -> onTrendingShown()
        }
    }

    private fun updateQuery(query: String) {
        _state.update { it.copy(query = query) }
        searchQueryInternal.value = query
    }

    private fun executeSearch(query: String) {
        // Analytics: query submitted
        analytics.track(
            AnalyticsEvent(
                name = "query_submitted",
                payload = mapOf(
                    "query" to query,
                    "length" to query.length,
                    "source" to "keyboard"
                )
            )
        )
        if (query.isNotBlank()) {
            viewModelScope.launch {
                searchHistoryUseCase.saveSearch(query)
                loadRecentSearches()
            }
            _state.update {
                it.copy(
                    query = query,
                    isSearchActive = false,
                    showTrending = false
                )
            }
            searchQueryInternal.value = query
        }
    }

    private fun selectRecentSearch(query: String) {
        executeSearch(query)
    }

    private fun clearSearch() {
        _state.update {
            it.copy(
                query = "",
                isSearchActive = false,
                showTrending = true
            )
        }
        searchQueryInternal.value = ""
    }

    private fun clearSearchHistory() {
        viewModelScope.launch {
            searchHistoryUseCase.clearHistory()
            loadRecentSearches()
        }
    }

    private fun activateSearch() {
        // Analytics: search opened (from UI activation)
        analytics.track(
            AnalyticsEvent(
                name = "search_opened",
                payload = mapOf(
                    "source" to "users_menu"
                )
            )
        )
        _state.update { it.copy(isSearchActive = true) }
    }

    private fun dismissSearch() {
        _state.update { it.copy(isSearchActive = false) }
    }

    private fun updateFilter(filter: SearchFilter) {
        _state.update { it.copy(currentFilter = filter) }
        currentFilterInternal.value = filter
    }

    private fun toggleFilterExpanded() {
        _state.update { it.copy(isFilterExpanded = !it.isFilterExpanded) }
    }

    private fun handleUserClick(user: UserSummary) {
        // Analytics: result clicked
        analytics.track(
            AnalyticsEvent(
                name = "result_clicked",
                payload = mapOf(
                    "user_id" to user.login,
                    "position" to 0, // Position wiring TBD at UI level
                    "query" to state.value.query
                )
            )
        )
        // Navigation handled by UI layer
    }

    private fun refreshTrending() {
        // Trending users will auto-refresh through the flow
    }

    private fun backToBrowse() {
        analytics.track(
            AnalyticsEvent(
                name = "back_to_browse",
                payload = mapOf(
                    "query" to state.value.query,
                    "origin" to state.value.origin
                )
            )
        )
    }

    private fun onTrendingShown() {
        if (!trendingShownLogged) {
            analytics.track(
                AnalyticsEvent(
                    name = "trending_shown",
                    payload = mapOf(
                        "recent_count" to state.value.recentSearches.size,
                        "origin" to state.value.origin
                    )
                )
            )
            trendingShownLogged = true
        }
    }

    private fun loadRecentSearches() {
        viewModelScope.launch {
            val recentSearches = searchHistoryUseCase.getRecentSearches()
            _state.update { it.copy(recentSearches = recentSearches) }
        }
    }
}
