package com.example.githubusers.feature.users.detail.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.githubusers.feature.users.detail.domain.entity.Repository
import com.example.githubusers.feature.users.detail.domain.entity.UserDetail
import com.example.githubusers.feature.users.detail.domain.repository.RepositorySort
import com.example.githubusers.feature.users.detail.domain.usecase.FollowUserUseCase
import com.example.githubusers.feature.users.detail.domain.usecase.GetUserDetailUseCase
import com.example.githubusers.feature.users.detail.domain.usecase.GetUserRepositoriesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for User Detail screen using MVI pattern.
 */
class UserDetailViewModel
constructor(
    private val username: String,
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val getUserRepositoriesUseCase: GetUserRepositoriesUseCase,
    private val followUserUseCase: FollowUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserDetailUiState())
    val uiState: StateFlow<UserDetailUiState> = _uiState.asStateFlow()

    val repositoriesFlow: Flow<PagingData<Repository>> =
        getUserRepositoriesUseCase(
            username = username,
            sort = RepositorySort.UPDATED
        ).cachedIn(viewModelScope)

    init {
        loadUserDetail()
        checkFollowStatus()
    }

    /**
     * Handle user intents.
     */
    fun onIntent(intent: UserDetailIntent) {
        when (intent) {
            is UserDetailIntent.Refresh -> {
                loadUserDetail()
                checkFollowStatus()
            }
            is UserDetailIntent.ToggleFollow -> toggleFollow()
            is UserDetailIntent.ChangeRepositorySort -> changeRepositorySort(intent.sort)
            is UserDetailIntent.RetryLoadUser -> loadUserDetail()
            is UserDetailIntent.ExpandBio -> expandBio()
            is UserDetailIntent.CollapseBio -> collapseBio()
        }
    }

    private fun loadUserDetail() {
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

    private fun checkFollowStatus() {
        viewModelScope.launch {
            val isFollowing = followUserUseCase.isFollowing(username)
            _uiState.update { it.copy(isFollowing = isFollowing) }
        }
    }

    private fun toggleFollow() {
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
        _uiState.update { it.copy(repositorySort = sort) }
        // Note: Repository flow would need to be recreated with new sort
    }

    private fun expandBio() {
        _uiState.update { it.copy(isBioExpanded = true) }
    }

    private fun collapseBio() {
        _uiState.update { it.copy(isBioExpanded = false) }
    }

    /**
     * UI State for User Detail screen.
     */
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

    /**
     * User intents for the User Detail screen.
     */
    sealed class UserDetailIntent {
        object Refresh : UserDetailIntent()

        object ToggleFollow : UserDetailIntent()

        data class ChangeRepositorySort(val sort: RepositorySort) : UserDetailIntent()

        object RetryLoadUser : UserDetailIntent()

        object ExpandBio : UserDetailIntent()

        object CollapseBio : UserDetailIntent()
    }
}
