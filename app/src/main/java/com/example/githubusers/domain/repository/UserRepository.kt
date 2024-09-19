package com.example.githubusers.domain.repository

import androidx.paging.PagingData
import com.example.githubusers.domain.entity.User
import com.example.githubusers.domain.entity.UserDetail
import kotlinx.coroutines.flow.Flow

/**
 * Defines the contract for fetching Github users and user details.
 * It utilizes both Room (local database) and the Github API (remote source).
 */
interface UserRepository {
    /**
     * Get users paged
     *
     * @return [Flow] [PagingData]<[User]>
     */
    fun getUsersPaged(): Flow<PagingData<User>>

    /**
     * Fetches the details of a specific Github user.
     * First, retrieves cached data from Room, then fetches fresh data from the Github API
     * and updates the Room cache.
     *
     * @param username The Github username to fetch details for.
     * @return A Flow emitting the [Result] of the details of the Github user from both Room and the API.
     */
    fun getUserDetail(username: String): Flow<Result<UserDetail?>>
}
