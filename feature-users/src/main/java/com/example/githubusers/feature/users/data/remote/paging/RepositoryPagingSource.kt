package com.example.githubusers.feature.users.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.githubusers.core.model.RepositoryDto
import com.example.githubusers.feature.users.data.detail.mapper.RepositoryMapper.toDomain
import com.example.githubusers.feature.users.data.remote.api.UserDetailRemoteDataSource
import com.example.githubusers.feature.users.domain.detail.repository.RepositorySort
import com.example.githubusers.feature.users.domain.model.Repository

/**
 * PagingSource for loading repositories from the network.
 */
class RepositoryPagingSource(
    private val remoteDataSource: UserDetailRemoteDataSource,
    private val username: String,
    private val sort: RepositorySort
) : PagingSource<Int, Repository>() {
    override fun getRefreshKey(state: PagingState<Int, Repository>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repository> {
        val page = params.key ?: 1

        return try {
            val response = fetchRepositories(page, params.loadSize)
            val repositories = response.map { it.toDomain() }

            LoadResult.Page(
                data = repositories,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (repositories.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun fetchRepositories(page: Int, loadSize: Int): List<RepositoryDto> =
        remoteDataSource.getUserRepositories(
            username = username,
            page = page,
            perPage = loadSize,
            sort = sort.value
        )
}
