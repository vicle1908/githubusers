@file:Suppress("ktlint:standard:function-naming")

package com.example.githubusers.feature.users.list.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.githubusers.feature.users.list.presentation.intent.UserListIntent
import com.example.githubusers.feature.users.list.presentation.viewmodel.UserListViewModel

/**
 * Main screen for displaying the user list with search functionality.
 * This version integrates with the ViewModel directly.
 */
@Composable
fun UserListScreen(
    viewModel: UserListViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagingUsers = viewModel.pagedUsers.collectAsLazyPagingItems()

    // Focus management
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Handle back press when SearchBar is expanded
    BackHandler(enabled = state.isSearchMode) {
        viewModel.processIntent(UserListIntent.DismissSearch)
    }

    // Auto-focus search input when search mode is activated
    LaunchedEffect(state.isSearchMode) {
        if (state.isSearchMode) {
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Search bar
        UserListSearchBar(
            query = state.searchQuery,
            isSearchMode = state.isSearchMode,
            onQueryChange = { query ->
                viewModel.processIntent(UserListIntent.UpdateSearchQuery(query))
            },
            onSearch = { query ->
                viewModel.processIntent(UserListIntent.ExecuteSearch(query))
            },
            onActivateSearch = {
                viewModel.processIntent(UserListIntent.ActivateSearch)
            },
            onDismissSearch = {
                viewModel.processIntent(UserListIntent.DismissSearch)
            },
            onClearSearch = {
                viewModel.processIntent(UserListIntent.ClearSearch)
            },
            focusRequester = focusRequester,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
        )

        // Search results header
        if (state.isSearching) {
            SearchResultsHeader(
                query = state.searchQuery,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        // User list
        UserListContent(
            pagingUsers = pagingUsers,
            onUserClick = { user ->
                viewModel.processIntent(UserListIntent.UserClicked(user))
            },
            onRefresh = {
                viewModel.processIntent(UserListIntent.RefreshUsers)
            },
            modifier = Modifier.weight(1f),
        )

        // Loading indicator for pagination
        if (pagingUsers.loadState.append is LoadState.Loading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * Main screen for displaying the user list with search functionality.
 * This version is used by UserListRoute with external state and callbacks.
 */
@Composable
fun UserListScreen(
    uiState: com.example.githubusers.feature.users.list.presentation.state.UserListState,
    onSearchQueryChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    onUserClick: (com.example.githubusers.feature.users.list.domain.entity.UserSummary) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onClearSearch: () -> Unit,
    onToggleSearchActive: (Boolean) -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: UserListViewModel = hiltViewModel()
    val pagingUsers = viewModel.pagedUsers.collectAsLazyPagingItems()

    // Focus management
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Handle back press when SearchBar is expanded
    BackHandler(enabled = uiState.isSearchMode) {
        onToggleSearchActive(false)
    }

    // Auto-focus search input when search mode is activated
    LaunchedEffect(uiState.isSearchMode) {
        if (uiState.isSearchMode) {
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Search bar
        UserListSearchBar(
            query = uiState.searchQuery,
            isSearchMode = uiState.isSearchMode,
            onQueryChange = onSearchQueryChange,
            onSearch = onSearchSubmit,
            onActivateSearch = { onToggleSearchActive(true) },
            onDismissSearch = { onToggleSearchActive(false) },
            onClearSearch = onClearSearch,
            focusRequester = focusRequester,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
        )

        // Search results header
        if (uiState.isSearching) {
            SearchResultsHeader(
                query = uiState.searchQuery,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        // User list
        UserListContent(
            pagingUsers = pagingUsers,
            onUserClick = onUserClick,
            onRefresh = onRefresh,
            modifier = Modifier.weight(1f),
        )

        // Loading indicator for pagination
        if (pagingUsers.loadState.append is LoadState.Loading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
