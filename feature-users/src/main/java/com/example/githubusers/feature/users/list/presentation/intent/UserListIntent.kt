package com.example.githubusers.feature.users.list.presentation.intent

import com.example.githubusers.core.users.domain.UserSummary

/**
 * User intents for the user list screen (browse-only).
 */
sealed interface UserListIntent {
    data object RefreshUsers : UserListIntent

    data class UserClicked(val user: UserSummary) : UserListIntent
}
