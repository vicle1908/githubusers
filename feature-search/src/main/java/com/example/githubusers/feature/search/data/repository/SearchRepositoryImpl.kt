package com.example.githubusers.feature.search.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.githubusers.feature.search.data.api.SearchApiService
import com.example.githubusers.feature.search.data.local.SearchHistoryDataSource
import com.example.githubusers.feature.search.data.paging.SearchPagingSource
import com.example.githubusers.feature.search.data.paging.TrendingUsersPagingSource
import com.example.githubusers.feature.search.domain.entity.SearchFilter
import com.example.githubusers.feature.search.domain.entity.SearchResult
import com.example.githubusers.feature.search.domain.repository.SearchRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of SearchRepository using GitHub API with proper URL encoding
 */
@Singleton
class SearchRepositoryImpl
@Inject
constructor(
    private val apiService: SearchApiService,
    private val searchHistoryDataSource: SearchHistoryDataSource
) : SearchRepository {
    companion object {
        private const val PAGE_SIZE = 30
        private const val INITIAL_LOAD_SIZE = 30
    }

    override fun searchUsers(query: String, filter: SearchFilter): Flow<PagingData<SearchResult>> = Pager(
        config =
        PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = INITIAL_LOAD_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            SearchPagingSource(
                apiService = apiService,
                query = query,
                filter = filter
            )
        }
    ).flow

    override suspend fun getRecentSearches(): List<String> = searchHistoryDataSource.getRecentSearches()

    override suspend fun saveSearchQuery(query: String) {
        searchHistoryDataSource.saveSearchQuery(query)
    }

    override suspend fun clearSearchHistory() {
        searchHistoryDataSource.clearSearchHistory()
    }

    override fun getTrendingUsers(): Flow<PagingData<SearchResult>> = Pager(
        config =
        PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = INITIAL_LOAD_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            TrendingUsersPagingSource(apiService)
        }
    ).flow
}
