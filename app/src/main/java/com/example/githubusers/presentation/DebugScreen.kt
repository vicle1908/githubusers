package com.example.githubusers.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.githubusers.presentation.debug.DebugSampleItem
import com.example.githubusers.presentation.debug.DebugSearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugScreen(modifier: Modifier = Modifier, viewModel: DebugSearchViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagingItems = viewModel.results.collectAsLazyPagingItems()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        DebugScreenHeader()
        DebugSearchInput(query = uiState.query, onQueryChange = viewModel::onQueryChange)
        DebugSearchActions(onRetry = viewModel::onRetry, onReset = viewModel::resetQuery)
        DebugSearchStatus(isSearching = uiState.isSearching, lastError = uiState.lastError)
        Divider()
        DebugSearchResults(pagingItems = pagingItems, query = uiState.query)
    }
}

@Composable
private fun DebugScreenHeader() {
    Text(
        text = "Core Paging + Search Playground",
        style = MaterialTheme.typography.titleLarge
    )
    Text(
        text = "Type to filter the in-memory dataset by language, title, or description.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun DebugSearchInput(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Search samples") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun DebugSearchActions(onRetry: () -> Unit, onReset: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(onClick = onRetry) {
            Text(text = "Retry")
        }
        TextButton(onClick = onReset) {
            Text(text = "Reset")
        }
    }
}

@Composable
private fun DebugSearchStatus(isSearching: Boolean, lastError: Throwable?) {
    if (isSearching) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
    }

    lastError?.let { error ->
        Text(
            text = error.message ?: "Search failed",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun DebugSearchResults(pagingItems: LazyPagingItems<DebugSampleItem>, query: String) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = pagingItems.itemCount,
            key = { index ->
                pagingItems.peek(index)?.id ?: index
            }
        ) { index ->
            val item = pagingItems[index]
            if (item != null) {
                DebugSampleCard(item)
            }
        }

        pagingItems.apply {
            val refresh = loadState.refresh
            val append = loadState.append
            when {
                refresh is LoadState.NotLoading &&
                    append is LoadState.NotLoading &&
                    append.endOfPaginationReached &&
                    itemCount == 0 -> {
                    item {
                        Text(
                            text = "No results for \"$query\"",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    }
                }

                append is LoadState.Loading -> {
                    item {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DebugSampleCard(item: DebugSampleItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Language: ${item.language}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "Stars: ${item.stars}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
