package com.example.githubusers.presentation.state.userlist

import androidx.paging.PagingData
import com.example.githubusers.domain.entity.User

/**
 * Represents different UI states for the Github users list using paging.
 */
sealed class UserListState {
    data object Loading : UserListState()

    data class Success(
        val users: PagingData<User>, // Update this to PagingData<User>
    ) : UserListState()

    data class Error(
        val message: String,
    ) : UserListState()
}
