package com.example.githubusers.feature.users.list.presentation.intent

import com.example.githubusers.feature.users.list.domain.entity.UserSummary

/**
 * User intents for the user list screen.
 */
sealed interface UserListIntent {
    data class UpdateSearchQuery(
        val query: String,
    ) : UserListIntent

    data class ExecuteSearch(
        val query: String,
    ) : UserListIntent

    data object ClearSearch : UserListIntent

    data object ActivateSearch : UserListIntent

    data object DismissSearch : UserListIntent

    data object RefreshUsers : UserListIntent

    data class UserClicked(
        val user: UserSummary,
    ) : UserListIntent
}
