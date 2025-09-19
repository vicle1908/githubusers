package com.example.githubusers.feature.users.list.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.users.list.domain.usecase.ObserveUserListUseCase
import com.example.githubusers.feature.users.list.presentation.intent.UserListIntent
import com.example.githubusers.feature.users.list.presentation.state.UserListState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

/**
 * ViewModel for the user list screen after consolidating search into feature-search.
 */
@HiltViewModel
class UserListViewModel @Inject constructor(observeUserListUseCase: ObserveUserListUseCase) : ViewModel() {
    companion object {
        private const val TAG = "UserListViewModel"
    }

    private val _state = MutableStateFlow(UserListState())
    val state: StateFlow<UserListState> = _state.asStateFlow()

    val pagedUsers = observeUserListUseCase().cachedIn(viewModelScope)

    fun processIntent(intent: UserListIntent) {
        when (intent) {
            UserListIntent.RefreshUsers -> {
                Timber.tag(TAG).d("Refresh intent received")
                // Paging handle refresh directly from UI; keeping intent for API compatibility.
            }
            is UserListIntent.UserClicked -> handleUserClick(intent.user)
        }
    }

    private fun handleUserClick(user: UserSummary) {
        // Navigation handled by the host via state observation
        _state.update { it.copy(selectedUser = user) }
    }
}
