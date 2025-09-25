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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.paging.compose.LazyPagingItems
import com.example.githubusers.feature.users.detail.presentation.viewmodel.UserDetailViewModel
import com.example.githubusers.feature.users.domain.model.Repository
import com.example.githubusers.feature.users.shared.ui.ErrorContent

/**
 * Main screen for displaying user details.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    uiState: UserDetailViewModel.UserDetailUiState,
    repositoriesFlow: LazyPagingItems<Repository>,
    onIntent: (UserDetailViewModel.UserDetailIntent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pull to refresh temporarily disabled due to API changes
    // Will use SwipeRefresh or manual refresh button

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.userDetail?.login ?: "User Details",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                scrollBehavior = scrollBehavior
            )
        },
        // Draw behind app bar to avoid extra top inset; actual padding applied by Scaffold content slot
        contentWindowInsets =
        androidx.compose.foundation.layout
            .WindowInsets(0)
    ) { paddingValues ->
        Box(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            UserDetailBody(
                uiState = uiState,
                repositoriesFlow = repositoriesFlow,
                onIntent = onIntent,
                onBackClick = onBackClick
            )
            // Pull to refresh UI temporarily disabled
        }
    }
}

@Composable
private fun UserDetailBody(
    uiState: UserDetailViewModel.UserDetailUiState,
    repositoriesFlow: LazyPagingItems<Repository>,
    onIntent: (UserDetailViewModel.UserDetailIntent) -> Unit,
    onBackClick: () -> Unit
) {
    when {
        uiState.isLoading && uiState.userDetail == null -> {
            LoadingContent()
        }

        uiState.error != null && uiState.userDetail == null -> {
            ErrorContent(
                message = uiState.error,
                onRetry = {
                    onIntent(UserDetailViewModel.UserDetailIntent.RetryLoadUser)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        uiState.userDetail != null -> {
            UserDetailContent(
                userDetail = uiState.userDetail,
                isFollowing = uiState.isFollowing,
                isFollowActionInProgress = uiState.isFollowActionInProgress,
                followError = uiState.error,
                isBioExpanded = uiState.isBioExpanded,
                repositories = repositoriesFlow,
                onFollowClick = {
                    onIntent(UserDetailViewModel.UserDetailIntent.ToggleFollow)
                },
                onRetryLoadUser = {
                    onIntent(UserDetailViewModel.UserDetailIntent.RetryLoadUser)
                },
                onRetryLoadRepositories = {
                    onIntent(UserDetailViewModel.UserDetailIntent.RetryLoadRepositories)
                },
                onRefresh = {
                    onIntent(UserDetailViewModel.UserDetailIntent.Refresh)
                },
                onExpandBio = {
                    onIntent(UserDetailViewModel.UserDetailIntent.ExpandBio)
                },
                onCollapseBio = {
                    onIntent(UserDetailViewModel.UserDetailIntent.CollapseBio)
                },
                onBackClick = onBackClick,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}