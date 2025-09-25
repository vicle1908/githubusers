package com.example.githubusers.feature.repository.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.githubusers.core.paging.DefaultPagingSourceProvider
import com.example.githubusers.core.search.PagingSearchController
import com.example.githubusers.core.search.SearchQueryNormalizer
import com.example.githubusers.core.search.SearchUiState
import com.example.githubusers.feature.repository.domain.RepositorySearchDefaults
import com.example.githubusers.feature.repository.domain.model.Repository
import com.example.githubusers.feature.repository.domain.usecase.ObserveRepositorySearchUseCase
import com.example.githubusers.feature.repository.presentation.intent.RepositoryListIntent
import com.example.githubusers.feature.repository.presentation.state.RepositoryListState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class RepositoryListViewModel @Inject constructor(observeRepositorySearchUseCase: ObserveRepositorySearchUseCase) :
    ViewModel() {

    private val pagingProvider = DefaultPagingSourceProvider(
        scope = viewModelScope,
        initialQuery = RepositorySearchDefaults.DEFAULT_QUERY,
        pagerFactory = { rawQuery ->
            val normalized = SearchQueryNormalizer.normalize(rawQuery)
            observeRepositorySearchUseCase(normalized)
        },
        debounceMillis = 200L
    )

    private val searchController = PagingSearchController(
        scope = viewModelScope,
        queryState = pagingProvider.query,
        resultsSource = pagingProvider.pagingData,
        updateQuery = pagingProvider::updateQuery,
        queryTransformer = { query -> query.trim().ifBlank { RepositorySearchDefaults.DEFAULT_QUERY } }
    )

    private val _state = MutableStateFlow(RepositoryListState())
    val state: StateFlow<RepositoryListState> = _state.asStateFlow()

    val searchState: StateFlow<SearchUiState> = searchController.uiState

    val repositories: Flow<PagingData<Repository>> = searchController.results

    fun process(intent: RepositoryListIntent) {
        when (intent) {
            is RepositoryListIntent.SearchQueryChanged -> {
                searchController.submitQuery(intent.query)
            }
            RepositoryListIntent.Retry -> {
                searchController.retry()
            }
            is RepositoryListIntent.RepositorySelected -> onRepositorySelected(intent.repository)
            RepositoryListIntent.NavigationConsumed ->
                _state.update { it.copy(selectedRepository = null) }
        }
    }

    private fun onRepositorySelected(repository: Repository) {
        _state.update { it.copy(selectedRepository = repository) }
    }
}
