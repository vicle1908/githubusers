package com.example.githubusers.feature.search.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.example.githubusers.feature.search.domain.entity.SearchResult
import com.example.githubusers.feature.search.presentation.intent.SearchIntent
import com.example.githubusers.feature.search.presentation.state.SearchState

/**
 * Main search screen UI
 */
@Composable
fun SearchScreen(
    state: SearchState,
    searchResults: LazyPagingItems<SearchResult>,
    trendingUsers: LazyPagingItems<SearchResult>,
    onIntent: (SearchIntent) -> Unit,
    onNavigateToUser: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        // Search bar
        SearchBar(
            query = state.query,
            onQueryChange = { onIntent(SearchIntent.UpdateQuery(it)) },
            onSearch = { onIntent(SearchIntent.ExecuteSearch(it)) },
            onBack = onNavigateBack,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        )

        // Content
        if (state.query.isEmpty() && state.showTrending) {
            // Show trending users
            TrendingUsersSection(
                trendingUsers = trendingUsers,
                onUserClick = { user ->
                    onNavigateToUser(user.login)
                },
            )
        } else {
            // Show search results
            SearchResultsList(
                results = searchResults,
                onUserClick = { user ->
                    onNavigateToUser(user.login)
                },
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search users...") },
        singleLine = true,
        modifier = modifier,
    )
}

@Composable
private fun TrendingUsersSection(
    trendingUsers: LazyPagingItems<SearchResult>,
    onUserClick: (SearchResult) -> Unit,
) {
    Column {
        Text(
            text = "Trending Users",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp),
        )
        LazyColumn {
            items(trendingUsers.itemCount) { index ->
                trendingUsers[index]?.let { user ->
                    SearchResultItem(
                        result = user,
                        onClick = { onUserClick(user) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultsList(
    results: LazyPagingItems<SearchResult>,
    onUserClick: (SearchResult) -> Unit,
) {
    LazyColumn {
        items(results.itemCount) { index ->
            results[index]?.let { result ->
                SearchResultItem(
                    result = result,
                    onClick = { onUserClick(result) },
                )
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    result: SearchResult,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.login,
                    style = MaterialTheme.typography.bodyLarge,
                )
                result.name?.let { name ->
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
