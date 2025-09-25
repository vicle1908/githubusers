package com.example.githubusers.feature.users.domain.repository

import androidx.paging.PagingData
import com.example.githubusers.core.search.SearchQueryNormalizer
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.users.domain.model.UserDetail
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUsers(query: SearchQueryNormalizer.NormalizedSearchQuery): Flow<PagingData<UserSummary>>

    suspend fun fetchUserDetail(username: String): Result<UserDetail>

    fun observeUsers(): Flow<PagingData<UserSummary>> = observeUsers(SearchQueryNormalizer.normalize(""))
}
