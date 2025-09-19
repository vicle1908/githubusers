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
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    val trendingUsers = viewModel.trendingUsers.collectAsLazyPagingItems()

    LaunchedEffect(initialQuery, initialFilter, origin) {
        viewModel.initialize(initialQuery, initialFilter, origin)
        if (!initialQuery.isNullOrBlank()) {
            viewModel.processIntent(SearchIntent.ExecuteSearch(initialQuery))
        }
    }

    SearchScreen(
        state = state,
        searchResults = searchResults,
        trendingUsers = trendingUsers,
        onIntent = viewModel::processIntent,
        onNavigateToUser = navigator::navigateToUserDetail,
        onNavigateBack = navigator::navigateBack
    )
}
