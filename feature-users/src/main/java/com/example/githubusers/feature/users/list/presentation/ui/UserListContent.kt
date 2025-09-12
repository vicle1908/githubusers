@file:Suppress("ktlint:standard:function-naming")

package com.example.githubusers.feature.users.list.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.githubusers.feature.users.list.domain.entity.UserSummary

/**
 * Content component displaying the list of users with adaptive layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListContent(
    pagingUsers: LazyPagingItems<UserSummary>,
    onUserClick: (UserSummary) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current

    // Battery optimization: Use derivedStateOf to avoid unnecessary recompositions
    val isLandscape by remember {
        derivedStateOf { configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE }
    }

    val pullToRefreshState = rememberPullToRefreshState()

    // Battery optimization: Use derivedStateOf for refresh state to reduce recompositions
    val isRefreshing by remember {
        derivedStateOf { pagingUsers.loadState.refresh is LoadState.Loading }
    }

    // Debug logging
    android.util.Log.d("UserListContent", "Load state: ${pagingUsers.loadState.refresh}, Item count: ${pagingUsers.itemCount}")

    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = {
            onRefresh()
            pagingUsers.refresh()
        },
        modifier = modifier,
    ) {
        when {
            pagingUsers.loadState.refresh is LoadState.Loading && pagingUsers.itemCount == 0 -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            pagingUsers.loadState.refresh is LoadState.Error -> {
                val error = (pagingUsers.loadState.refresh as LoadState.Error).error
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Error: ${error.localizedMessage}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            pagingUsers.itemCount == 0 -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No users found",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            else -> {
                if (isLandscape) {
                    // Grid layout for landscape
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(
                            count = pagingUsers.itemCount,
                            key = { index -> pagingUsers[index]?.id ?: index },
                        ) { index ->
                            pagingUsers[index]?.let { user ->
                                UserListItem(
                                    user = user,
                                    onClick = { onUserClick(user) },
                                )
                            }
                        }
                    }
                } else {
                    // List layout for portrait
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(
                            count = pagingUsers.itemCount,
                            key = { index -> pagingUsers[index]?.id ?: index },
                        ) { index ->
                            pagingUsers[index]?.let { user ->
                                UserListItem(
                                    user = user,
                                    onClick = { onUserClick(user) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
