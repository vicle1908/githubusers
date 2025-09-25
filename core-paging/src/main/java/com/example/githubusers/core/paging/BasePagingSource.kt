package com.example.githubusers.core.paging

import timber.log.Timber
import androidx.paging.PagingSource
import androidx.paging.PagingState

abstract class BasePagingSource<Value : Any>(private val startingPage: Int = 1) : PagingSource<Int, Value>() {

    protected abstract suspend fun loadPage(page: Int, loadSize: Int): List<Value>

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Value> {
        val page = params.key ?: startingPage

        return runCatching {
            val data = loadPage(page, params.loadSize)
            val prevKey = if (page == startingPage) null else page - 1
            val nextKey = if (data.isEmpty()) null else page + 1

            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey
            )
        }.getOrElse { throwable ->
            Timber.e(throwable, "Paging load failed at page %s", params.key ?: "unknown")
            LoadResult.Error(throwable)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Value>): Int? = state.anchorPosition?.let { position ->
        val page = state.closestPageToPosition(position)
        page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
    }
}
