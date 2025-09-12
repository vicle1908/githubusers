package com.example.githubusers.feature.search.data.model

import kotlinx.serialization.Serializable

/**
 * GitHub user data model for search results
 * Owned by feature-search module per feature-based architecture
 */
@Serializable
data class GitHubUser(
    val id: Long,
    val login: String,
    val avatar_url: String,
    val type: String,
    val score: Double? = null
)
