package com.example.githubusers.feature.users.detail.data.remote.api

import com.example.githubusers.feature.users.detail.data.remote.dto.RepositoryDto
import com.example.githubusers.feature.users.detail.data.remote.dto.UserDetailDto
import com.example.githubusers.feature.users.detail.di.UserDetailHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.http.HttpStatusCode
import javax.inject.Inject

/**
 * Remote data source for user detail operations.
 */
interface UserDetailRemoteDataSource {
    suspend fun getUserDetail(username: String): UserDetailDto

    suspend fun getUserRepositories(
        username: String,
        page: Int,
        perPage: Int,
        sort: String,
    ): List<RepositoryDto>

    suspend fun isFollowing(username: String): Boolean

    suspend fun followUser(username: String)

    suspend fun unfollowUser(username: String)
}

/**
 * Implementation of UserDetailRemoteDataSource using Ktor.
 */
class UserDetailRemoteDataSourceImpl
    @Inject
    constructor(
        @UserDetailHttpClient private val httpClient: HttpClient,
    ) : UserDetailRemoteDataSource {
        override suspend fun getUserDetail(username: String): UserDetailDto = httpClient.get("users/$username").body()

        override suspend fun getUserRepositories(
            username: String,
            page: Int,
            perPage: Int,
            sort: String,
        ): List<RepositoryDto> =
            httpClient
                .get("users/$username/repos") {
                    parameter("page", page)
                    parameter("per_page", perPage)
                    parameter("sort", sort)
                }.body()

        override suspend fun isFollowing(username: String): Boolean =
            try {
                val response = httpClient.get("user/following/$username")
                response.status == HttpStatusCode.NoContent
            } catch (e: Exception) {
                false
            }

        override suspend fun followUser(username: String) {
            httpClient.put("user/following/$username")
        }

        override suspend fun unfollowUser(username: String) {
            httpClient.delete("user/following/$username")
        }
    }
