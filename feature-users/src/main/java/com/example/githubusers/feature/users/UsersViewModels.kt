package com.example.githubusers.feature.users

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.githubusers.feature.users.base.FeatureMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class UsersListViewModel @Inject constructor(private val repository: FeatureUsersRepository) :
    FeatureMviViewModel<UsersIntent, UsersState, UsersEffect>(
        initialState = UsersState()
    ) {
    private val _intents = MutableSharedFlow<UsersIntent>(extraBufferCapacity = 64)
    val intents = _intents.asSharedFlow()

    val users: Flow<PagingData<UserUi>> = repository.getUsersPaged().cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            intents.collect { processIntent(it) }
        }
    }

    override suspend fun processIntent(intent: UsersIntent) {
        when (intent) {
            UsersIntent.Refresh -> {
                // Paging handles refresh directly via UI. Update state for analytics if needed.
                updateState { copy(isRefreshing = true) }
                updateState { copy(isRefreshing = false) }
            }
            is UsersIntent.SelectUser -> {
                // Navigation handled externally.
            }
        }
    }
}

@HiltViewModel
class UserDetailViewModel @Inject constructor(private val repository: FeatureUsersRepository) :
    FeatureMviViewModel<UserDetailIntent, UserDetailState, UserDetailEffect>(
        initialState = UserDetailState.Loading
    ) {
    override suspend fun processIntent(intent: UserDetailIntent) {
        when (intent) {
            is UserDetailIntent.Load -> load(intent.username)
            UserDetailIntent.Back -> {
                // Back is handled by the host; no-op
            }
        }
    }

    private fun load(username: String) {
        viewModelScope.launch {
            repository.getUserDetail(username).collect { result ->
                result
                    .onSuccess { detail ->
                        if (detail != null) {
                            updateState { UserDetailState.Loaded(detail) }
                        } else {
                            updateState { UserDetailState.Error("User not found") }
                        }
                    }.onFailure { e ->
                        updateState { UserDetailState.Error(e.message ?: "Unknown error") }
                    }
            }
        }
    }
}

sealed interface UserDetailIntent : com.example.githubusers.core.mvi.contracts.ViewIntent {
    data class Load(val username: String) : UserDetailIntent

    data object Back : UserDetailIntent
}

sealed interface UserDetailEffect : com.example.githubusers.core.mvi.contracts.ViewEffect
