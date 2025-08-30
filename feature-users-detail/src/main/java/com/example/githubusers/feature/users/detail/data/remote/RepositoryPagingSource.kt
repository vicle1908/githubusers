package com.example.githubusers.feature.users.detail.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.githubusers.feature.users.detail.data.mapper.RepositoryMapper.toDomain
import com.example.githubusers.feature.users.detail.data.remote.api.UserDetailRemoteDataSource
import com.example.githubusers.feature.users.detail.domain.entity.Repository
import com.example.githubusers.feature.users.detail.domain.repository.RepositorySort

/**
 * PagingSource for loading repositories from the network.
 */
class RepositoryPagingSource(
    private val remoteDataSource: UserDetailRemoteDataSource,
    private val username: String,
    private val sort: RepositorySort,
) : PagingSource<Int, Repository>() {
    override fun getRefreshKey(state: PagingState<Int, Repository>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repository> {
        val page = params.key ?: 1

        return try {
            val response =
                remoteDataSource.getUserRepositories(
                    username = username,
                    page = page,
                    perPage = params.loadSize,
                    sort = sort.value,
                )

            val repositories = response.map { it.toDomain() }

            LoadResult.Page(
                data = repositories,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (repositories.isEmpty()) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
