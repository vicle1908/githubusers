package com.example.githubusers.feature.users.presentation.list.state

import com.example.githubusers.core.users.domain.UserSummary

data class UserListState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedUser: UserSummary? = null
)
