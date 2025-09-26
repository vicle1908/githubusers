package com.example.githubusers.feature.users.presentation.list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.githubusers.core.paging.DefaultPagingSourceProvider
import com.example.githubusers.core.search.PagingSearchController
import com.example.githubusers.core.search.SearchQueryNormalizer
import com.example.githubusers.core.search.SearchUiState
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.users.domain.list.usecase.ObserveUserListUseCase
import com.example.githubusers.feature.users.presentation.list.analytics.UserListAnalytics
import com.example.githubusers.feature.users.presentation.list.intent.UserListIntent
import com.example.githubusers.feature.users.presentation.list.state.UserListState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

/**
 * ViewModel for the user list screen after consolidating search into feature-search.
 */
@HiltViewModel
class UserListViewModel @Inject constructor(
    private val observeUserListUseCase: ObserveUserListUseCase,
    private val analytics: UserListAnalytics
) : ViewModel() {
    companion object {
        private const val TAG = "UserListViewModel"
    }

    init {
        analytics.onScreenDisplayed(this::class.java.simpleName)
    }

    private val pagingProvider = DefaultPagingSourceProvider(
        scope = viewModelScope,
        initialQuery = "",
        pagerFactory = { query ->
            val normalized = SearchQueryNormalizer.normalize(query)
            Timber.tag(TAG).d("Applying normalised query=%s qualifiers=%s", normalized.original, normalized.qualifiers)
            observeUserListUseCase(normalized)
        },
        debounceMillis = 200L
    )

    private val searchController = PagingSearchController(
        scope = viewModelScope,
        queryState = pagingProvider.query,
        resultsSource = pagingProvider.pagingData,
        updateQuery = pagingProvider::updateQuery,
        queryTransformer = { it.trim() }
    )

    private val _state = MutableStateFlow(UserListState())
    val state: StateFlow<UserListState> = _state.asStateFlow()

    val searchState: StateFlow<SearchUiState> = searchController.uiState

    val pagedUsers: Flow<PagingData<UserSummary>> = searchController.results

    fun processIntent(intent: UserListIntent) {
        when (intent) {
            UserListIntent.RefreshUsers -> {
                Timber.tag(TAG).d("Refresh intent received")
                analytics.onRefresh()
                searchController.retry()
            }
            is UserListIntent.SearchQueryChanged -> {
                analytics.onQueryChanged(intent.query)
                searchController.submitQuery(intent.query)
            }
            UserListIntent.Retry -> {
                analytics.onRetry()
                searchController.retry()
            }
            is UserListIntent.UserClicked -> handleUserClick(intent.user)
        }
    }

    fun onAdvancedSearchRequested(query: String?) {
        analytics.onOpenSearch(query)
    }

    fun onSettingsRequested() {
        analytics.onOpenSettings()
    }

    private fun handleUserClick(user: UserSummary) {
        analytics.onUserSelected(user)
        _state.update { it.copy(selectedUser = user) }
    }
}
