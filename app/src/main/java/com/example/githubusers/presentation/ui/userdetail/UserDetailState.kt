package com.example.githubusers.presentation.ui.userdetail

import com.example.githubusers.domain.entity.UserDetail

sealed interface UserDetailState {
    data object Loading : UserDetailState

    data class Success(
        val userDetail: UserDetail?,
    ) : UserDetailState

    data class Error(
        val message: String,
    ) : UserDetailState
}
