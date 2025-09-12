package com.example.githubusers.feature.search.data.model

import kotlinx.serialization.Serializable

/**
 * GitHub user detail data model
 * Owned by feature-search module per feature-based architecture
 */
@Serializable
data class GitHubUserDetail(
    val id: Long,
    val login: String,
    val name: String?,
    val avatar_url: String,
    val bio: String?,
    val location: String?,
    val company: String?,
    val public_repos: Int,
    val followers: Int,
    val following: Int,
    val created_at: String,
    val updated_at: String
)
