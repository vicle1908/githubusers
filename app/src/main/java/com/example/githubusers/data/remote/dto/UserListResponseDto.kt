package com.example.githubusers.data.remote.dto

import com.example.githubusers.domain.entity.UserListResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for GitHub user list API response.
 * Used for both search results and regular user listing.
 */
@Serializable
data class UserListResponseDto(
    @SerialName("total_count") val totalCount: Int? = null, // Null for browse endpoint
    @SerialName("incomplete_results") val incompleteResults: Boolean? = null, // Null for browse endpoint
    val items: List<UserDto>,
)

/**
 * Maps UserListResponseDto to UserListResponse domain entity
 */
fun UserListResponseDto.toEntity(): UserListResponse =
    UserListResponse(
        totalCount = totalCount,
        incompleteResults = incompleteResults ?: false,
        items = items.map { it.toEntity() },
    )
