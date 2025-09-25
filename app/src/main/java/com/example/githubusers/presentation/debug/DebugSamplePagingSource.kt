package com.example.githubusers.presentation.debug

import androidx.paging.PagingSource
import androidx.paging.PagingState

@Suppress("ktlint:standard:class-signature")
class DebugSamplePagingSource(
    private val data: List<DebugSampleItem>,
    private val query: String
) :
    PagingSource<Int, DebugSampleItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DebugSampleItem> = runCatching {
        val offset = params.key ?: 0
        val filteredItems = filterData()
        val pageSize = params.loadSize
        val page = filteredItems.drop(offset).take(pageSize)
        val nextKey = if (offset + page.size >= filteredItems.size) null else offset + page.size
        val prevKey = if (offset == 0) null else maxOf(offset - pageSize, 0)
        LoadResult.Page(
            data = page,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }.getOrElse { throwable -> LoadResult.Error(throwable) }

    override fun getRefreshKey(state: PagingState<Int, DebugSampleItem>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val closestPage = state.closestPageToPosition(anchorPosition)
        return closestPage?.prevKey?.plus(state.config.pageSize)
            ?: closestPage?.nextKey?.minus(state.config.pageSize)
    }

    private fun filterData(): List<DebugSampleItem> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return data
        return data.filter { item ->
            item.title.contains(trimmed, ignoreCase = true) ||
                item.language.contains(trimmed, ignoreCase = true) ||
                item.description.contains(trimmed, ignoreCase = true)
        }
    }
}
