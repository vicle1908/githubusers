package com.example.githubusers.feature.users.domain.repository

import androidx.paging.PagingData
import com.example.githubusers.feature.users.domain.entity.User
import com.example.githubusers.feature.users.domain.entity.UserDetail
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsersPaged(): Flow<PagingData<User>>

    suspend fun getUserDetail(username: String): Result<UserDetail?>
}
