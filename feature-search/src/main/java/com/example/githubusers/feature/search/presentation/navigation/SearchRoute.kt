package com.example.githubusers.feature.search.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.githubusers.feature.search.presentation.ui.SearchScreen
import com.example.githubusers.feature.search.presentation.viewmodel.SearchViewModel

/**
 * Route composable for the Search feature
 */
@Composable
fun SearchRoute(navigator: SearchNavigator, initialQuery: String? = null) {
    val viewModel: SearchViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    val trendingUsers = viewModel.trendingUsers.collectAsLazyPagingItems()

    // If opened via deep link with a query, execute immediately once
    androidx.compose.runtime.LaunchedEffect(initialQuery) {
        if (!initialQuery.isNullOrBlank()) {
            viewModel.processIntent(
                com.example.githubusers.feature.search.presentation.intent.SearchIntent
                    .ExecuteSearch(initialQuery)
            )
        }
    }

    SearchScreen(
        state = state,
        searchResults = searchResults,
        trendingUsers = trendingUsers,
        onIntent = viewModel::processIntent,
        onNavigateToUser = { username ->
            navigator.navigateToUserDetail(username)
        },
        onNavigateBack = {
            navigator.navigateBack()
        }
    )
}
