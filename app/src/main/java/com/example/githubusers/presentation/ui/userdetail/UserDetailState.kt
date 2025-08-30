package com.example.githubusers.presentation.state.userdetail

import com.example.githubusers.domain.entity.UserDetail

// Represents different UI states for the Github user detail
sealed class UserDetailState {
    data object Loading : UserDetailState()

    data class Success(
        val userDetail: UserDetail?,
    ) : UserDetailState()

    data class Error(
        val message: String,
    ) : UserDetailState()
}
