@file:Suppress("ktlint:standard:function-naming")

package com.example.githubusers.feature.users.list.presentation.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.githubusers.core.ui.accessibility.AccessibilityConstants
import com.example.githubusers.core.ui.accessibility.accessibleInteraction
import com.example.githubusers.core.ui.performance.PerformanceMonitor
import com.example.githubusers.core.ui.performance.TrackCompositionPerformance
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.users.list.presentation.viewmodel.UserListViewModel
import timber.log.Timber

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

    Timber.d("UserListScreen invoked with ViewModel: %s", viewModel::class.java.simpleName)
    val pagingUsers = viewModel.pagedUsers.collectAsLazyPagingItems()

    Timber.d("Paging state: loadState=%s, itemCount=%d", pagingUsers.loadState.refresh, pagingUsers.itemCount)

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            UserListTopAppBar(
                onOpenSearch = { onOpenSearch(null) },
                onOpenSettings = onOpenSettings,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        UserListMainContent(
            pagingUsers = pagingUsers,
            onUserClick = onUserClick,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserListTopAppBar(
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = { Text("GitHub Users") },
        actions = {
            IconButton(
                onClick = {
                    Timber.d("Search icon clicked - opening dedicated search")
                    onOpenSearch()
                },
                modifier =
                Modifier.accessibleInteraction(
                    contentDescription = "Search GitHub users",
                    testTag = "search_button"
                )
            ) {
                Icon(Icons.Filled.Search, contentDescription = null)
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = {
                    Timber.d("Settings icon clicked")
                    onOpenSettings()
                },
                modifier =
                Modifier.accessibleInteraction(
                    contentDescription = AccessibilityConstants.OPEN_SETTINGS,
                    testTag = "settings_button"
                )
            ) {
                Icon(Icons.Filled.Settings, contentDescription = null)
            }
        },
        colors =
        TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
            scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun UserListMainContent(
    pagingUsers: LazyPagingItems<UserSummary>,
    onUserClick: (UserSummary) -> Unit,
    modifier: Modifier = Modifier
) {
    UserListContent(
        pagingUsers = pagingUsers,
        onUserClick = onUserClick,
        onRefresh = { pagingUsers.refresh() },
        modifier = modifier
    )
}
