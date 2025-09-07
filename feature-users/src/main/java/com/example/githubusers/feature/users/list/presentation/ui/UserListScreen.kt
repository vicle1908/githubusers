@file:Suppress("ktlint:standard:function-naming")

package com.example.githubusers.feature.users.list.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    viewModel: UserListViewModel = hiltViewModel(),
    onUserClick: (com.example.githubusers.feature.users.list.domain.entity.UserSummary) -> Unit = {},
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    android.util.Log.d("UserListScreen", "UserListScreen called with ViewModel: ${viewModel::class.java.simpleName}")
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagingUsers = viewModel.pagedUsers.collectAsLazyPagingItems()

    android.util.Log.d("UserListScreen", "State: isLoading=${state.isLoading}, searchQuery='${state.searchQuery}', error=${state.error}")
    android.util.Log.d("UserListScreen", "Paging: loadState=${pagingUsers.loadState.refresh}, itemCount=${pagingUsers.itemCount}")

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

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Crossfade(targetState = state.isSearchMode, label = "TopBarSearch") { isActive ->
                if (!isActive) {
                    TopAppBar(
                        title = { Text("GitHub Users") },
                        actions = {
                            IconButton(onClick = {
                                android.util.Log.d("UserListScreen", "Search icon clicked")
                                viewModel.processIntent(UserListIntent.ActivateSearch)
                            }) {
                                Icon(Icons.Filled.Search, contentDescription = "Search")
                            }
                            IconButton(onClick = {
                                android.util.Log.d("UserListScreen", "Settings icon clicked")
                                onOpenSettings()
                            }) {
                                Icon(Icons.Filled.Settings, contentDescription = "Settings")
                            }
                        },
                        scrollBehavior = scrollBehavior,
                        colors =
                            TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                scrolledContainerColor = Color.Transparent,
                                titleContentColor = MaterialTheme.colorScheme.onSurface,
                                actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                    )
                } else {
                    SearchBar(
                        query = state.searchQuery,
                        onQueryChange = { query ->
                            android.util.Log.d("UserListScreen", "Query changed: $query")
                            viewModel.processIntent(UserListIntent.UpdateSearchQuery(query))
                        },
                        onSearch = { query ->
                            android.util.Log.d("UserListScreen", "Search submitted: $query")
                            viewModel.processIntent(UserListIntent.ExecuteSearch(query))
                        },
                        active = true,
                        onActiveChange = { isActiveChange ->
                            android.util.Log.d("UserListScreen", "Search active changed: $isActiveChange")
                            if (!isActiveChange) {
                                viewModel.processIntent(UserListIntent.DismissSearch)
                            }
                        },
                        placeholder = { Text("Search GitHub users...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        trailingIcon = {
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        android.util.Log.d("UserListScreen", "Clear search clicked")
                                        viewModel.processIntent(UserListIntent.ClearSearch)
                                    },
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                                }
                            }
                        },
                        modifier =
                            Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
                        colors =
                            SearchBarDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            ),
                    ) {
                        if (state.searchQuery.isNotEmpty()) {
                            SearchSuggestions(
                                query = state.searchQuery,
                                onSuggestionClick = { suggestion ->
                                    android.util.Log.d("UserListScreen", "Suggestion clicked: $suggestion")
                                    viewModel.processIntent(UserListIntent.UpdateSearchQuery(suggestion))
                                    viewModel.processIntent(UserListIntent.ExecuteSearch(suggestion))
                                },
                            )
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
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
                onUserClick = onUserClick,
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
}

/**
 * Search suggestions composable for the SearchBar content slot.
 * Shows relevant search suggestions based on the current query.
 */
@Composable
private fun SearchSuggestions(
    query: String,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Generate some sample suggestions based on the query
    val suggestions =
        remember(query) {
            listOf(
                "$query android",
                "$query kotlin",
                "$query compose",
                "$query github",
                "$query developer",
            ).take(5)
        }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
    ) {
        items(suggestions) { suggestion ->
            ListItem(
                headlineContent = { Text(suggestion) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { onSuggestionClick(suggestion) },
            )
        }
    }
}
