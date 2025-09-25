package com.example.githubusers.feature.search.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRepository(
    val id: Long,
    val name: String,
    @SerialName("full_name") val fullName: String,
    val owner: GitHubRepositoryOwner,
    val description: String? = null,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("stargazers_count") val stargazersCount: Int,
    @SerialName("language") val primaryLanguage: String? = null,
    @SerialName("forks_count") val forksCount: Int,
    @SerialName("open_issues_count") val openIssuesCount: Int,
    val score: Float = 0f
)

@Serializable
data class GitHubRepositoryOwner(
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String? = null
)
