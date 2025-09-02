package com.example.githubusers.presentation.ui.userlist

import androidx.paging.PagingData
import com.example.githubusers.domain.entity.User

sealed interface UserListState {
    data object Loading : UserListState

    data class Success(
        val users: PagingData<User>,
    ) : UserListState

    data class Error(
        val message: String,
    ) : UserListState

    data object Empty : UserListState
}
