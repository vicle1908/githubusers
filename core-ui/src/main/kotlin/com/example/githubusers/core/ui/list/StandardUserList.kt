package com.example.githubusers.core.ui.list

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.githubusers.core.users.domain.UserSummary

/**
 * Shared scaffold for displaying paged user lists with consistent empty/error/loading handling,
 * optional pull-to-refresh, and slot-based customization hooks.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Any> StandardUserList(
    pagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    layout: StandardUserListLayout = StandardUserListLayout.VerticalList(),
    enablePullToRefresh: Boolean = true,
    onRefresh: (() -> Unit)? = null,
    header: @Composable (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null,
    emptyContent: @Composable () -> Unit = { DefaultEmptyState() },
    errorContent: @Composable (Throwable, () -> Unit) -> Unit = { error, retry -> DefaultErrorState(error, retry) },
    loadingContent: @Composable () -> Unit = { DefaultLoadingState() },
    appendErrorContent: @Composable (
        Throwable,
        () -> Unit
    ) -> Unit = { error, retry -> DefaultInlineAppendError(error, retry) },
    key: ((T) -> Any)? = null,
    contentType: ((T) -> Any?)? = null,
    itemContent: @Composable (T) -> Unit
) {
    val loadStates = pagingItems.loadState
    val refreshState = loadStates.refresh
    val itemCount by remember { derivedStateOf { pagingItems.itemCount } }

    val isRefreshing by remember(refreshState, itemCount) {
        derivedStateOf { refreshState is LoadState.Loading && itemCount > 0 }
    }

    val refreshAction = remember(onRefresh, pagingItems) {
        onRefresh ?: { pagingItems.refresh() }
    }

    val pullToRefreshState = rememberPullToRefreshState()

    val content: @Composable () -> Unit = {
        when (refreshState) {
            is LoadState.Loading -> {
                if (itemCount == 0) {
                    loadingContent()
                } else {
                    StandardUserListContent(
                        pagingItems = pagingItems,
                        listState = listState,
                        layout = layout,
                        header = header,
                        footer = footer,
                        appendErrorContent = appendErrorContent,
                        loadStates = loadStates,
                        key = key,
                        contentType = contentType,
                        itemContent = itemContent
                    )
                }
            }

            is LoadState.Error -> {
                val error = (refreshState as LoadState.Error).error
                errorContent(error) { refreshAction() }
            }

            is LoadState.NotLoading -> {
                if (itemCount == 0) {
                    emptyContent()
                } else {
                    StandardUserListContent(
                        pagingItems = pagingItems,
                        listState = listState,
                        layout = layout,
                        header = header,
                        footer = footer,
                        appendErrorContent = appendErrorContent,
                        loadStates = loadStates,
                        key = key,
                        contentType = contentType,
                        itemContent = itemContent
                    )
                }
            }
        }
    }

    if (enablePullToRefresh) {
        PullToRefreshBox(
            modifier = modifier.fillMaxSize(),
            state = pullToRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = refreshAction
        ) {
            content()
        }
    } else {
        Box(modifier = modifier.fillMaxSize()) { content() }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun <T : Any> StandardUserListContent(
    pagingItems: LazyPagingItems<T>,
    listState: LazyListState,
    layout: StandardUserListLayout,
    header: @Composable (() -> Unit)?,
    footer: @Composable (() -> Unit)?,
    appendErrorContent: @Composable (Throwable, () -> Unit) -> Unit,
    loadStates: CombinedLoadStates,
    key: ((T) -> Any)?,
    contentType: ((T) -> Any?)?,
    itemContent: @Composable (T) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    when (layout) {
        is StandardUserListLayout.VerticalList -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = layout.contentPadding,
                verticalArrangement = layout.verticalArrangement
            ) {
                header?.let {
                    item(key = "header") { it() }
                }

                items(
                    count = pagingItems.itemCount,
                    key = { index: Int ->
                        val item = pagingItems[index]
                        item?.let { key?.invoke(it) } ?: index
                    },
                    contentType = { index: Int ->
                        pagingItems[index]?.let { contentType?.invoke(it) }
                    }
                ) { index: Int ->
                    val item = pagingItems[index]
                    if (item != null) {
                        itemContent(item)
                    }
                }

                if (loadStates.append is LoadState.Error) {
                    item(key = "append_error") {
                        val error = (loadStates.append as LoadState.Error).error
                        appendErrorContent(error) { pagingItems.retry() }
                    }
                }

                footer?.let {
                    item(key = "footer") { it() }
                }

                if (loadStates.append is LoadState.Loading) {
                    item(key = "append_loader") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }

        is StandardUserListLayout.LandscapeGrid -> {
            val useGrid = isLandscape
            if (useGrid) {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = layout.gridCells,
                    contentPadding = layout.contentPadding,
                    horizontalArrangement = Arrangement.spacedBy(layout.horizontalSpacing),
                    verticalArrangement = Arrangement.spacedBy(layout.verticalSpacing)
                ) {
                    header?.let {
                        item(key = "header", span = { GridItemSpan(maxLineSpan) }) { it() }
                    }

                    repeat(pagingItems.itemCount) { index ->
                        val item = pagingItems[index]
                        if (item != null) {
                            val itemKey = key?.invoke(item) ?: index
                            val type = contentType?.invoke(item)
                            item(
                                key = itemKey,
                                span = { GridItemSpan(1) },
                                contentType = type
                            ) {
                                itemContent(item)
                            }
                        }
                    }

                    if (loadStates.append is LoadState.Error) {
                        item(span = { GridItemSpan(maxLineSpan) }, key = "append_error") {
                            val error = (loadStates.append as LoadState.Error).error
                            appendErrorContent(error) { pagingItems.retry() }
                        }
                    }

                    footer?.let {
                        item(span = { GridItemSpan(maxLineSpan) }, key = "footer") { it() }
                    }

                    if (loadStates.append is LoadState.Loading) {
                        item(span = { GridItemSpan(maxLineSpan) }, key = "append_loader") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            } else {
                // Fallback to vertical list in portrait
                StandardUserListContent(
                    pagingItems = pagingItems,
                    listState = listState,
                    layout = StandardUserListLayout.VerticalList(
                        contentPadding = layout.contentPadding,
                        verticalArrangement = Arrangement.spacedBy(layout.verticalSpacing)
                    ),
                    header = header,
                    footer = footer,
                    appendErrorContent = appendErrorContent,
                    loadStates = loadStates,
                    key = key,
                    contentType = contentType,
                    itemContent = itemContent
                )
            }
        }
    }
}

/** Standard row wrapper exposing consistent semantics and layout. */
@Composable
fun StandardUserRow(user: UserSummary, onClick: () -> Unit, modifier: Modifier = Modifier) {
    UserListItem(
        user = user,
        onClick = onClick,
        modifier = modifier
    )
}

sealed interface StandardUserListLayout {
    data class VerticalList(
        val contentPadding: PaddingValues = PaddingValues(0.dp),
        val verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(2.dp)
    ) : StandardUserListLayout

    data class LandscapeGrid(
        val contentPadding: PaddingValues = PaddingValues(0.dp),
        val gridCells: GridCells = GridCells.Fixed(2),
        val horizontalSpacing: Dp = 4.dp,
        val verticalSpacing: Dp = 2.dp
    ) : StandardUserListLayout
}

@Composable
private fun DefaultLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun DefaultEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No results found",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun DefaultErrorState(error: Throwable, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = error.localizedMessage ?: error.toString(),
                style = MaterialTheme.typography.bodySmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            )
            TextButton(onClick = onRetry) {
                Text("Retry".uppercase())
            }
        }
    }
}

@Composable
private fun DefaultInlineAppendError(error: Throwable, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        TextButton(onClick = onRetry, colors = ButtonDefaults.textButtonColors()) {
            Text(text = "Retry loading more".uppercase())
        }
    }
}
