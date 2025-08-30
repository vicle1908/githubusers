package com.example.githubusers.feature.search.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.githubusers.core.data.api.GitHubApiService
import com.example.githubusers.feature.search.data.mapper.toSearchResult
import com.example.githubusers.feature.search.domain.entity.SearchFilter
import com.example.githubusers.feature.search.domain.entity.SearchResult
import com.example.githubusers.feature.search.domain.entity.SearchSortOption

/**
 * Paging source for search results
 */
open class SearchPagingSource(
    private val apiService: GitHubApiService?,
    private val query: String,
    private val filter: SearchFilter,
) : PagingSource<Int, SearchResult>() {
    override fun getRefreshKey(state: PagingState<Int, SearchResult>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchResult> {
        // Skip actual API call for testing
        if (apiService == null) {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null,
            )
        }

        return try {
            val page = params.key ?: 1
            val searchQuery = buildSearchQuery(query, filter)

            val response =
                apiService.searchUsers(
                    query = searchQuery,
                    page = page,
                    perPage = params.loadSize,
                )

            val searchResults =
                response.items.map { user ->
                    user.toSearchResult()
                }

            LoadResult.Page(
                data = searchResults,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (searchResults.isEmpty()) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    /**
     * Build search query with filters
     * This method is made open for testing purposes
     */
    open fun buildSearchQuery(
        baseQuery: String,
        filter: SearchFilter,
    ): String {
        val queryParts = mutableListOf(baseQuery)

        // Add type filter
        filter.type?.let { type ->
            queryParts.add("type:${type.name.lowercase()}")
        }

        // Add location filter
        filter.location?.let { location ->
            queryParts.add("location:$location")
        }

        // Add language filter
        filter.language?.let { language ->
            queryParts.add("language:$language")
        }

        // Add minimum repos filter
        filter.minRepos?.let { minRepos ->
            queryParts.add("repos:>=$minRepos")
        }

        // Add minimum followers filter
        filter.minFollowers?.let { minFollowers ->
            queryParts.add("followers:>=$minFollowers")
        }

        // Add sort parameter based on filter
        val sortParam =
            when (filter.sortBy) {
                SearchSortOption.FOLLOWERS -> "sort:followers"
                SearchSortOption.REPOSITORIES -> "sort:repositories"
                SearchSortOption.JOINED -> "sort:joined"
                else -> "" // BEST_MATCH is default, no need to specify
            }

        if (sortParam.isNotEmpty()) {
            queryParts.add(sortParam)
        }

        return queryParts.joinToString(" ")
    }
}
