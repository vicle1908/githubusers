package com.example.githubusers.feature.search.data.model

import kotlinx.serialization.SerialName
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
    @SerialName("avatar_url") val avatarUrl: String,
    val bio: String?,
    val location: String?,
    val company: String?,
    @SerialName("public_repos") val publicRepos: Int,
    val followers: Int,
    val following: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)
