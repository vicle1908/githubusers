package com.example.githubusers.feature.search.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar as MaterialSearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.githubusers.feature.search.domain.entity.SearchResult
import com.example.githubusers.feature.search.presentation.intent.SearchIntent
import com.example.githubusers.feature.search.presentation.state.SearchState

/**
 * Main search screen UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchState,
    searchResults: LazyPagingItems<SearchResult>,
    trendingUsers: LazyPagingItems<SearchResult>,
    onIntent: (SearchIntent) -> Unit,
    onNavigateToUser: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Search bar
        CustomSearchBar(
            state = state,
            onIntent = onIntent,
            onNavigateBack = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        // Content
        if (state.query.isEmpty() && state.showTrending) {
            // Show trending users
            TrendingUsersSection(
                trendingUsers = trendingUsers,
                onUserClick = { user ->
                    onNavigateToUser(user.login)
                }
            )
        } else {
            // Show search results
            SearchResultsList(
                results = searchResults,
                onUserClick = { user ->
                    onNavigateToUser(user.login)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomSearchBar(
    state: SearchState,
    onIntent: (SearchIntent) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val onQueryChange: (String) -> Unit = { query -> onIntent(SearchIntent.UpdateQuery(query)) }
    val onSearch: (String) -> Unit = { query -> onIntent(SearchIntent.ExecuteSearch(query)) }
    val onActivateSearch: () -> Unit = { onIntent(SearchIntent.ActivateSearch) }
    val onDismissSearch: () -> Unit = {
        if (state.query.isNotEmpty()) {
            onIntent(SearchIntent.DismissSearch)
        } else {
            onNavigateBack()
        }
    }
    val onClearSearch: () -> Unit = { onIntent(SearchIntent.ClearSearch) }
    val onSelectRecentSearch: (String) -> Unit = { search -> onIntent(SearchIntent.SelectRecentSearch(search)) }
    val onClearHistory: () -> Unit = { onIntent(SearchIntent.ClearSearchHistory) }

    MaterialSearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = state.query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                expanded = state.isSearchActive,
                onExpandedChange = { /* Controlled by state */ },
                placeholder = { Text("Search GitHub users...") },
                leadingIcon = {
                    LeadingIconContent(
                        state.isSearchActive,
                        onActivateSearch,
                        onDismissSearch
                    )
                },
                trailingIcon = {
                    TrailingIconContent(
                        state.query,
                        onSearch,
                        onClearSearch
                    )
                }
            )
        },
        expanded = state.isSearchActive,
        onExpandedChange = { expanded ->
            if (expanded) {
                onActivateSearch()
            } else {
                onDismissSearch()
            }
        },
        modifier = modifier
    ) {
        SearchSuggestionsContent(
            state.isSearchActive,
            state.query,
            state.recentSearches,
            onSelectRecentSearch,
            onClearHistory
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeadingIconContent(isSearchActive: Boolean, onActivateSearch: () -> Unit, onDismissSearch: () -> Unit) {
    if (!isSearchActive) {
        IconButton(onClick = onActivateSearch) {
            Icon(
                Icons.Filled.Search,
                contentDescription = "Search"
            )
        }
    } else {
        IconButton(onClick = onDismissSearch) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrailingIconContent(query: String, onSearch: (String) -> Unit, onClearSearch: () -> Unit) {
    when {
        query.isNotEmpty() -> {
            Row {
                IconButton(onClick = { onSearch(query) }) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Submit search"
                    )
                }
                IconButton(onClick = onClearSearch) {
                    Icon(
                        Icons.Filled.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        }
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
    if (isSearchActive) {
        Column(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            if (query.isEmpty()) {
                Text(
                    text = "Start typing to search users",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                RecentSearches(
                    recentSearches = recentSearches,
                    onRecentSearchClick = onSelectRecentSearch,
                    onClearHistory = onClearHistory
                )
            } else if (query.length < 2) {
                Text(
                    text = "Enter at least 2 characters to search",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun TrendingUsersSection(trendingUsers: LazyPagingItems<SearchResult>, onUserClick: (SearchResult) -> Unit) {
    Column {
        Text(
            text = "Trending Users",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn {
            items(trendingUsers.itemCount) { index ->
                trendingUsers[index]?.let { user ->
                    SearchResultItem(
                        result = user,
                        onClick = { onUserClick(user) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultsList(results: LazyPagingItems<SearchResult>, onUserClick: (SearchResult) -> Unit) {
    when {
        results.loadState.refresh is LoadState.Loading -> {
            LoadingStateContent()
        }
        results.loadState.refresh is LoadState.Error -> {
            ErrorStateContent(results)
        }
        results.itemCount == 0 && results.loadState.refresh is LoadState.NotLoading -> {
            EmptyStateContent()
        }
        else -> {
            ResultsListContent(results, onUserClick)
        }
    }
}

@Composable
private fun LoadingStateContent() {
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Loading results…",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorStateContent(results: LazyPagingItems<SearchResult>) {
    val error = (results.loadState.refresh as LoadState.Error).error
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Failed to load results",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = error.message ?: "Unknown error",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { results.retry() }) { Text("Retry") }
    }
}

@Composable
private fun EmptyStateContent() {
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No results found",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Try a different search term",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ResultsListContent(results: LazyPagingItems<SearchResult>, onUserClick: (SearchResult) -> Unit) {
    LazyColumn {
        items(results.itemCount) { index ->
            results[index]?.let { result ->
                SearchResultItem(
                    result = result,
                    onClick = { onUserClick(result) }
                )
            }
        }
        if (results.loadState.append is LoadState.Loading) {
            item {
                Row(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(result: SearchResult, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.login,
                    style = MaterialTheme.typography.bodyLarge
                )
                result.name?.let { name ->
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentSearches(
    recentSearches: List<String>,
    onRecentSearchClick: (String) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (recentSearches.isNotEmpty()) {
        Column(modifier = modifier) {
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent searches",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = onClearHistory
                ) {
                    Text("Clear")
                }
            }

            recentSearches.take(5).forEach { search ->
                ListItem(
                    headlineContent = { Text(search) },
                    leadingContent = {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier =
                    Modifier
                        .clickable { onRecentSearchClick(search) }
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
}
