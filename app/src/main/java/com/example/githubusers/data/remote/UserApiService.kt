package com.example.githubusers.data.remote

import android.util.Log
import com.example.githubusers.data.remote.dto.UserDetailDto
import com.example.githubusers.data.remote.dto.UserDto
import com.example.githubusers.data.remote.dto.toEntity
import com.example.githubusers.domain.entity.User
import com.example.githubusers.domain.entity.UserDetail
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * A service class that interacts with the GitHub API to fetch user information.
 */
class UserApiService(
    private val client: HttpClient,
) {

    /**
     * Fetches a list of users from the GitHub API and maps them to domain entities.
     * It returns a [Result] that contains either a list of users or an exception.
     *
     * @param perPage The number of users to retrieve per page.
     * @param since The starting user ID for pagination.
     * @return [Result] of a list of GitHub users or an error.
     */
    suspend fun getUsers(since: Int, perPage: Int): Result<List<User>> {
        return try {
            val listUser = client.get("https://api.github.com/users") {
                parameter("since", since)
                parameter("per_page", perPage)
            }.body<List<UserDto>>().map { it.toEntity() }

            Log.d("UserApiService", "Fetched users: $listUser")

            Result.success(listUser)
        } catch (e: Exception) {
            Log.e("UserApiService", "Error fetching users: ${e.localizedMessage}")
            Result.failure(e)
        }
    }

    /**
     * Fetches the details of a specific user from the GitHub API and maps it to a domain entity.
     * It returns a [Result] that contains either a [UserDetail] or an exception.
     *
     * @param username The login username of the user.
     * @return [Result] of the detailed information of the user or an error.
     */
    suspend fun getUserDetail(username: String): Result<UserDetail> {
        return try {
            val userDetail = client.get("https://api.github.com/users/$username")
                .body<UserDetailDto>()
                .toEntity()

            Log.d("UserApiService", "Fetched user detail: $userDetail")
            Result.success(userDetail)
        } catch (e: Exception) {
            Log.e("UserApiService", "Error fetching user details: ${e.localizedMessage}")
            Result.failure(e)
        }
    }
}
