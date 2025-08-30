package com.example.githubusers.feature.users.detail.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.githubusers.feature.users.detail.presentation.viewmodel.UserDetailViewModel

/**
 * Main screen for displaying user details.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    uiState: UserDetailViewModel.UserDetailUiState,
    repositoriesFlow: androidx.paging.compose.LazyPagingItems<com.example.githubusers.feature.users.detail.domain.entity.Repository>,
    onIntent: (UserDetailViewModel.UserDetailIntent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Pull to refresh temporarily disabled due to API changes
    // Will use SwipeRefresh or manual refresh button

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.userDetail?.login ?: "User Details",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            when {
                uiState.isLoading && uiState.userDetail == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null && uiState.userDetail == null -> {
                    ErrorContent(
                        message = uiState.error,
                        onRetry = {
                            onIntent(UserDetailViewModel.UserDetailIntent.RetryLoadUser)
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                uiState.userDetail != null -> {
                    UserDetailContent(
                        userDetail = uiState.userDetail,
                        isFollowing = uiState.isFollowing,
                        isFollowActionInProgress = uiState.isFollowActionInProgress,
                        isBioExpanded = uiState.isBioExpanded,
                        repositories = repositoriesFlow,
                        onFollowClick = {
                            onIntent(UserDetailViewModel.UserDetailIntent.ToggleFollow)
                        },
                        onBioClick = {
                            if (uiState.isBioExpanded) {
                                onIntent(UserDetailViewModel.UserDetailIntent.CollapseBio)
                            } else {
                                onIntent(UserDetailViewModel.UserDetailIntent.ExpandBio)
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            // Pull to refresh UI temporarily disabled
        }
    }
}
