package com.example.githubusers.feature.search.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar as MaterialSearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.example.githubusers.core.ui.list.StandardUserList
import com.example.githubusers.core.ui.list.StandardUserListLayout
import com.example.githubusers.core.ui.list.StandardUserRow
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.search.presentation.intent.SearchIntent
import com.example.githubusers.feature.search.presentation.state.SearchState

/**
 * Search screen that reuses the shared StandardUserList scaffold so search results and browse
 * lists render identically.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchState,
    searchResults: LazyPagingItems<UserSummary>,
    trendingUsers: LazyPagingItems<UserSummary>,
    onIntent: (SearchIntent) -> Unit,
    onNavigateToUser: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        SearchTopBar(state = state, onIntent = onIntent, onNavigateBack = onNavigateBack)

        val showTrending = state.query.isEmpty() && state.showTrending

        LaunchedEffect(showTrending) {
            if (showTrending) {
                onIntent(SearchIntent.TrendingShown)
            }
        }

        if (showTrending) {
            TrendingListSection(
                state = state,
                trendingUsers = trendingUsers,
                onIntent = onIntent,
                onNavigateToUser = onNavigateToUser
            )
        } else {
            SearchResultsListSection(
                state = state,
                searchResults = searchResults,
                onIntent = onIntent,
                onNavigateToUser = onNavigateToUser
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    state: SearchState,
    onIntent: (SearchIntent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val onQueryChange: (String) -> Unit = { text -> onIntent(SearchIntent.UpdateQuery(text)) }
    val onExecuteSearch: (String) -> Unit = { text -> onIntent(SearchIntent.ExecuteSearch(text)) }

    MaterialSearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = state.query,
                onQueryChange = onQueryChange,
                onSearch = onExecuteSearch,
                expanded = state.isSearchActive,
                onExpandedChange = { expanded ->
                    if (expanded) onIntent(SearchIntent.ActivateSearch) else onIntent(SearchIntent.DismissSearch)
                },
                placeholder = { Text("Search GitHub users…") },
                leadingIcon = {
                    IconButton(
                        onClick = {
                            if (state.isSearchActive) {
                                onIntent(SearchIntent.BackToBrowse)
                                onNavigateBack()
                            } else {
                                onIntent(SearchIntent.ActivateSearch)
                            }
                        }
                    ) {
                        val leadingIcon =
                            if (state.isSearchActive) {
                                Icons.AutoMirrored.Filled.ArrowBack
                            } else {
                                Icons.Filled.Search
                            }
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = if (state.isSearchActive) "Back" else "Search"
                        )
                    }
                },
                trailingIcon = {
                    if (state.query.isNotBlank()) {
                        IconButton(onClick = { onIntent(SearchIntent.ClearSearch) }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                        }
                    }
                }
            )
        },
        expanded = state.isSearchActive,
        onExpandedChange = { expanded ->
            if (expanded) onIntent(SearchIntent.ActivateSearch) else onIntent(SearchIntent.DismissSearch)
        }
    ) {
        SearchSuggestionsContent(
            isSearchActive = state.isSearchActive,
            query = state.query,
            recentSearches = state.recentSearches,
            onSelectRecentSearch = { onIntent(SearchIntent.SelectRecentSearch(it)) },
            onClearHistory = { onIntent(SearchIntent.ClearSearchHistory) }
        )
    }
}

@Composable
private fun TrendingListSection(
    state: SearchState,
    trendingUsers: LazyPagingItems<UserSummary>,
    onIntent: (SearchIntent) -> Unit,
    onNavigateToUser: (String) -> Unit
) {
    val refreshError = trendingUsers.loadState.refresh
    if (refreshError is androidx.paging.LoadState.Error) {
        com.example.githubusers.core.ui.feedback.ErrorBanner(
            throwable = refreshError.error,
            onRetry = { trendingUsers.retry() }
        )
    }
    StandardUserList(
        pagingItems = trendingUsers,
        layout = StandardUserListLayout.VerticalList(
            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ),
        onRefresh = {
            onIntent(SearchIntent.RefreshTrending)
            trendingUsers.refresh()
        },
        header = {
            TrendingHeader(
                recentQueries = state.recentSearches,
                onRecentSearchClick = { onIntent(SearchIntent.SelectRecentSearch(it)) },
                onClearHistory = { onIntent(SearchIntent.ClearSearchHistory) }
            )
        },
        emptyContent = { TrendingEmptyState(onSearch = { onIntent(SearchIntent.ActivateSearch) }) },
        itemContent = { user ->
            StandardUserRow(
                user = user,
                onClick = {
                    onIntent(SearchIntent.UserClicked(user))
                    onNavigateToUser(user.login)
                }
            )
        }
    )
}

@Composable
private fun SearchResultsListSection(
    state: SearchState,
    searchResults: LazyPagingItems<UserSummary>,
    onIntent: (SearchIntent) -> Unit,
    onNavigateToUser: (String) -> Unit
) {
    val refreshError = searchResults.loadState.refresh
    if (refreshError is androidx.paging.LoadState.Error) {
        com.example.githubusers.core.ui.feedback.ErrorBanner(
            throwable = refreshError.error,
            onRetry = { searchResults.retry() }
        )
    }
    StandardUserList(
        pagingItems = searchResults,
        layout = StandardUserListLayout.VerticalList(
            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ),
        onRefresh = {
            if (state.query.isNotBlank()) {
                onIntent(SearchIntent.ExecuteSearch(state.query))
            }
            searchResults.refresh()
        },
        emptyContent = { SearchEmptyState(query = state.query) },
        itemContent = { user ->
            StandardUserRow(
                user = user,
                onClick = {
                    onIntent(SearchIntent.UserClicked(user))
                    onNavigateToUser(user.login)
                }
            )
        }
    )
}

@Composable
private fun TrendingHeader(
    recentQueries: List<String>,
    onRecentSearchClick: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        if (recentQueries.isNotEmpty()) {
            Text(
                text = "Recent searches",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                recentQueries.take(5).forEach { query ->
                    TextButton(
                        onClick = { onRecentSearchClick(query) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = query, maxLines = 1)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onClearHistory) {
                Text("Clear history")
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        Text(
            text = "Trending Users",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun TrendingEmptyState(onSearch: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No trending users right now",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Try searching for a username or keyword to find people.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onSearch) {
            Text("Start a search")
        }
    }
}

@Composable
private fun SearchEmptyState(query: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No matches for \"$query\"",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Check the spelling or try a different keyword.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SearchSuggestionsContent(
    isSearchActive: Boolean,
    query: String,
    recentSearches: List<String>,
    onSelectRecentSearch: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    if (!isSearchActive) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        when {
            query.isEmpty() && recentSearches.isEmpty() -> {
                Text(
                    text = "Start typing to search users",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            query.length < 2 -> {
                Text(
                    text = "Enter at least 2 characters to search",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            recentSearches.isNotEmpty() -> {
                Text(
                    text = "Recent searches",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    recentSearches.take(5).forEach { recent ->
                        TextButton(onClick = { onSelectRecentSearch(recent) }) {
                            Text(text = recent, maxLines = 1)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = onClearHistory) {
                    Text("Clear history")
                }
            }
        }
    }
}
