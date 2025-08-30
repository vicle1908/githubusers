package com.example.githubusers.feature.users.list.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data transfer object for search response from GitHub API.
 */
@Serializable
data class SearchResponseDto(
    @SerialName("total_count") val totalCount: Int,
    @SerialName("incomplete_results") val incompleteResults: Boolean,
    @SerialName("items") val items: List<UserSummaryDto>,
)
