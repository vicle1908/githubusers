package com.example.githubusers.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.githubusers.core.search.SearchUiState

/**
 * Shared scaffold for search-driven list screens.
 * Provides a consistent top app bar with inline search, loading indicator, error banner, and slot-based content.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableListScaffold(
    title: String,
    state: SearchUiState,
    onQueryChanged: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    searchLabel: String = "Search",
    onSearch: (String) -> Unit = onQueryChanged,
    onSearchFocusChanged: (Boolean) -> Unit = {},
    actions: @Composable () -> Unit = {},
    onBack: (() -> Unit)? = null,
    headerContent: @Composable ColumnScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    var query by remember(state.query) { mutableStateOf(state.query) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    onBack?.let { navigateBack ->
                        IconButton(onClick = navigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Navigate back"
                            )
                        }
                    }
                },
                actions = { actions() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { onSearchFocusChanged(it.isFocused) }
                    .semantics { contentDescription = "search_field" },
                value = query,
                onValueChange = {
                    query = it
                    onQueryChanged(it)
                },
                label = { Text(text = searchLabel) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
                trailingIcon = {
                    IconButton(onClick = { onSearch(query) }) {
                        Icon(Icons.Filled.Search, contentDescription = "Submit search")
                    }
                }
            )

            if (state.isSearching) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }

            state.lastError?.let { error ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = error.localizedMessage ?: "Unable to load results")
                    Button(onClick = onRetry) {
                        Text(text = "Retry")
                    }
                }
            }

            headerContent()

            Box(
                modifier = Modifier
                    .weight(1f, fill = true)
                    .fillMaxWidth()
            ) {
                content(PaddingValues(0.dp))
            }
        }
    }
}