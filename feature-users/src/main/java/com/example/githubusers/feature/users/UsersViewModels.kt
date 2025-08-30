package com.example.githubusers.feature.users

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.githubusers.feature.users.base.FeatureMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersListViewModel
    @Inject
    constructor(
        private val repository: FeatureUsersRepository,
    ) : FeatureMviViewModel<UsersIntent, UsersState, UsersEffect>(
            initialState = UsersState(),
        ) {
        private val _intents = MutableSharedFlow<UsersIntent>(extraBufferCapacity = 64)
        val intents = _intents.asSharedFlow()

        private val queryFlow = MutableStateFlow("")

        @OptIn(FlowPreview::class)
        val users: Flow<PagingData<UserUi>> =
            queryFlow
                .debounce(300)
                .map { it.trim() }
                .flatMapLatest { q ->
                    repository.getUsersPaged(q)
                }.cachedIn(viewModelScope)

        init {
            // Start collecting intents
            viewModelScope.launch {
                intents.collect { processIntent(it) }
            }
        }

        override suspend fun processIntent(intent: UsersIntent) {
            when (intent) {
                is UsersIntent.SearchChanged -> updateState { copy(query = intent.query, isSearching = true, error = null) }
                UsersIntent.SubmitSearch -> {
                    queryFlow.value = state.value.query
                    updateState { copy(isSearching = false) }
                }
                is UsersIntent.SelectUser -> {
                    // Selection is handled by the host via deep link/navigation callback; no-op here
                }
                UsersIntent.Refresh -> {
                    queryFlow.value = state.value.query
                }
            }
        }
    }

// Effects for list screen are declared in UsersContracts.kt

@HiltViewModel
class UserDetailViewModel
    @Inject
    constructor(
        private val repository: FeatureUsersRepository,
    ) : FeatureMviViewModel<UserDetailIntent, UserDetailState, UserDetailEffect>(
            initialState = UserDetailState.Loading,
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
    data class Load(
        val username: String,
    ) : UserDetailIntent

    data object Back : UserDetailIntent
}

sealed interface UserDetailEffect : com.example.githubusers.core.mvi.contracts.ViewEffect
