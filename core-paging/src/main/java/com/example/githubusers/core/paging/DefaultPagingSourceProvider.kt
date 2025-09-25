package com.example.githubusers.core.paging

import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest

class DefaultPagingSourceProvider<Q, T : Any>(
    private val scope: CoroutineScope,
    initialQuery: Q,
    private val pagerFactory: (Q) -> Flow<PagingData<T>>,
    private val debounceMillis: Long = 300L
) : PagingSourceProvider<Q, T> {

    private val queryFlow = MutableStateFlow(initialQuery)

    override val query: StateFlow<Q> = queryFlow.asStateFlow()

    override fun updateQuery(query: Q) {
        if (queryFlow.value == query) return
        queryFlow.value = query
    }

    override val pagingData: Flow<PagingData<T>> = queryFlow
        .debounce(debounceMillis)
        .flatMapLatest { query ->
            pagerFactory(query)
        }
        .cachedIn(scope)
}
