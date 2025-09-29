package com.example.githubusers.feature.search.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.githubusers.core.search.domain.SearchDomain
import com.example.githubusers.core.search.domain.SearchException
import com.example.githubusers.core.search.domain.SearchFilter
import com.example.githubusers.core.search.domain.SearchResult
import com.example.githubusers.core.search.domain.SearchSortOption
import com.example.githubusers.feature.search.data.api.SearchApiService
import com.example.githubusers.feature.search.data.mapper.toSearchResult
import com.example.githubusers.feature.search.data.util.toSearchError
import io.ktor.http.encodeURLParameter
import io.ktor.http.encodeURLQueryComponent

class RepositorySearchPagingSource(
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
        val (sortParam, orderParam) = resolveSortParameters(filter)

        val response = apiService.searchRepositories(
            query = searchQuery,
            sort = sortParam,
            order = orderParam,
            page = page,
            perPage = params.loadSize
        )

        val results = response.items.map { repository -> repository.toSearchResult() }

        LoadResult.Page(
            data = results,
            prevKey = if (page == 1) null else page - 1,
            nextKey = if (results.isEmpty()) null else page + 1
        )
    } catch (t: Throwable) {
        val error = t.toSearchError()
        LoadResult.Error(SearchException(error, message = "Failed to load repository results", cause = t))
    }

    private fun buildEncodedSearchQuery(baseQuery: String, filter: SearchFilter): String {
        val queryParts = mutableListOf<String>()

        if (baseQuery.isNotBlank()) {
            queryParts.add(encodeQueryComponent(baseQuery))
        }

        filter.language?.let { language ->
            queryParts.add("language:${encodeParameterValue(language)}")
        }

        // For repository mode, reuse minRepos as a stars threshold if provided
        if (filter.domain == SearchDomain.REPOSITORIES) {
            filter.minRepos?.let { minStars ->
                queryParts.add("stars:>=$minStars")
            }
        }

        return queryParts.joinToString(" ")
    }

    private fun resolveSortParameters(filter: SearchFilter): Pair<String?, String?> = when (filter.sortBy) {
        SearchSortOption.STARS -> "stars" to "desc"
        SearchSortOption.FORKS -> "forks" to "desc"
        SearchSortOption.UPDATED -> "updated" to "desc"
        else -> null to null
    }

    private fun encodeQueryComponent(value: String): String = value.encodeURLQueryComponent(spaceToPlus = true)

    private fun encodeParameterValue(value: String): String = value.encodeURLParameter(spaceToPlus = true)
}
