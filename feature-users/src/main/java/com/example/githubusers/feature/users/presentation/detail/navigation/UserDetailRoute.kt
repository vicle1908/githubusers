package com.example.githubusers.feature.users.presentation.detail.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.githubusers.feature.users.presentation.detail.ui.UserDetailScreen
import com.example.githubusers.feature.users.presentation.detail.viewmodel.UserDetailViewModel

@Composable
fun UserDetailRoute(username: String, navigator: UserDetailNavigator) {
    val viewModel: UserDetailViewModel = hiltViewModel()

    LaunchedEffect(username) {
        viewModel.onUsername(username)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val repositoriesFlow = viewModel.repositoriesFlow.collectAsLazyPagingItems()

    UserDetailScreen(
        uiState = uiState,
        repositoriesFlow = repositoriesFlow,
        onIntent = viewModel::onIntent,
        onBackClick = navigator::navigateBack
    )
}
