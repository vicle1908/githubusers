package com.example.githubusers.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.githubusers.core.data.api.GitHubApiService
import com.example.githubusers.core.data.mappers.toDomain
import com.example.githubusers.core.data.mappers.toEntity
import com.example.githubusers.core.domain.entity.User

class UsersPagingSource(
    private val gitHubService: GitHubApiService,
    private val query: String,
) : PagingSource<Int, User>() {
    override fun getRefreshKey(state: PagingState<Int, User>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> =
        try {
            val page = params.key ?: 1
            val response = gitHubService.searchUsers(query, page, params.loadSize)

            LoadResult.Page(
                data = response.items.map { it.toEntity(page).toDomain() },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.items.isEmpty()) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
}
