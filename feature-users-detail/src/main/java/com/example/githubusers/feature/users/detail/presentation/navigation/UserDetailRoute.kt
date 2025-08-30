package com.example.githubusers.feature.users.detail.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.githubusers.feature.users.detail.presentation.ui.UserDetailScreen
import com.example.githubusers.feature.users.detail.presentation.viewmodel.UserDetailViewModel

/**
 * Route composable for the User Detail feature.
 * This is the entry point for the user detail screen.
 *
 * @param navigator Navigator for handling navigation actions
 */
@Composable
fun UserDetailRoute(navigator: UserDetailNavigator) {
    val viewModel: UserDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val repositoriesFlow = viewModel.repositoriesFlow.collectAsLazyPagingItems()

    UserDetailScreen(
        uiState = uiState,
        repositoriesFlow = repositoriesFlow,
        onIntent = viewModel::onIntent,
        onBackClick = navigator::navigateBack,
    )
}
