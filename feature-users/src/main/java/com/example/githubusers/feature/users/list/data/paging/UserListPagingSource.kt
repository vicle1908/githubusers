package com.example.githubusers.feature.users.list.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.githubusers.core.ui.performance.PerformanceMonitor
import com.example.githubusers.core.ui.performance.withMemoryTracking
import com.example.githubusers.feature.users.list.data.mapper.toDomainModel
import com.example.githubusers.feature.users.list.data.remote.UserListApiService
import com.example.githubusers.feature.users.list.domain.entity.UserSummary

/**
 * PagingSource for searching users from the GitHub API.
 */
class UserListPagingSource(
    private val apiService: UserListApiService,
    private val query: String,
    private val performanceMonitor: PerformanceMonitor,
) : PagingSource<Int, UserSummary>() {
    override fun getRefreshKey(state: PagingState<Int, UserSummary>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserSummary> {
        val page = params.key ?: 1

        return try {
            val response =
                apiService.searchUsers(
                    query = query,
                    page = page,
                    perPage = params.loadSize,
                )

            response.fold(
                onSuccess = { searchResponse ->
                    val users =
                        performanceMonitor.withMemoryTracking("UserSearchMapping-Page$page") {
                            searchResponse.items.map { it.toDomainModel() }
                        }

                    LoadResult.Page(
                        data = users,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (users.isEmpty()) null else page + 1,
                    )
                },
                onFailure = { exception ->
                    LoadResult.Error<Int, UserSummary>(exception)
                },
            )
        } catch (e: Exception) {
            LoadResult.Error<Int, UserSummary>(e)
        }
    }
}
