package com.example.githubusers.core.domain.repository

import androidx.paging.PagingData
import com.example.githubusers.core.domain.entity.User
import com.example.githubusers.core.domain.entity.UserDetail
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsersPaged(query: String = ""): Flow<PagingData<User>>
    fun getUserDetail(username: String): Flow<Result<UserDetail?>>
}

