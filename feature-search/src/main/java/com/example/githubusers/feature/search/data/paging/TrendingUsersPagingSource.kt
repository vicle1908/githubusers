package com.example.githubusers.feature.search.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.githubusers.core.data.api.GitHubApiService
import com.example.githubusers.feature.search.data.mapper.toSearchResult
import com.example.githubusers.feature.search.domain.entity.SearchResult
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Paging source for trending users (users with most followers recently)
 */
class TrendingUsersPagingSource(
    private val apiService: GitHubApiService,
) : PagingSource<Int, SearchResult>() {
    override fun getRefreshKey(state: PagingState<Int, SearchResult>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchResult> =
        try {
            val page = params.key ?: 1

            // Query for trending users - users created in last 30 days with most followers
            val thirtyDaysAgo = LocalDate.now().minusDays(30)
            val dateString = thirtyDaysAgo.format(DateTimeFormatter.ISO_DATE)

            val response =
                apiService.searchUsers(
                    query = "created:>$dateString sort:followers",
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
            // Fallback to regular users list if search fails
            try {
                val since = ((params.key ?: 1) - 1) * params.loadSize
                val users =
                    apiService.getUsers(
                        since = since,
                        perPage = params.loadSize,
                    )

                val searchResults =
                    users.map { user ->
                        user.toSearchResult()
                    }

                LoadResult.Page(
                    data = searchResults,
                    prevKey = if (since == 0) null else params.key?.minus(1),
                    nextKey = if (searchResults.isEmpty()) null else (params.key ?: 1) + 1,
                )
            } catch (fallbackError: Exception) {
                LoadResult.Error(fallbackError)
            }
        }
}
