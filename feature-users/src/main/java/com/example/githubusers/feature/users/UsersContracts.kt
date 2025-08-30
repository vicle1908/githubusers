package com.example.githubusers.feature.users

import androidx.paging.PagingData
import com.example.githubusers.core.mvi.contracts.ViewIntent
import com.example.githubusers.core.mvi.contracts.ViewState
import kotlinx.coroutines.flow.Flow

// UI models local to this feature to avoid cross-module coupling
data class UserUi(
    val id: Int,
    val username: String,
    val avatarUrl: String,
    val htmlUrl: String? = null,
)

data class UserDetailUi(
    val id: Int,
    val username: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val location: String?,
    val followers: Int,
    val following: Int,
    val blog: String?,
)

/**
 * Repository contract to be provided by the app layer via Hilt.
 * The app should bind this to its domain repository with mapping to the UI models here.
 */
interface FeatureUsersRepository {
    fun getUsersPaged(query: String = ""): Flow<PagingData<UserUi>>

    fun getUserDetail(username: String): Flow<Result<UserDetailUi?>>
}

// Intents for MVI
sealed interface UsersIntent : ViewIntent {
    data class SearchChanged(
        val query: String,
    ) : UsersIntent

    data object SubmitSearch : UsersIntent

    data class SelectUser(
        val username: String,
    ) : UsersIntent

    data object Refresh : UsersIntent
}

// State for list/search screen
data class UsersState(
    val query: String = "",
    val isSearching: Boolean = false,
    val users: Flow<PagingData<UserUi>>? = null,
    val error: String? = null,
) : ViewState

// One-off effects for the list screen
sealed interface UsersEffect : com.example.githubusers.core.mvi.contracts.ViewEffect

// State for detail screen
sealed interface UserDetailState : com.example.githubusers.core.mvi.contracts.ViewState {
    data object Loading : UserDetailState

    data class Loaded(
        val detail: UserDetailUi,
    ) : UserDetailState

    data class Error(
        val message: String,
    ) : UserDetailState
}
