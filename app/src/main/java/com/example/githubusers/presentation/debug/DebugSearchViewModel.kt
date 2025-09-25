package com.example.githubusers.presentation.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.githubusers.core.paging.DefaultPagingSourceProvider
import com.example.githubusers.core.paging.PagingSourceProvider
import com.example.githubusers.core.search.PagingSearchController
import com.example.githubusers.core.search.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class DebugSearchViewModel @Inject constructor() : ViewModel() {

    private val samples: List<DebugSampleItem> = DebugSampleData.generate()

    private val provider: PagingSourceProvider<String, DebugSampleItem> =
        DefaultPagingSourceProvider<String, DebugSampleItem>(
            scope = viewModelScope,
            initialQuery = "",
            pagerFactory = { query ->
                Pager(
                    config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false)
                ) {
                    DebugSamplePagingSource(
                        data = samples,
                        query = query
                    )
                }.flow
            }
        )

    private val controller = PagingSearchController(
        scope = viewModelScope,
        queryState = provider.query,
        resultsSource = provider.pagingData,
        updateQuery = provider::updateQuery,
        minQueryLength = 0
    )

    val uiState: StateFlow<SearchUiState> = controller.uiState

    val results: Flow<PagingData<DebugSampleItem>> = controller.results

    fun onQueryChange(query: String) {
        controller.submitQuery(query)
    }

    fun onRetry() {
        controller.retry()
    }

    fun resetQuery() {
        controller.submitQuery("")
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
