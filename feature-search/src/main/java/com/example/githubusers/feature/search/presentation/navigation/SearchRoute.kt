package com.example.githubusers.feature.search.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.githubusers.core.search.domain.SearchFilter
import com.example.githubusers.feature.search.presentation.intent.SearchIntent
import com.example.githubusers.feature.search.presentation.ui.SearchScreen
import com.example.githubusers.feature.search.presentation.viewmodel.SearchViewModel

/**
 * Route composable for the Search feature. Delegates list rendering to the shared user list component.
 */
@Composable
fun SearchRoute(
    navigator: SearchNavigator,
    initialQuery: String? = null,
    initialFilter: SearchFilter? = null,
    origin: String? = null
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listUiState by viewModel.listUiState.collectAsStateWithLifecycle()
    val userResults = viewModel.userResults.collectAsLazyPagingItems()
    val repositoryResults = viewModel.repositoryResults.collectAsLazyPagingItems()
    val trendingUsers = viewModel.trendingUsers.collectAsLazyPagingItems()

    LaunchedEffect(initialQuery, initialFilter, origin) {
        viewModel.initialize(initialQuery, initialFilter, origin)
        if (!initialQuery.isNullOrBlank()) {
            viewModel.processIntent(SearchIntent.ExecuteSearch(initialQuery))
        }
    }

    SearchScreen(
        state = state,
        listUiState = listUiState,
        userResults = userResults,
        repositoryResults = repositoryResults,
        trendingUsers = trendingUsers,
        onIntent = viewModel::processIntent,
        onNavigateToUser = navigator::navigateToUserDetail,
        onNavigateToRepository = navigator::navigateToRepository,
        onNavigateBack = navigator::navigateBack
    )
}
