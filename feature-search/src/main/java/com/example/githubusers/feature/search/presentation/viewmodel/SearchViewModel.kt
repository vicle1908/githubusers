package com.example.githubusers.feature.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.githubusers.core.analytics.AnalyticsEvent
import com.example.githubusers.core.analytics.AnalyticsFacade
import com.example.githubusers.core.paging.DefaultPagingSourceProvider
import com.example.githubusers.core.search.PagingSearchController
import com.example.githubusers.core.search.SearchQueryNormalizer
import com.example.githubusers.core.search.SearchUiState
import com.example.githubusers.core.search.domain.SearchDomain
import com.example.githubusers.core.search.domain.SearchFilter
import com.example.githubusers.core.search.domain.SearchResult
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.repository.domain.model.Repository
import com.example.githubusers.feature.search.data.mapper.toRepositoryModel
import com.example.githubusers.feature.search.data.mapper.toUserSummary
import com.example.githubusers.feature.search.domain.usecase.GetTrendingUsersUseCase
import com.example.githubusers.feature.search.domain.usecase.ManageSearchHistoryUseCase
import com.example.githubusers.feature.search.domain.usecase.SearchUseCase
import com.example.githubusers.feature.search.presentation.intent.SearchIntent
import com.example.githubusers.feature.search.presentation.state.SearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import timber.log.Timber

/**
 * ViewModel for the search screen.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val searchHistoryUseCase: ManageSearchHistoryUseCase,
    private val trendingUsersUseCase: GetTrendingUsersUseCase,
    private val analytics: AnalyticsFacade
) : ViewModel() {

    companion object {
        private const val TAG = "SearchViewModel"
        private const val MIN_QUERY_LENGTH = 2
    }

    private var trendingShownLogged: Boolean = false

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val currentFilterInternal = MutableStateFlow(SearchFilter())

    private val userPagingProvider = DefaultPagingSourceProvider(
        scope = viewModelScope,
        initialQuery = "",
        pagerFactory = { query -> userSearchFlow(query) },
        debounceMillis = 200L
    )

    private val repositoryPagingProvider = DefaultPagingSourceProvider(
        scope = viewModelScope,
        initialQuery = "",
        pagerFactory = { query -> repositorySearchFlow(query) },
        debounceMillis = 200L
    )

    private val userSearchController = PagingSearchController(
        scope = viewModelScope,
        queryState = userPagingProvider.query,
        resultsSource = userPagingProvider.pagingData,
        updateQuery = userPagingProvider::updateQuery,
        queryTransformer = { SearchQueryNormalizer.normalize(it).original },
        minQueryLength = MIN_QUERY_LENGTH
    )

    private val repositorySearchController = PagingSearchController(
        scope = viewModelScope,
        queryState = repositoryPagingProvider.query,
        resultsSource = repositoryPagingProvider.pagingData,
        updateQuery = repositoryPagingProvider::updateQuery,
        queryTransformer = { SearchQueryNormalizer.normalize(it).original },
        minQueryLength = MIN_QUERY_LENGTH
    )

    val userResults: Flow<PagingData<UserSummary>> = userSearchController.results

    val repositoryResults: Flow<PagingData<Repository>> = repositorySearchController.results

    val trendingUsers: Flow<PagingData<UserSummary>> =
        trendingUsersUseCase()
            .map { pagingData: PagingData<SearchResult> ->
                pagingData.map { result -> result.toUserSummary() }
            }
            .cachedIn(viewModelScope)

    val listUiState: StateFlow<SearchUiState> =
        state
            .flatMapLatest { stateSnapshot ->
                when (stateSnapshot.activeDomain) {
                    SearchDomain.USERS -> userSearchController.uiState
                    SearchDomain.REPOSITORIES -> repositorySearchController.uiState
                }
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, SearchUiState())

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

        if (initialFilter != null) {
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
            is SearchIntent.SwitchDomain -> switchDomain(intent.domain)
            is SearchIntent.UserClicked -> handleUserClick(intent.user)
            is SearchIntent.RepositoryClicked -> handleRepositoryClick(intent.repository)
            SearchIntent.RefreshTrending -> refreshTrending()
            SearchIntent.BackToBrowse -> backToBrowse()
            SearchIntent.TrendingShown -> onTrendingShown()
            SearchIntent.RetryActiveDomain -> retryActiveDomain()
        }
    }

    private fun updateQuery(query: String) {
        val activeDomain = state.value.activeDomain
        val shouldShowTrending = activeDomain == SearchDomain.USERS && query.isBlank()
        if (shouldShowTrending) {
            trendingShownLogged = false
        }
        _state.update {
            it.copy(
                query = query,
                showTrending = shouldShowTrending,
                isSearchActive = true
            )
        }
        controllerFor(activeDomain).submitQuery(query)
    }

    private fun executeSearch(rawQuery: String) {
        val query = rawQuery.trim()
        if (query.isEmpty()) {
            clearSearch()
            return
        }

        val domain = state.value.activeDomain
        val filterSnapshot = currentFilterInternal.value.copy(domain = domain)
        val endpoint =
            when (domain) {
                SearchDomain.REPOSITORIES -> "/search/repositories"
                SearchDomain.USERS -> "/search/users"
            }
        val analyticsPayload = mutableMapOf<String, Any>(
            "query" to query,
            "length" to query.length,
            "source" to "keyboard",
            "domain" to filterSnapshot.domain.name,
            "endpoint" to endpoint
        )
        state.value.origin?.let { analyticsPayload["origin"] = it }
        filterSnapshot.type?.let { analyticsPayload["filter_type"] = it.name }

        analytics.track(
            AnalyticsEvent(
                name = "query_submitted",
                payload = analyticsPayload
            )
        )

        viewModelScope.launch {
            searchHistoryUseCase.saveSearch(query)
            loadRecentSearches()
        }

        _state.update {
            it.copy(
                query = query,
                isSearchActive = false,
                showTrending = domain == SearchDomain.USERS && query.isBlank()
            )
        }

        controllerFor(domain).submitQuery(query)
    }

    private fun selectRecentSearch(query: String) {
        executeSearch(query)
    }

    private fun clearSearch() {
        val domain = state.value.activeDomain
        val shouldShowTrending = domain == SearchDomain.USERS
        trendingShownLogged = false
        _state.update {
            it.copy(
                query = "",
                isSearchActive = false,
                showTrending = shouldShowTrending
            )
        }
        controllerFor(domain).submitQuery("")
    }

    private fun clearSearchHistory() {
        viewModelScope.launch {
            searchHistoryUseCase.clearHistory()
            loadRecentSearches()
        }
    }

    private fun activateSearch() {
        analytics.track(
            AnalyticsEvent(
                name = "search_opened",
                payload = mapOf("source" to "users_menu")
            )
        )
        _state.update { it.copy(isSearchActive = true) }
    }

    private fun dismissSearch() {
        _state.update { it.copy(isSearchActive = false) }
    }

    private fun updateFilter(filter: SearchFilter) {
        currentFilterInternal.value = filter
        val shouldShowTrending =
            if (filter.domain == SearchDomain.USERS) _state.value.query.isBlank() else false
        if (filter.domain == SearchDomain.USERS) {
            trendingShownLogged = false
        }
        _state.update {
            it.copy(
                currentFilter = filter,
                activeDomain = filter.domain,
                showTrending = shouldShowTrending
            )
        }
        controllerFor(filter.domain).retry()
    }

    private fun toggleFilterExpanded() {
        _state.update { it.copy(isFilterExpanded = !it.isFilterExpanded) }
    }

    private fun switchDomain(domain: SearchDomain) {
        val currentState = state.value
        if (currentState.activeDomain == domain) return

        val shouldShowTrending = domain == SearchDomain.USERS && currentState.query.isBlank()
        if (domain == SearchDomain.USERS) {
            trendingShownLogged = false
        }
        currentFilterInternal.value = currentFilterInternal.value.copy(domain = domain)
        _state.update {
            it.copy(
                activeDomain = domain,
                currentFilter = it.currentFilter.copy(domain = domain),
                showTrending = shouldShowTrending
            )
        }
        controllerFor(domain).submitQuery(currentState.query)
    }

    private fun handleUserClick(user: UserSummary) {
        analytics.track(
            AnalyticsEvent(
                name = "result_clicked",
                payload = mapOf(
                    "user_id" to user.login,
                    "position" to 0,
                    "query" to state.value.query
                )
            )
        )
    }

    private fun handleRepositoryClick(repository: Repository) {
        analytics.track(
            AnalyticsEvent(
                name = "repository_clicked",
                payload = mapOf(
                    "repository" to (repository.fullName ?: repository.name),
                    "query" to state.value.query,
                    "origin" to state.value.origin
                )
            )
        )
    }

    private fun refreshTrending() {
        // No-op: the trending flow is hot and will refresh when collected.
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

    private fun retryActiveDomain() {
        when (state.value.activeDomain) {
            SearchDomain.USERS -> userSearchController.retry()
            SearchDomain.REPOSITORIES -> repositorySearchController.retry()
        }
    }

    private fun onTrendingShown() {
        if (!trendingShownLogged && state.value.activeDomain == SearchDomain.USERS) {
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

    private fun controllerFor(domain: SearchDomain): PagingSearchController<*> {
        return when (domain) {
            SearchDomain.USERS -> userSearchController
            SearchDomain.REPOSITORIES -> repositorySearchController
        }
    }

    private fun userSearchFlow(rawQuery: String): Flow<PagingData<UserSummary>> {
        val normalized = SearchQueryNormalizer.normalize(rawQuery)
        val query = normalized.original
        if (query.length < MIN_QUERY_LENGTH) {
            return flowOf(PagingData.empty())
        }
        val filterSnapshot = currentFilterInternal.value.copy(domain = SearchDomain.USERS)
        return searchUseCase(query, filterSnapshot)
            .map { pagingData -> pagingData.map { result -> result.toUserSummary() } }
    }

    private fun repositorySearchFlow(rawQuery: String): Flow<PagingData<Repository>> {
        val normalized = SearchQueryNormalizer.normalize(rawQuery)
        val query = normalized.original
        if (query.length < MIN_QUERY_LENGTH) {
            return flowOf(PagingData.empty())
        }
        val filterSnapshot = currentFilterInternal.value.copy(domain = SearchDomain.REPOSITORIES)
        return searchUseCase(query, filterSnapshot)
            .map { pagingData -> pagingData.map { result -> result.toRepositoryModel() } }
    }
}
