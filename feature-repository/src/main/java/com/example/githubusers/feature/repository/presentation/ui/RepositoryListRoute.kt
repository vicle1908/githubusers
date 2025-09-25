@file:Suppress("ktlint:standard:function-naming")

package com.example.githubusers.feature.repository.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.githubusers.feature.repository.presentation.intent.RepositoryListIntent
import com.example.githubusers.feature.repository.presentation.navigation.RepositoryListNavigator
import com.example.githubusers.feature.repository.presentation.viewmodel.RepositoryListViewModel

@Composable
fun RepositoryListRoute(navigator: RepositoryListNavigator, viewModel: RepositoryListViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val searchState by viewModel.searchState.collectAsState()
    val repositories = viewModel.repositories.collectAsLazyPagingItems()

    LaunchedEffect(state.selectedRepository) {
        val selected = state.selectedRepository ?: return@LaunchedEffect
        navigator.openRepositoryDetails(selected.ownerLogin, selected.name)
        viewModel.process(RepositoryListIntent.NavigationConsumed)
    }

    RepositoryListScreen(
        searchState = searchState,
        repositories = repositories,
        onIntent = viewModel::process,
        onOpenSearch = { navigator.openSearch(searchState.query) }
    )
}
