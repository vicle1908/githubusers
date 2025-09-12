package com.example.githubusers.feature.search.data.model

import kotlinx.serialization.Serializable

/**
 * GitHub search response data model
 * Owned by feature-search module per feature-based architecture
 */
@Serializable
data class GitHubSearchResponse(val total_count: Int, val incomplete_results: Boolean, val items: List<GitHubUser>)
