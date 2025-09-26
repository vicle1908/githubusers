package com.example.githubusers.feature.users.presentation.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.githubusers.core.paging.DefaultPagingSourceProvider
import com.example.githubusers.core.search.PagingSearchController
import com.example.githubusers.core.search.SearchUiState
import com.example.githubusers.feature.users.domain.detail.repository.RepositorySort
import com.example.githubusers.feature.users.domain.detail.usecase.FollowUserUseCase
import com.example.githubusers.feature.users.domain.detail.usecase.GetUserDetailUseCase
import com.example.githubusers.feature.users.domain.detail.usecase.ObserveUserRepositoriesUseCase
import com.example.githubusers.feature.users.domain.model.Repository
import com.example.githubusers.feature.users.domain.model.UserDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for User Detail screen using MVI pattern.
 */
@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val observeUserRepositoriesUseCase: ObserveUserRepositoriesUseCase,
    private val followUserUseCase: FollowUserUseCase
) : ViewModel() {
    companion object {
        private const val TAG = "UserDetailViewModel"
        private const val QUERY_DELIMITER = "|"

        private fun buildRepositoryQuery(username: String, sort: RepositorySort): String =
            listOf(username, sort.name).joinToString(separator = QUERY_DELIMITER)

        private fun parseRepositoryQuery(rawQuery: String): Pair<String, RepositorySort>? {
            if (rawQuery.isBlank()) return null
            val parts = rawQuery.split(QUERY_DELIMITER)
            val username = parts.getOrNull(0).orEmpty()
            if (username.isBlank()) return null

            val sort = parts.getOrNull(1)
                ?.let { runCatching { RepositorySort.valueOf(it) }.getOrNull() }
                ?: RepositorySort.UPDATED

            return username to sort
        }
    }

    private val usernameState = MutableStateFlow<String?>(null)

    private val _uiState = MutableStateFlow(UserDetailUiState())
    val uiState: StateFlow<UserDetailUiState> = _uiState.asStateFlow()

    // Create a paging provider for user repositories
    private val repositoriesPagingProvider = DefaultPagingSourceProvider(
        scope = viewModelScope,
        initialQuery = buildRepositoryQuery("", RepositorySort.UPDATED),
        pagerFactory = { rawQuery ->
            val (username, sort) = parseRepositoryQuery(rawQuery) ?: return@DefaultPagingSourceProvider emptyFlow()
            observeUserRepositoriesUseCase(
                username = username,
                sort = sort
            )
        }
    )

    // Create a search controller for repositories
    private val repositoriesSearchController = PagingSearchController(
        scope = viewModelScope,
        queryState = repositoriesPagingProvider.query,
        resultsSource = repositoriesPagingProvider.pagingData,
        updateQuery = repositoriesPagingProvider::updateQuery
    )

    val searchState: StateFlow<SearchUiState> = repositoriesSearchController.uiState

    val repositoriesFlow: Flow<PagingData<Repository>> = repositoriesSearchController.results

    fun onUsername(username: String) {
        if (usernameState.value == username) return
        usernameState.value = username
        loadUserDetail(username)
        checkFollowStatus(username)
        // Load repositories for this user
        repositoriesPagingProvider.updateQuery(buildRepositoryQuery(username, _uiState.value.repositorySort))
    }

    fun onIntent(intent: UserDetailIntent) {
        when (intent) {
            is UserDetailIntent.Refresh -> usernameState.value?.let {
                loadUserDetail(it)
                checkFollowStatus(it)
                // Refresh repositories as well
                repositoriesPagingProvider.updateQuery(buildRepositoryQuery(it, _uiState.value.repositorySort))
            }
            is UserDetailIntent.ToggleFollow -> toggleFollow()
            is UserDetailIntent.ChangeRepositorySort -> changeRepositorySort(intent.sort)
            is UserDetailIntent.RetryLoadUser -> usernameState.value?.let { loadUserDetail(it) }
            is UserDetailIntent.ExpandBio -> expandBio()
            is UserDetailIntent.CollapseBio -> collapseBio()
            UserDetailIntent.RetryLoadRepositories -> {
                usernameState.value?.let {
                    repositoriesPagingProvider.updateQuery(buildRepositoryQuery(it, _uiState.value.repositorySort))
                }
            }
        }
    }

    private fun loadUserDetail(username: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getUserDetailUseCase(username)
                .onSuccess { userDetail ->
                    _uiState.update {
                        it.copy(
                            userDetail = userDetail,
                            isLoading = false,
                            error = null
                        )
                    }
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load user details"
                        )
                    }
                }
        }
    }

    private fun checkFollowStatus(username: String) {
        viewModelScope.launch {
            val isFollowing = followUserUseCase.isFollowing(username)
            _uiState.update { it.copy(isFollowing = isFollowing) }
        }
    }

    private fun toggleFollow() {
        val username = usernameState.value ?: return
        viewModelScope.launch {
            val currentFollowState = _uiState.value.isFollowing
            _uiState.update { it.copy(isFollowActionInProgress = true) }

            followUserUseCase(username, currentFollowState)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isFollowing = !currentFollowState,
                            isFollowActionInProgress = false
                        )
                    }
                }.onFailure {
                    _uiState.update {
                        it.copy(
                            isFollowActionInProgress = false,
                            followError = "Failed to update follow status"
                        )
                    }
                }
        }
    }

    private fun changeRepositorySort(sort: RepositorySort) {
        if (_uiState.value.repositorySort == sort) return
        _uiState.update { it.copy(repositorySort = sort) }
        val username = usernameState.value ?: return
        viewModelScope.launch {
            repositoriesPagingProvider.updateQuery(buildRepositoryQuery(username, sort))
        }
    }

    private fun expandBio() {
        _uiState.update { it.copy(isBioExpanded = true) }
    }

    private fun collapseBio() {
        _uiState.update { it.copy(isBioExpanded = false) }
    }

    data class UserDetailUiState(
        val userDetail: UserDetail? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val isFollowing: Boolean = false,
        val isFollowActionInProgress: Boolean = false,
        val followError: String? = null,
        val repositorySort: RepositorySort = RepositorySort.UPDATED,
        val isBioExpanded: Boolean = false
    )

    sealed class UserDetailIntent {
        object Refresh : UserDetailIntent()
        object ToggleFollow : UserDetailIntent()
        data class ChangeRepositorySort(val sort: RepositorySort) : UserDetailIntent()
        object RetryLoadUser : UserDetailIntent()
        object RetryLoadRepositories : UserDetailIntent()
        object ExpandBio : UserDetailIntent()
        object CollapseBio : UserDetailIntent()
    }
}
