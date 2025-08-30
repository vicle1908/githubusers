package com.example.githubusers.feature.users.list.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.githubusers.feature.users.list.domain.entity.UserSummary
import com.example.githubusers.feature.users.list.domain.usecase.ObserveUserListUseCase
import com.example.githubusers.feature.users.list.presentation.intent.UserListIntent
import com.example.githubusers.feature.users.list.presentation.state.UserListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel for the user list screen using MVI pattern.
 * Handles user browsing and search functionality.
 */
@HiltViewModel
class UserListViewModel
    @Inject
    constructor(
        private val observeUserListUseCase: ObserveUserListUseCase,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        companion object {
            private const val TAG = "UserListViewModel"
            private const val SEARCH_DEBOUNCE_MS = 500L
            private const val MIN_SEARCH_LENGTH = 2
        }

        // Search query flow with debounce for automatic search
        private val searchQueryInternal = MutableStateFlow("")

        // Single state for the entire screen
        private val _state = MutableStateFlow(UserListState())
        val state: StateFlow<UserListState> = _state.asStateFlow()

        /**
         * Flow emitting paginated user data.
         * Shows all users when no search query, or search results when query exists.
         */
        @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
        val pagedUsers =
            searchQueryInternal
                .debounce(SEARCH_DEBOUNCE_MS)
                .flatMapLatest { query ->
                    Log.d(TAG, "Loading users with query: '$query'")
                    // Only search if query has at least MIN_SEARCH_LENGTH characters
                    val searchQuery = if (query.length >= MIN_SEARCH_LENGTH) query else ""
                    observeUserListUseCase(searchQuery)
                }.cachedIn(viewModelScope)

        init {
            // Initialize with query from SavedStateHandle if available
            val initialQuery = savedStateHandle.get<String>("query") ?: ""
            if (initialQuery.isNotEmpty()) {
                processIntent(UserListIntent.UpdateSearchQuery(initialQuery))
            }
        }

        /**
         * Process user intents following MVI pattern
         */
        fun processIntent(intent: UserListIntent) {
            when (intent) {
                is UserListIntent.UpdateSearchQuery -> updateSearchQuery(intent.query)
                is UserListIntent.ExecuteSearch -> executeSearch(intent.query)
                is UserListIntent.ClearSearch -> clearSearch()
                is UserListIntent.ActivateSearch -> activateSearch()
                is UserListIntent.DismissSearch -> dismissSearch()
                is UserListIntent.RefreshUsers -> refreshUsers()
                is UserListIntent.UserClicked -> handleUserClick(intent.user)
            }
        }

        private fun updateSearchQuery(query: String) {
            _state.update { it.copy(searchQuery = query) }
            searchQueryInternal.value = query
        }

        private fun executeSearch(query: String) {
            Log.d(TAG, "Execute search: $query")
            if (query.isNotBlank() && query.length >= MIN_SEARCH_LENGTH) {
                searchQueryInternal.value = query
                _state.update {
                    it.copy(
                        searchQuery = query,
                        isSearchMode = false,
                    )
                }
            }
        }

        private fun clearSearch() {
            _state.update {
                it.copy(
                    searchQuery = "",
                    isSearchMode = false,
                )
            }
            searchQueryInternal.value = ""
        }

        private fun activateSearch() {
            _state.update { it.copy(isSearchMode = true) }
        }

        private fun dismissSearch() {
            _state.update { it.copy(isSearchMode = false) }
            // Clear search query if it's empty
            if (searchQueryInternal.value.isBlank()) {
                _state.update { it.copy(searchQuery = "") }
                searchQueryInternal.value = ""
            }
        }

        private fun refreshUsers() {
            // Trigger refresh by updating the search query
            searchQueryInternal.value = searchQueryInternal.value
        }

        private fun handleUserClick(user: UserSummary) {
            // Navigation will be handled by the UI layer through the navigator interface
            _state.update { it.copy(selectedUser = user) }
        }
    }
