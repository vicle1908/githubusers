@file:Suppress("ktlint:standard:function-naming")

package com.example.githubusers.feature.users.list.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.githubusers.core.ui.accessibility.AccessibilityConstants
import com.example.githubusers.core.ui.accessibility.accessibleInteraction
import com.example.githubusers.core.ui.accessibility.accessibleSearchField
import com.example.githubusers.core.ui.performance.PerformanceMonitor
import com.example.githubusers.core.ui.performance.TrackCompositionPerformance
import com.example.githubusers.feature.users.list.domain.entity.UserSummary
import com.example.githubusers.feature.users.list.presentation.intent.UserListIntent
import com.example.githubusers.feature.users.list.presentation.state.UserListState
import com.example.githubusers.feature.users.list.presentation.viewmodel.UserListViewModel
import timber.log.Timber

/**
 * UserListScreen with edge-to-edge layout using SearchBar, translucent TopAppBar,
 * automatic system bar inset handling, performance optimizations, and accessibility.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    viewModel: UserListViewModel = hiltViewModel(),
    performanceMonitor: PerformanceMonitor? = null,
    onUserClick: (UserSummary) -> Unit = {},
    onOpenSettings: () -> Unit = {},
) {
    // Track composition performance
    performanceMonitor?.let {
        TrackCompositionPerformance(
            screenName = "UserListScreen",
            monitor = it,
        )
    }

    Timber.d("UserListScreen called with ViewModel: %s", viewModel::class.java.simpleName)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagingUsers = viewModel.pagedUsers.collectAsLazyPagingItems()

    Timber.d("State: isLoading=%b, searchQuery='%s', error=%s", state.isLoading, state.searchQuery, state.error)
    Timber.d("Paging: loadState=%s, itemCount=%d", pagingUsers.loadState.refresh, pagingUsers.itemCount)

    // Focus management
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Handle back press when SearchBar is expanded
    // This follows Android's best practices for predictive back gestures:
    // - Uses BackHandler for simple state-based actions
    // - Enabled/disabled based on observable UI state (state.isSearchMode)
    // - Single responsibility: dismiss search when back is pressed
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
        // Handle safe drawing area including display cutouts and system bars
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            UserListTopAppBar(
                onActivateSearch = { viewModel.processIntent(UserListIntent.ActivateSearch) },
                onOpenSettings = onOpenSettings,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        UserListMainContent(
            state = state,
            pagingUsers = pagingUsers,
            onUserClick = onUserClick,
            onIntentProcessed = viewModel::processIntent,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

/**
 * TopAppBar for the user list screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserListTopAppBar(
    onActivateSearch: () -> Unit,
    onOpenSettings: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        title = { Text("GitHub Users") },
        actions = {
            IconButton(
                onClick = {
                    Timber.d("Search icon clicked")
                    onActivateSearch()
                },
                modifier =
                    Modifier.accessibleInteraction(
                        contentDescription = "Search GitHub users",
                        testTag = "search_button",
                    ),
            ) {
                Icon(Icons.Filled.Search, contentDescription = null)
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = {
                    Timber.d("Settings icon clicked")
                    onOpenSettings()
                },
                modifier =
                    Modifier.accessibleInteraction(
                        contentDescription = AccessibilityConstants.OPEN_SETTINGS,
                        testTag = "settings_button",
                    ),
            ) {
                Icon(Icons.Filled.Settings, contentDescription = null)
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
                scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        scrollBehavior = scrollBehavior,
    )
}

/**
 * Main content of the user list screen.
 */
@Composable
private fun UserListMainContent(
    state: UserListState,
    pagingUsers: androidx.paging.compose.LazyPagingItems<UserSummary>,
    onUserClick: (UserSummary) -> Unit,
    onIntentProcessed: (UserListIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            UserSearchBar(
                state = state,
                pagingUsers = pagingUsers,
                onIntentProcessed = onIntentProcessed,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
            )

            if (state.isSearching) {
                Text(
                    text = "Searching for: ${state.searchQuery}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            UserListContent(
                pagingUsers = pagingUsers,
                onUserClick = onUserClick,
                onRefresh = { onIntentProcessed(UserListIntent.RefreshUsers) },
                modifier = Modifier.weight(1f),
            )

            if (pagingUsers.loadState.append is LoadState.Loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

/**
 * UserSearchBar composable that handles search functionality with the updated SearchBar API.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserSearchBar(
    state: UserListState,
    pagingUsers: androidx.paging.compose.LazyPagingItems<UserSummary>,
    onIntentProcessed: (UserListIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchBar(
        inputField = {
            SearchBarInputField(
                state = state,
                onIntentProcessed = onIntentProcessed,
            )
        },
        expanded = state.isSearchMode,
        onExpandedChange = { isActive ->
            Timber.d("Search expanded changed: %b", isActive)
            if (!isActive) {
                onIntentProcessed(UserListIntent.DismissSearch)
            }
        },
        modifier =
            modifier.accessibleSearchField(
                query = state.searchQuery,
                isActive = state.isSearchMode,
                resultCount = if (state.isSearching) pagingUsers.itemCount else null,
            ),
        content = {
            SearchSuggestionsContent(state.searchQuery)
        },
    )
}

/**
 * SearchBar input field with icons.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarInputField(
    state: UserListState,
    onIntentProcessed: (UserListIntent) -> Unit,
) {
    SearchBarDefaults.InputField(
        query = state.searchQuery,
        onQueryChange = { query ->
            Timber.d("Query changed: %s", query)
            onIntentProcessed(UserListIntent.UpdateSearchQuery(query))
        },
        onSearch = { query ->
            Timber.d("Search submitted: %s", query)
            onIntentProcessed(UserListIntent.ExecuteSearch(query))
        },
        expanded = state.isSearchMode,
        onExpandedChange = { isActive ->
            Timber.d("Search active changed: %b", isActive)
            if (!isActive) {
                onIntentProcessed(UserListIntent.DismissSearch)
            }
        },
        placeholder = { Text("Search GitHub users...") },
        leadingIcon = {
            SearchBackButton(onIntentProcessed)
        },
        trailingIcon = {
            SearchClearButton(
                searchQuery = state.searchQuery,
                onIntentProcessed = onIntentProcessed,
            )
        },
    )
}

/**
 * Search back button.
 */
@Composable
private fun SearchBackButton(onIntentProcessed: (UserListIntent) -> Unit) {
    IconButton(
        onClick = {
            Timber.d("Back arrow clicked - dismissing search")
            onIntentProcessed(UserListIntent.DismissSearch)
        },
        modifier =
            Modifier.accessibleInteraction(
                contentDescription = "Exit search and return to user list",
                testTag = "exit_search_button",
            ),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
        )
    }
}

/**
 * Search clear button.
 */
@Composable
private fun SearchClearButton(
    searchQuery: String,
    onIntentProcessed: (UserListIntent) -> Unit,
) {
    if (searchQuery.isNotEmpty()) {
        IconButton(
            onClick = {
                Timber.d("Clear search clicked")
                onIntentProcessed(UserListIntent.ClearSearch)
            },
            modifier =
                Modifier.accessibleInteraction(
                    contentDescription = AccessibilityConstants.SEARCH_CLEAR,
                    testTag = "clear_search_button",
                ),
        ) {
            Icon(Icons.Filled.Close, contentDescription = null)
        }
    }
}

/**
 * Search suggestions content.
 */
@Composable
private fun SearchSuggestionsContent(searchQuery: String) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .imePadding(),
    ) {
        if (searchQuery.isNotEmpty()) {
            Text("No search suggestions available", modifier = Modifier.padding(16.dp))
        }
    }
}

/**
 * Unified search suggestions composable.
 */
