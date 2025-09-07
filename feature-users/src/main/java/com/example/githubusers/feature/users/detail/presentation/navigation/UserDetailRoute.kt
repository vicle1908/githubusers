package com.example.githubusers.feature.users.detail.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.githubusers.feature.users.detail.domain.usecase.FollowUserUseCase
import com.example.githubusers.feature.users.detail.domain.usecase.GetUserDetailUseCase
import com.example.githubusers.feature.users.detail.domain.usecase.GetUserRepositoriesUseCase
import com.example.githubusers.feature.users.detail.presentation.ui.UserDetailScreen
import com.example.githubusers.feature.users.detail.presentation.viewmodel.UserDetailViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface UserDetailDependencies {
    fun getUserDetailUseCase(): GetUserDetailUseCase

    fun getUserRepositoriesUseCase(): GetUserRepositoriesUseCase

    fun followUserUseCase(): FollowUserUseCase
}

/**
 * Route composable for the User Detail feature.
 * This is the entry point for the user detail screen.
 *
 * @param username The username to display details for
 * @param navigator Navigator for handling navigation actions
 */
@Composable
fun UserDetailRoute(
    username: String,
    navigator: UserDetailNavigator,
) {
    val context = LocalContext.current
    val dependencies =
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            UserDetailDependencies::class.java,
        )

    val viewModel =
        remember(username) {
            UserDetailViewModel(
                username = username,
                getUserDetailUseCase = dependencies.getUserDetailUseCase(),
                getUserRepositoriesUseCase = dependencies.getUserRepositoriesUseCase(),
                followUserUseCase = dependencies.followUserUseCase(),
            )
        }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val repositoriesFlow = viewModel.repositoriesFlow.collectAsLazyPagingItems()

    UserDetailScreen(
        uiState = uiState,
        repositoriesFlow = repositoriesFlow,
        onIntent = viewModel::onIntent,
        onBackClick = navigator::navigateBack,
    )
}
