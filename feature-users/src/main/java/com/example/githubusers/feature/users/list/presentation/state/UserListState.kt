package com.example.githubusers.feature.users.list.presentation.state

import com.example.githubusers.feature.users.list.domain.entity.UserSummary

/**
 * UI state for the user list screen.
 */
data class UserListState(
    val searchQuery: String = "",
    val isSearchMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedUser: UserSummary? = null,
) {
    val isSearching: Boolean
        get() = searchQuery.length >= 2
}
