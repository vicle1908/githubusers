package com.example.githubusers.presentation.ui.screens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.example.githubusers.domain.entity.User
import com.example.githubusers.presentation.viewmodel.UserListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    onUserClick: (User) -> Unit,
    viewModel: UserListViewModel = hiltViewModel(),
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val pagingUsers: LazyPagingItems<User> = viewModel.pagedUsers.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                isRefreshing = true
                try {
                    pagingUsers.refresh()
                } catch (e: Exception) {
                    Log.e("UserListScreen", "Error refreshing users: ${e.localizedMessage}")
                } finally {
                    isRefreshing = false
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            items(
                count = pagingUsers.itemCount,
                key = pagingUsers.itemKey { it.id },
                contentType = pagingUsers.itemContentType { it::class },
            ) { index ->
                val user = pagingUsers[index]
                user?.let {
                    UserRow(user = it, onClick = { onUserClick(it) })
                }
            }

            when (pagingUsers.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is LoadState.Error -> {
                    val error = (pagingUsers.loadState.append as LoadState.Error).error
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "Error loading more users: ${error.localizedMessage}",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            RetryButton(onRetry = { pagingUsers.retry() })
                        }
                    }
                }
                is LoadState.NotLoading -> {
                    if (pagingUsers.itemCount > 0) {
                        item {
                            Text(
                                text = "No more users to load",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }

        LaunchedEffect(pagingUsers) {
            Log.d("UserListScreen", "Item count: ${pagingUsers.itemCount}")
        }
    }
}

@Composable
fun ErrorBanner(errorMessage: String) {
    Box(
        modifier =
        Modifier
            .fillMaxWidth()
            .background(Color.Red)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = errorMessage,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun RetryButton(onRetry: () -> Unit) {
    Button(onClick = onRetry) {
        Text("Retry")
    }
}

@Composable
fun UserRow(
    user: User,
    onClick: () -> Unit,
) {
    val context = LocalContext.current

    Card(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            UserAvatar(avatarUrl = user.avatarUrl)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = user.login,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                val annotatedString =
                    buildAnnotatedString {
                        val startIndex = length
                        append(user.htmlUrl)
                        addStyle(
                            style =
                            SpanStyle(
                                color = Color.Blue,
                                textDecoration = TextDecoration.Underline,
                            ),
                            start = startIndex,
                            end = length,
                        )
                        user.htmlUrl?.let {
                            addStringAnnotation(
                                tag = "URL",
                                annotation = it,
                                start = startIndex,
                                end = length,
                            )
                        }
                    }

                Text(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodySmall,
                    modifier =
                    Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, user.htmlUrl?.toUri())
                        context.startActivity(intent)
                    },
                )
            }
        }
    }
}

@Composable
fun UserAvatar(avatarUrl: String) {
    AsyncImage(
        model = avatarUrl,
        contentDescription = null,
        placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
        error = painterResource(id = android.R.drawable.ic_menu_report_image),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewUserListScreen() {
    // Mocking API response
    val mockUserList =
        listOf(
            User(
                login = "defunkt",
                avatarUrl = "https://avatars.githubusercontent.com/u/2?v=4",
                htmlUrl = "https://github.com/defunkt",
                id = 1,
            ),
            User(
                login = "pjhyett",
                avatarUrl = "https://avatars.githubusercontent.com/u/3?v=4",
                htmlUrl = "https://github.com/pjhyett",
                id = 2,
            ),
        )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(mockUserList) { user ->
            UserRow(user = user, onClick = { /* Handle click */ })
        }
    }
}
