package com.example.githubusers.feature.repository.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.githubusers.core.search.SearchUiState
import com.example.githubusers.core.ui.SearchableListScaffold
import com.example.githubusers.feature.repository.domain.model.Repository
import com.example.githubusers.feature.repository.presentation.intent.RepositoryListIntent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import timber.log.Timber

@Composable
fun RepositoryListScreen(
    searchState: SearchUiState,
    repositories: LazyPagingItems<Repository>,
    onIntent: (RepositoryListIntent) -> Unit,
    onOpenSearch: () -> Unit
) {
    SearchableListScaffold(
        title = "Repositories",
        state = searchState,
        onQueryChanged = { onIntent(RepositoryListIntent.SearchQueryChanged(it)) },
        onRetry = { onIntent(RepositoryListIntent.Retry) },
        actions = {
            IconButton(onClick = onOpenSearch) {
                Icon(Icons.Filled.Search, contentDescription = "Advanced search")
            }
        },
        content = { innerPadding ->
            RepositoryListMainContent(
                searchState = searchState,
                repositories = repositories,
                onIntent = onIntent,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    )
}

@Composable
private fun RepositoryListMainContent(
    searchState: SearchUiState,
    repositories: LazyPagingItems<Repository>,
    onIntent: (RepositoryListIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(repositories, searchState.query) {
        snapshotFlow { repositories.itemSnapshotList.items.take(3) }
            .map { items ->
                items.map { repository ->
                    "${repository.fullName} (⭐${repository.stargazersCount})"
                }
            }
            .filter { it.isNotEmpty() }
            .distinctUntilChanged()
            .collectLatest { snapshot ->
                val queryLabel = searchState.query.takeIf { it.isNotBlank() } ?: "<default>"
                Timber.tag("RepositoryListScreen").d("Snapshot[%s]: %s", queryLabel, snapshot.joinToString())
            }
    }

    RepositoryListContent(
        repositories = repositories,
        onIntent = onIntent,
        searchQuery = searchState.query,
        modifier = modifier
    )
}

@Composable
private fun RepositoryListContent(
    repositories: LazyPagingItems<Repository>,
    onIntent: (RepositoryListIntent) -> Unit,
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    when (val refreshState = repositories.loadState.refresh) {
        is LoadState.Loading -> LoadingState(modifier)
        is LoadState.Error -> ErrorState(modifier, refreshState.error) {
            onIntent(RepositoryListIntent.Retry)
            repositories.retry()
        }
        is LoadState.NotLoading -> if (repositories.itemCount == 0) {
            RepositoryEmptyState(
                query = searchQuery,
                modifier = modifier
            )
        } else {
            RepositoryListItems(
                repositories = repositories,
                onIntent = onIntent,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun RepositoryListItems(
    repositories: LazyPagingItems<Repository>,
    onIntent: (RepositoryListIntent) -> Unit,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(repositories.itemCount) { index ->
            val repository = repositories[index] ?: return@items
            RepositoryListItem(
                repository = repository,
                onClick = { onIntent(RepositoryListIntent.RepositorySelected(repository)) }
            )
        }

        when (val appendState = repositories.loadState.append) {
            is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LinearProgressIndicator()
                    }
                }
            }

            is LoadState.Error -> {
                item {
                    ErrorState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        throwable = appendState.error
                    ) {
                        repositories.retry()
                    }
                }
            }

            else -> Unit
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(modifier: Modifier, throwable: Throwable, onRetry: () -> Unit) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = throwable.message ?: "Unable to load repositories",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun RepositoryEmptyState(query: String, modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val headline = if (query.isBlank()) {
                "No repositories found"
            } else {
                "No repositories for \"$query\""
            }
            Text(
                text = headline,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Try adjusting the language or stars filters to discover more repositories.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
