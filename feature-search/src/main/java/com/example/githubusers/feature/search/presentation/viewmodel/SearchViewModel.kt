package com.example.githubusers.feature.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.githubusers.feature.search.domain.entity.SearchFilter
import com.example.githubusers.feature.search.domain.entity.SearchResult
import com.example.githubusers.feature.search.domain.usecase.GetTrendingUsersUseCase
import com.example.githubusers.feature.search.domain.usecase.ManageSearchHistoryUseCase
import com.example.githubusers.feature.search.domain.usecase.SearchUseCase
import com.example.githubusers.feature.search.presentation.intent.SearchIntent
import com.example.githubusers.feature.search.presentation.state.SearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    ) : ViewModel() {
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
            }.debounce(500)
                .flatMapLatest { (query, filter) ->
                    if (query.isNotBlank() && query.length >= 2) {
                        searchUseCase(query, filter)
                    } else {
                        flowOf()
                    }
                }.cachedIn(viewModelScope)

        /**
         * Flow of trending users
         */
        val trendingUsers =
            trendingUsersUseCase()
                .cachedIn(viewModelScope)

        init {
            loadRecentSearches()
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
            }
        }

        private fun updateQuery(query: String) {
            _state.update { it.copy(query = query) }
            searchQueryInternal.value = query
        }

        private fun executeSearch(query: String) {
            if (query.isNotBlank()) {
                viewModelScope.launch {
                    searchHistoryUseCase.saveSearch(query)
                    loadRecentSearches()
                }
                _state.update {
                    it.copy(
                        query = query,
                        isSearchActive = false,
                        showTrending = false,
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
                    showTrending = true,
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

        private fun handleUserClick(user: SearchResult) {
            // Navigation handled by UI layer
        }

        private fun refreshTrending() {
            // Trending users will auto-refresh through the flow
        }

        private fun loadRecentSearches() {
            viewModelScope.launch {
                val recentSearches = searchHistoryUseCase.getRecentSearches()
                _state.update { it.copy(recentSearches = recentSearches) }
            }
        }
    }
