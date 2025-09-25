package com.example.githubusers.feature.users.list.presentation.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.githubusers.core.ui.SearchableListScaffold
import com.example.githubusers.core.ui.performance.PerformanceMonitor
import com.example.githubusers.core.ui.performance.TrackCompositionPerformance
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.users.list.presentation.intent.UserListIntent
import com.example.githubusers.feature.users.list.presentation.viewmodel.UserListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    viewModel: UserListViewModel = hiltViewModel(),
    performanceMonitor: PerformanceMonitor? = null,
    onUserClick: (UserSummary) -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onOpenSearch: (String?) -> Unit = {}
) {
    performanceMonitor?.let {
        TrackCompositionPerformance(
            screenName = "UserListScreen",
            monitor = it
        )
    }

    val pagingUsers = viewModel.pagedUsers.collectAsLazyPagingItems()
    val searchState by viewModel.searchState.collectAsState()

    SearchableListScaffold(
        title = "GitHub Users",
        state = searchState,
        onQueryChanged = { viewModel.processIntent(UserListIntent.SearchQueryChanged(it)) },
        onRetry = { viewModel.processIntent(UserListIntent.Retry) },
        actions = {
            IconButton(
                onClick = {
                    val query = searchState.query.takeIf { it.isNotBlank() }
                    viewModel.onAdvancedSearchRequested(query)
                    onOpenSearch(query)
                }
            ) {
                Icon(Icons.Filled.Search, contentDescription = "Search GitHub users")
            }
            IconButton(
                onClick = {
                    viewModel.onSettingsRequested()
                    onOpenSettings()
                }
            ) {
                Icon(Icons.Filled.Settings, contentDescription = "Open settings")
            }
        },
        content = { innerPadding ->
            UserListContent(
                pagingUsers = pagingUsers,
                onUserClick = { user ->
                    viewModel.processIntent(UserListIntent.UserClicked(user))
                    onUserClick(user)
                },
                onRefresh = { viewModel.processIntent(UserListIntent.RefreshUsers) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    )
}
