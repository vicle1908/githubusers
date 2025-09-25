package com.example.githubusers.feature.users.detail.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
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
    onBackClick: () -> Unit,
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

    Scaffold(
        modifier = modifier,
        topBar = {
            UserDetailTopAppBar(
                username = userDetail.login,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserDetailTopAppBar(username: String, onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = username,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
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
        androidx.compose.material3.CircularProgressIndicator()
    }
}

@Composable
private fun UserOverviewSection(userDetail: UserDetail, modifier: Modifier = Modifier) {
    // Placeholder for UserOverview - implement as needed
}