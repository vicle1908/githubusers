package com.example.githubusers.core.search

import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Coordinates query updates with a [PagingSourceProvider] and exposes lightweight search state.
 */
class PagingSearchController<T : Any>(
    private val scope: CoroutineScope,
    queryState: StateFlow<String>,
    private val resultsSource: Flow<PagingData<T>>,
    private val updateQuery: (String) -> Unit,
    private val queryTransformer: (String) -> String = { it.trim() },
    private val minQueryLength: Int = 0,
    private val onQuerySubmitted: suspend (String) -> Unit = {}
) {

    private val _uiState = MutableStateFlow(SearchUiState(query = queryState.value))
    val uiState: StateFlow<SearchUiState> = _uiState

    /**
     * Flow of results; collect once and feed into Paging UI components.
     */
    val results: Flow<PagingData<T>> = resultsSource
        .onEach { _uiState.update { state -> state.copy(isSearching = false, lastError = null) } }
        .catch { throwable ->
            _uiState.update { state -> state.copy(isSearching = false, lastError = throwable) }
            throw throwable
        }

    init {
        scope.launch {
            queryState.collect { queryValue ->
                _uiState.update { state -> state.copy(query = queryValue) }
            }
        }
    }

    fun submitQuery(rawQuery: String) {
        val normalised = queryTransformer(rawQuery)
        if (normalised == _uiState.value.query) return

        if (normalised.length < minQueryLength) {
            _uiState.update { it.copy(query = normalised, isSearching = false, lastError = null) }
            updateQuery("")
            return
        }

        _uiState.update { it.copy(query = normalised, isSearching = true, lastError = null) }
        updateQuery(normalised)
        scope.launch { onQuerySubmitted(normalised) }
    }

    fun retry() {
        updateQuery(_uiState.value.query)
    }
}
