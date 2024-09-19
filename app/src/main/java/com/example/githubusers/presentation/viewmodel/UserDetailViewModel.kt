package com.example.githubusers.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubusers.domain.usecase.GetUserDetailUseCase
import com.example.githubusers.presentation.state.userdetail.UserDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel to handle Github user detail data and state management.
 *
 * This ViewModel fetches and stores detailed information for a specific Github user
 * and manages state transitions between loading, success, and error.
 */
@HiltViewModel
class UserDetailViewModel
@Inject
constructor(
    private val getUserDetailUseCase: GetUserDetailUseCase, // Inject the use case instead of the repository
) : ViewModel() {

    // StateFlow to hold the UI state for the user detail screen
    private val _userDetailState = MutableStateFlow<UserDetailState>(UserDetailState.Loading)
    val userDetailState: StateFlow<UserDetailState> = _userDetailState.asStateFlow()

    /**
     * Fetches detailed information for a Github user by their username.
     *
     * This function triggers the fetching of user details and updates the UI state
     * depending on the result (loading, success, or error).
     *
     * @param username The username of the Github user to fetch details for.
     */
    fun fetchUserDetail(username: String) {
        viewModelScope.launch {
            getUserDetailUseCase(username) // Use the use case to fetch data
                .onStart {
                    _userDetailState.value = UserDetailState.Loading
                }
                .catch { e ->
                    _userDetailState.value = UserDetailState.Error(
                        e.localizedMessage ?: "Unknown error occurred"
                    )
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { userDetail ->
                            _userDetailState.value = UserDetailState.Success(userDetail)
                        },
                        onFailure = { error ->
                            _userDetailState.value = UserDetailState.Error(
                                error.localizedMessage ?: "Failed to load user details"
                            )
                        }
                    )
                }
        }
    }
}
