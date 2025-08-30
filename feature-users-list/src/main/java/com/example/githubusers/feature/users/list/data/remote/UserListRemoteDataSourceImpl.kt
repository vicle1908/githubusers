package com.example.githubusers.feature.users.list.data.remote

import com.example.githubusers.feature.users.list.data.remote.dto.SearchResponse
import com.example.githubusers.feature.users.list.data.remote.dto.UserDto
import com.example.githubusers.feature.users.list.di.UserListHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

/**
 * Implementation of UserListRemoteDataSource using Ktor.
 */
class UserListRemoteDataSourceImpl
    @Inject
    constructor(
        @UserListHttpClient private val httpClient: HttpClient,
    ) : UserListRemoteDataSource {
        override suspend fun getUsers(
            since: Int,
            perPage: Int,
        ): List<UserDto> =
            httpClient
                .get("users") {
                    parameter("since", since)
                    parameter("per_page", perPage)
                }.body()

        override suspend fun searchUsers(
            query: String,
            page: Int,
            perPage: Int,
        ): SearchResponse =
            httpClient
                .get("search/users") {
                    parameter("q", query)
                    parameter("page", page)
                    parameter("per_page", perPage)
                }.body()
    }
