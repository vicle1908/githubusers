package com.example.githubusers.feature.users.list.presentation.state

import com.example.githubusers.core.users.domain.UserSummary

/**
 * UI state for the user list screen after removing inline search.
 */
data class UserListState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedUser: UserSummary? = null
)
