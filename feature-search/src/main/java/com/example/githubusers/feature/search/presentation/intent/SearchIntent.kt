package com.example.githubusers.feature.search.presentation.intent

import com.example.githubusers.core.search.domain.SearchDomain
import com.example.githubusers.core.search.domain.SearchFilter
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.repository.domain.model.Repository

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

    data class SwitchDomain(val domain: SearchDomain) : SearchIntent()

    data class UserClicked(val user: UserSummary) : SearchIntent()

    data class RepositoryClicked(val repository: Repository) : SearchIntent()

    object RefreshTrending : SearchIntent()

    object RetryActiveDomain : SearchIntent()

    /** Analytics-related intents */
    object BackToBrowse : SearchIntent()
    object TrendingShown : SearchIntent()
}
