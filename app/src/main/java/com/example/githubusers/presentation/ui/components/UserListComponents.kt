package com.example.githubusers.presentation.ui.components

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.example.githubusers.domain.entity.User

/**
 * Enum for different user display styles
 */
enum class UserDisplayStyle {
    ROW,
    CARD,
}

/**
 * Handles the different load states for paging data in a LazyGrid
 * This eliminates duplicate code for handling Loading, Error, and NotLoading states
 */
@Composable
fun PagingLoadStateHandler(
    loadState: LoadState,
    itemCount: Int = 0,
    onRetry: () -> Unit,
) {
    PagingLoadStateContent(loadState, itemCount, onRetry)
}

/**
 * Common content for handling paging load states
 */
@Composable
private fun PagingLoadStateContent(
    loadState: LoadState,
    itemCount: Int,
    onRetry: () -> Unit,
) {
    when (loadState) {
        is LoadState.Loading -> {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
        is LoadState.Error -> {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Error loading more users: ${loadState.error.localizedMessage}",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
        is LoadState.NotLoading -> {
            // Don't show anything when not loading
            // The main list will handle empty states
        }
    }
}

/**
 * Unified user item component that can display in different styles
 * Eliminates duplication between UserRow and UserCard
 */
@Composable
fun UserItem(
    user: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    displayStyle: UserDisplayStyle = UserDisplayStyle.ROW,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onClick() },
    ) {
        when (displayStyle) {
            UserDisplayStyle.ROW -> {
                Row(modifier = Modifier.padding(16.dp)) {
                    UserAvatar(avatarUrl = user.avatarUrl)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        UserInfo(user = user)
                    }
                }
            }
            UserDisplayStyle.CARD -> {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    UserAvatar(avatarUrl = user.avatarUrl)
                    Spacer(modifier = Modifier.height(12.dp))
                    UserInfo(user = user, centerText = true)
                }
            }
        }
    }
}

/**
 * Common user info display (name and clickable URL)
 * Extracted to eliminate duplication of user text and link handling
 */
@Composable
private fun UserInfo(
    user: User,
    centerText: Boolean = false,
) {
    Text(
        text = user.login,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        textAlign = if (centerText) TextAlign.Center else TextAlign.Start,
        modifier = if (centerText) Modifier.fillMaxWidth() else Modifier,
    )
    if (!centerText) {
        UserClickableLink(user = user)
    } else {
        Spacer(modifier = Modifier.height(4.dp))
        UserClickableLink(user = user, centerText = true)
    }
}

/**
 * Reusable user avatar component
 */
@Composable
fun UserAvatar(
    avatarUrl: String,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = avatarUrl,
        contentDescription = null,
        placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
        error = painterResource(id = android.R.drawable.ic_menu_report_image),
        contentScale = ContentScale.Crop,
        modifier =
            modifier
                .size(48.dp)
                .clip(CircleShape),
    )
}

/**
 * Clickable link component for user URLs
 * Eliminates duplicate AnnotatedString creation and click handling
 */
@Composable
fun UserClickableLink(
    user: User,
    centerText: Boolean = false,
) {
    val context = LocalContext.current
    val annotatedString = rememberUserHtmlLink(user.htmlUrl)

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodySmall,
        textAlign = if (centerText) TextAlign.Center else TextAlign.Start,
        modifier =
            Modifier
                .then(if (centerText) Modifier.fillMaxWidth() else Modifier)
                .clickable {
                    val intent = Intent(Intent.ACTION_VIEW, user.htmlUrl?.toUri())
                    context.startActivity(intent)
                },
    )
}

/**
 * Creates an annotated string for the user's HTML URL with proper styling
 */
@Composable
fun rememberUserHtmlLink(htmlUrl: String?): androidx.compose.ui.text.AnnotatedString =
    remember(htmlUrl) {
        buildAnnotatedString {
            val startIndex = length
            append(htmlUrl ?: "")
            addStyle(
                style =
                    SpanStyle(
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline,
                    ),
                start = startIndex,
                end = length,
            )
            htmlUrl?.let {
                addStringAnnotation(
                    tag = "URL",
                    annotation = it,
                    start = startIndex,
                    end = length,
                )
            }
        }
    }

/**
 * Adaptive paging user list that automatically switches between list and grid
 * based on device orientation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
fun AdaptivePagingUserList(
    pagingUsers: LazyPagingItems<User>,
    onUserClick: (User) -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Use rememberSaveable to preserve scroll position across configuration changes
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()

    val isRefreshing =
        remember(pagingUsers.loadState.refresh) {
            pagingUsers.loadState.refresh is LoadState.Loading
        }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { pagingUsers.refresh() },
        modifier = modifier.fillMaxSize(),
    ) {
        // Always show the list, even during refresh
        // The PullToRefreshBox handles the refresh indicator
        if (isLandscape) {
            // Landscape: Use Grid Layout
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                state = gridState,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .then(scrollBehavior?.let { Modifier.nestedScroll(it.nestedScrollConnection) } ?: Modifier),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Handle empty state for initial load
                if (pagingUsers.loadState.refresh is LoadState.NotLoading && pagingUsers.itemCount == 0) {
                    item {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No users found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                items(
                    count = pagingUsers.itemCount,
                    key = pagingUsers.itemKey { it.id },
                    contentType = pagingUsers.itemContentType { it::class },
                ) { index ->
                    val user = pagingUsers[index]
                    user?.let {
                        UserItem(
                            user = it,
                            onClick = { onUserClick(it) },
                            displayStyle = UserDisplayStyle.CARD,
                        )
                    }
                }

                // Handle initial loading state
                if (pagingUsers.loadState.refresh is LoadState.Loading) {
                    item {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                // Handle initial error state
                if (pagingUsers.loadState.refresh is LoadState.Error) {
                    item {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "Error: ${(pagingUsers.loadState.refresh as LoadState.Error).error.localizedMessage}",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { pagingUsers.retry() }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                // Handle append state (pagination)
                item {
                    PagingLoadStateHandler(
                        loadState = pagingUsers.loadState.append,
                        itemCount = pagingUsers.itemCount,
                    ) { pagingUsers.retry() }
                }
            }
        } else {
            // Portrait: Use List Layout
            LazyColumn(
                state = listState,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .then(scrollBehavior?.let { Modifier.nestedScroll(it.nestedScrollConnection) } ?: Modifier),
            ) {
                // Handle empty state for initial load
                if (pagingUsers.loadState.refresh is LoadState.NotLoading && pagingUsers.itemCount == 0) {
                    item {
                        Box(
                            modifier =
                                Modifier
                                    .fillParentMaxSize()
                                    .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No users found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                items(
                    count = pagingUsers.itemCount,
                    key = pagingUsers.itemKey { it.id },
                    contentType = pagingUsers.itemContentType { it::class },
                ) { index ->
                    val user = pagingUsers[index]
                    user?.let {
                        UserItem(
                            user = it,
                            onClick = { onUserClick(it) },
                            displayStyle = UserDisplayStyle.ROW,
                        )
                    }
                }

                // Handle initial loading state
                if (pagingUsers.loadState.refresh is LoadState.Loading) {
                    item {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                // Handle initial error state
                if (pagingUsers.loadState.refresh is LoadState.Error) {
                    item {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "Error: ${(pagingUsers.loadState.refresh as LoadState.Error).error.localizedMessage}",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { pagingUsers.retry() }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                // Handle append state (pagination)
                item {
                    PagingLoadStateHandler(
                        loadState = pagingUsers.loadState.append,
                        itemCount = pagingUsers.itemCount,
                    ) { pagingUsers.retry() }
                }
            }
        }
    }
}
