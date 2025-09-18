package com.example.githubusers.feature.search.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.githubusers.core.search.domain.SearchException
import com.example.githubusers.core.search.domain.SearchFilter
import com.example.githubusers.core.search.domain.SearchResult
import com.example.githubusers.core.search.domain.SearchSortOption
import com.example.githubusers.feature.search.data.api.SearchApiService
import com.example.githubusers.feature.search.data.mapper.toSearchResult
import com.example.githubusers.feature.search.data.util.toSearchError
import io.ktor.http.encodeURLParameter
import io.ktor.http.encodeURLQueryComponent

/**
 * Paging source for search results with proper URL encoding and error handling
 */
class SearchPagingSource(
    private val apiService: SearchApiService,
    private val query: String,
    private val filter: SearchFilter
) : PagingSource<Int, SearchResult>() {
    override fun getRefreshKey(state: PagingState<Int, SearchResult>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchResult> = try {
        val page = params.key ?: 1
        val searchQuery = buildEncodedSearchQuery(query, filter)

        val response =
            apiService.searchUsers(
                query = searchQuery,
                page = page,
                perPage = params.loadSize
            )

        val searchResults =
            response.items.map { user ->
                user.toSearchResult()
            }

        LoadResult.Page(
            data = searchResults,
            prevKey = if (page == 1) null else page - 1,
            nextKey = if (searchResults.isEmpty()) null else page + 1
        )
    } catch (t: Throwable) {
        val error = t.toSearchError()
        LoadResult.Error(SearchException(error, message = "Failed to load search results", cause = t))
    }

    /**
     * Build search query with proper URL encoding for all components
     * This method ensures that all parts of the query are properly encoded
     */
    internal fun buildEncodedSearchQuery(baseQuery: String, filter: SearchFilter): String {
        val queryParts = mutableListOf<String>()

        // Add base query with proper encoding
        if (baseQuery.isNotBlank()) {
            queryParts.add(encodeQueryComponent(baseQuery))
        }

        // Add type filter with proper encoding
        filter.type?.let { type ->
            queryParts.add("type:${encodeParameterValue(type.name.lowercase())}")
        }

        // Add location filter with proper encoding
        filter.location?.let { location ->
            queryParts.add("location:${encodeParameterValue(location)}")
        }

        // Add language filter with proper encoding
        filter.language?.let { language ->
            queryParts.add("language:${encodeParameterValue(language)}")
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

    /**
     * Encode query component for search terms
     * Uses Ktor's encodeURLQueryComponent with appropriate settings
     */
    private fun encodeQueryComponent(value: String): String = value.encodeURLQueryComponent(spaceToPlus = true)

    /**
     * Encode parameter values for query qualifiers
     * Uses Ktor's encodeURLParameter for proper encoding
     */
    private fun encodeParameterValue(value: String): String = value.encodeURLParameter(spaceToPlus = true)
}

