package com.example.githubusers.feature.search.presentation.intent

import com.example.githubusers.feature.search.domain.entity.SearchFilter
import com.example.githubusers.feature.search.domain.entity.SearchResult

/**
 * User intents for the search screen
 */
sealed class SearchIntent {
    data class UpdateQuery(val query: String) : SearchIntent()

    data class ExecuteSearch(val query: String) : SearchIntent()

    data class SelectRecentSearch(val query: String) : SearchIntent()

    object ClearSearch : SearchIntent()

    object ClearSearchHistory : SearchIntent()

    object ActivateSearch : SearchIntent()

    object DismissSearch : SearchIntent()

    data class UpdateFilter(val filter: SearchFilter) : SearchIntent()

    object ToggleFilterExpanded : SearchIntent()

    data class UserClicked(val user: SearchResult) : SearchIntent()

    object RefreshTrending : SearchIntent()
}
