package com.example.githubusers.feature.users.presentation.detail.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.githubusers.feature.users.domain.model.Repository
import com.example.githubusers.feature.users.domain.model.UserDetail
import com.example.githubusers.feature.users.shared.ui.RepositoryItem
import com.example.githubusers.feature.users.shared.ui.ErrorContent

@Composable
fun UserDetailContent(
    userDetail: UserDetail?,
    repositories: LazyPagingItems<Repository>,
    isFollowing: Boolean,
    isFollowActionInProgress: Boolean,
    followError: String?,
    isBioExpanded: Boolean,
    onFollowClick: () -> Unit,
    onRetryLoadUser: () -> Unit,
    onRetryLoadRepositories: () -> Unit,
    onRefresh: () -> Unit,
    onExpandBio: () -> Unit,
    onCollapseBio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    if (userDetail == null) {
        ErrorContent(
            modifier = modifier.fillMaxSize(),
            onRetry = onRetryLoadUser
        )
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            UserInfoCard(
                userDetail = userDetail,
                isFollowing = isFollowing,
                isFollowActionInProgress = isFollowActionInProgress,
                isBioExpanded = isBioExpanded,
                onFollowClick = onFollowClick,
                onBioClick = {
                    if (isBioExpanded) {
                        onCollapseBio()
                    } else {
                        onExpandBio()
                    }
                }
            )
        }

        item {
            UserOverviewSection(
                userDetail = userDetail
            )
        }

        repositoriesSection(
            repositories = repositories,
            onRetryLoadRepositories = onRetryLoadRepositories,
            onRefresh = onRefresh
        )
    }
}

private fun LazyListScope.repositoriesSection(
    repositories: LazyPagingItems<Repository>,
    onRetryLoadRepositories: () -> Unit,
    onRefresh: () -> Unit
) {
    item {
        Text(
            text = "Repositories",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }

    when (repositories.loadState.refresh) {
        is LoadState.Loading -> {
            item {
                LoadingContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }

        is LoadState.Error -> {
            item {
                ErrorContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    onRetry = onRetryLoadRepositories
                )
            }
        }

        is LoadState.NotLoading -> {
            if (repositories.itemCount == 0) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No repositories found",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(repositories.itemCount) { index: Int ->
                    val repository = repositories[index]
                    repository?.let {
                        RepositoryItem(
                            repository = it,
                            onClick = { /* Handle repository click if needed */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }

                if (repositories.loadState.append is LoadState.Loading) {
                    item {
                        LoadingContent(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }

                if (repositories.loadState.append is LoadState.Error) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(onClick = { repositories.retry() }) {
                                Text(text = "Retry")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun UserOverviewSection(userDetail: UserDetail, modifier: Modifier = Modifier) {
    // Placeholder for UserOverview - implement as needed
}
