@file:Suppress("ktlint:standard:function-naming")

package com.example.githubusers.feature.users.list.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.githubusers.feature.users.list.presentation.intent.UserListIntent
import com.example.githubusers.feature.users.list.presentation.ui.UserListScreen
import com.example.githubusers.feature.users.list.presentation.viewmodel.UserListViewModel

/**
 * Route composable for the User List feature.
 * This is the entry point for the user list screen.
 *
 * @param navigator Navigator for handling navigation actions
 * @param initialQuery Optional initial search query to pre-populate
 */
@Composable
fun UserListRoute(
    navigator: UserListNavigator,
    initialQuery: String? = null,
) {
    val viewModel: UserListViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    // Apply initial query if provided
    if (initialQuery != null && uiState.searchQuery.isEmpty()) {
        viewModel.processIntent(UserListIntent.UpdateSearchQuery(initialQuery))
        viewModel.processIntent(UserListIntent.ExecuteSearch(initialQuery))
    }

    UserListScreen(
        uiState = uiState,
        onSearchQueryChange = { query ->
            viewModel.processIntent(UserListIntent.UpdateSearchQuery(query))
        },
        onSearchSubmit = { query ->
            viewModel.processIntent(UserListIntent.ExecuteSearch(query))
        },
        onUserClick = { user ->
            navigator.navigateToUserDetail(user.login)
        },
        onRefresh = {
            viewModel.processIntent(UserListIntent.RefreshUsers)
        },
        onRetry = {
            viewModel.processIntent(UserListIntent.RefreshUsers)
        },
        onClearSearch = {
            viewModel.processIntent(UserListIntent.ClearSearch)
        },
        onToggleSearchActive = { active ->
            if (active) {
                viewModel.processIntent(UserListIntent.ActivateSearch)
            } else {
                viewModel.processIntent(UserListIntent.DismissSearch)
            }
        },
        onBackPress = {
            navigator.navigateBack()
        },
    )
}
