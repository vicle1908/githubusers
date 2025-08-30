@file:Suppress("ktlint:standard:function-naming")

package com.example.githubusers.feature.users.list.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp

/**
 * Search bar component for the user list screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListSearchBar(
    query: String,
    isSearchMode: Boolean,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onActivateSearch: () -> Unit,
    onDismissSearch: () -> Unit,
    onClearSearch: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                expanded = isSearchMode,
                onExpandedChange = { /* Controlled by state */ },
                placeholder = { Text("Search GitHub users...") },
                modifier =
                    Modifier
                        .focusRequester(focusRequester)
                        .onKeyEvent { event ->
                            if (event.type == KeyEventType.KeyDown && event.key == Key.Escape) {
                                onDismissSearch()
                                true
                            } else {
                                false
                            }
                        },
                leadingIcon = {
                    IconButton(onClick = onActivateSearch) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                        )
                    }
                },
                trailingIcon = {
                    when {
                        // Show X button when in search mode to exit
                        isSearchMode -> {
                            IconButton(onClick = onDismissSearch) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Exit search",
                                )
                            }
                        }
                        // Show X button to clear search when collapsed but has results
                        query.isNotEmpty() -> {
                            IconButton(onClick = onClearSearch) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear search",
                                )
                            }
                        }
                    }
                },
            )
        },
        expanded = isSearchMode,
        onExpandedChange = { /* Controlled by state */ },
        modifier =
            modifier.then(
                if (!isSearchMode) {
                    Modifier.clickable { onActivateSearch() }
                } else {
                    Modifier
                },
            ),
    ) {
        // Search suggestions or help text when expanded
        if (isSearchMode && query.isEmpty()) {
            Text(
                text = "Enter at least 2 characters to search",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
