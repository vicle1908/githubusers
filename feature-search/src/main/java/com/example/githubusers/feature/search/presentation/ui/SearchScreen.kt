package com.example.githubusers.feature.search.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.githubusers.core.search.SearchUiState
import com.example.githubusers.core.search.domain.SearchDomain
import com.example.githubusers.core.search.domain.SearchError
import com.example.githubusers.core.search.domain.SearchException
import com.example.githubusers.core.ui.SearchableListScaffold
import com.example.githubusers.core.ui.feedback.ErrorBanner
import com.example.githubusers.core.ui.list.StandardUserList
import com.example.githubusers.core.ui.list.StandardUserListLayout
import com.example.githubusers.core.ui.list.StandardUserRow
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.repository.domain.model.Repository
import com.example.githubusers.feature.repository.presentation.ui.RepositoryListItem
import com.example.githubusers.feature.search.presentation.intent.SearchIntent
import com.example.githubusers.feature.search.presentation.state.SearchState

@Composable
fun SearchScreen(
    state: SearchState,
    listUiState: SearchUiState,
    userResults: LazyPagingItems<UserSummary>,
    repositoryResults: LazyPagingItems<Repository>,
    trendingUsers: LazyPagingItems<UserSummary>,
    onIntent: (SearchIntent) -> Unit,
    onNavigateToUser: (String) -> Unit,
    onNavigateToRepository: (String, String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    SearchableListScaffold(
        title = "Search",
        state = listUiState,
        onQueryChanged = { onIntent(SearchIntent.UpdateQuery(it)) },
        onSearch = { onIntent(SearchIntent.ExecuteSearch(it)) },
        onSearchFocusChanged = { focused ->
            if (focused) onIntent(SearchIntent.ActivateSearch) else onIntent(SearchIntent.DismissSearch)
        },
        onRetry = { onIntent(SearchIntent.RetryActiveDomain) },
        onBack = onNavigateBack,
        searchLabel = if (state.activeDomain == SearchDomain.REPOSITORIES) {
            "Search repositories"
        } else {
            "Search users"
        },
        headerContent = {
            SearchHeaderSection(state = state, onIntent = onIntent)
        },
        content = { innerPadding ->
            when (state.activeDomain) {
                SearchDomain.USERS -> {
                    if (state.showTrending) {
                        TrendingListSection(
                            state = state,
                            trendingUsers = trendingUsers,
                            onIntent = onIntent,
                            onNavigateToUser = onNavigateToUser,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                    } else {
                        SearchResultsListSection(
                            state = state,
                            searchResults = userResults,
                            onIntent = onIntent,
                            onNavigateToUser = onNavigateToUser,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                    }
                }

                SearchDomain.REPOSITORIES -> {
                    RepositorySearchResultsSection(
                        state = state,
                        repositoryResults = repositoryResults,
                        onIntent = onIntent,
                        onNavigateToRepository = onNavigateToRepository,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        },
        modifier = modifier
    )
}

@Composable
private fun ColumnScope.SearchHeaderSection(state: SearchState, onIntent: (SearchIntent) -> Unit) {
    DomainToggleRow(
        activeDomain = state.activeDomain,
        onSelect = { domain -> onIntent(SearchIntent.SwitchDomain(domain)) }
    )

    SearchSuggestionsContent(
        isSearchActive = state.isSearchActive,
        query = state.query,
        recentSearches = state.recentSearches,
        onSelectRecentSearch = { onIntent(SearchIntent.SelectRecentSearch(it)) },
        onClearHistory = { onIntent(SearchIntent.ClearSearchHistory) }
    )
}

@Composable
private fun DomainToggleRow(activeDomain: SearchDomain, onSelect: (SearchDomain) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = activeDomain == SearchDomain.USERS,
            onClick = { onSelect(SearchDomain.USERS) },
            label = { Text("Users") }
        )
        FilterChip(
            selected = activeDomain == SearchDomain.REPOSITORIES,
            onClick = { onSelect(SearchDomain.REPOSITORIES) },
            label = { Text("Repositories") }
        )
    }
}

@Composable
private fun TrendingListSection(
    state: SearchState,
    trendingUsers: LazyPagingItems<UserSummary>,
    onIntent: (SearchIntent) -> Unit,
    onNavigateToUser: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val refreshError = trendingUsers.loadState.refresh
    if (refreshError is LoadState.Error) {
        ErrorBanner(
            message = searchErrorMessageFromThrowable(refreshError.error),
            onRetry = { trendingUsers.retry() }
        )
    }

    StandardUserList(
        pagingItems = trendingUsers,
        modifier = modifier,
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
    onNavigateToUser: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val refreshError = searchResults.loadState.refresh
    if (refreshError is LoadState.Error) {
        ErrorBanner(
            message = searchErrorMessageFromThrowable(refreshError.error),
            onRetry = { searchResults.retry() }
        )
    }

    StandardUserList(
        pagingItems = searchResults,
        modifier = modifier,
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
private fun RepositorySearchResultsSection(
    state: SearchState,
    repositoryResults: LazyPagingItems<Repository>,
    onIntent: (SearchIntent) -> Unit,
    onNavigateToRepository: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (val refreshState = repositoryResults.loadState.refresh) {
        is LoadState.Loading -> {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is LoadState.Error -> {
            ErrorBanner(
                message = refreshState.error.message ?: "Unable to load repositories",
                onRetry = { repositoryResults.retry() }
            )
        }

        is LoadState.NotLoading -> {
            if (repositoryResults.itemCount == 0) {
                Box(modifier = modifier, contentAlignment = Alignment.Center) {
                    RepositorySearchEmptyState(query = state.query)
                }
            } else {
                LazyColumn(
                    modifier = modifier,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(repositoryResults.itemCount) { index ->
                        val repository = repositoryResults[index] ?: return@items
                        RepositoryListItem(
                            repository = repository,
                            onClick = {
                                onIntent(SearchIntent.RepositoryClicked(repository))
                                onNavigateToRepository(repository.ownerLogin, repository.name)
                            }
                        )
                    }

                    when (val appendState = repositoryResults.loadState.append) {
                        is LoadState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        is LoadState.Error -> {
                            item {
                                ErrorBanner(
                                    message = appendState.error.message
                                        ?: "Unable to load more repositories",
                                    onRetry = { repositoryResults.retry() }
                                )
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}

@Composable
private fun RepositorySearchEmptyState(query: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val headline = if (query.isBlank()) {
            "No repositories found"
        } else {
            "No repositories for \"$query\""
        }
        Text(text = headline, style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Try adjusting the language or stars filters to discover more repositories.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

private fun searchErrorMessageFromThrowable(t: Throwable): String {
    val default = "Something went wrong. Please try again."
    return if (t is SearchException) {
        when (val error = t.error) {
            is SearchError.Network -> "Network error. Check your connection and try again."
            is SearchError.Timeout -> "Request timed out. Please retry."
            is SearchError.RateLimited -> "Rate limit reached. Please wait a moment before retrying."
            is SearchError.Server -> "Server error (${error.code}). Please try again later."
            is SearchError.Client -> "Request error (${error.code}). Please adjust your query and try again."
            is SearchError.Unknown -> default
        }
    } else {
        default
    }
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
                            Icon(Icons.Filled.Search, contentDescription = "Use search")
                            Spacer(modifier = Modifier.width(8.dp))
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
