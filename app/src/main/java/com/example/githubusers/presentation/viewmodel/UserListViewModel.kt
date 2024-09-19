package com.example.githubusers.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.githubusers.domain.entity.User
import com.example.githubusers.domain.usecase.GetUsersPagedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * ViewModel for the User list screen.
 */
@HiltViewModel
class UserListViewModel @Inject constructor(
    getUsersPagedUseCase: GetUsersPagedUseCase,
) : ViewModel() {

    /**
     * Flow emitting paginated user data.
     */
    val pagedUsers: Flow<PagingData<User>> =
        getUsersPagedUseCase().cachedIn(viewModelScope) // Cache the PagingData in the ViewModel
}
