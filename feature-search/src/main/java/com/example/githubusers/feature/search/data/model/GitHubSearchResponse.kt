package com.example.githubusers.feature.search.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GitHub search response data model
 * Owned by feature-search module per feature-based architecture
 */
@Serializable
data class GitHubSearchResponse(
    @SerialName("total_count") val totalCount: Int,
    @SerialName("incomplete_results") val incompleteResults: Boolean,
    val items: List<GitHubUser>
)
