package com.example.githubusers.feature.search.presentation.state

import com.example.githubusers.feature.search.domain.entity.SearchFilter

/**
 * UI state for the search screen
 */
data class SearchState(
    val query: String = "",
    val isSearchActive: Boolean = false,
    val isLoading: Boolean = false,
    val recentSearches: List<String> = emptyList(),
    val currentFilter: SearchFilter = SearchFilter(),
    val isFilterExpanded: Boolean = false,
    val showTrending: Boolean = true,
    val errorMessage: String? = null,
)
