package com.example.githubusers.feature.repository.presentation.intent

import com.example.githubusers.feature.repository.domain.model.Repository

sealed interface RepositoryListIntent {
    data class SearchQueryChanged(val query: String) : RepositoryListIntent
    data object Retry : RepositoryListIntent
    data class RepositorySelected(val repository: Repository) : RepositoryListIntent
    data object NavigationConsumed : RepositoryListIntent
}
