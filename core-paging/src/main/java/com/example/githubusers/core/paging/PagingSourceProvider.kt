package com.example.githubusers.core.paging

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PagingSourceProvider<Q, T : Any> {
    fun updateQuery(query: Q)

    val query: StateFlow<Q>
    val pagingData: Flow<PagingData<T>>
}
