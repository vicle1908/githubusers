package com.example.githubusers.feature.repository.domain.model

/**
 * Domain model representing a GitHub repository summary displayed in the list screen.
 */
data class Repository(
    val id: Long,
    val name: String,
    val fullName: String,
    val ownerLogin: String,
    val description: String?,
    val htmlUrl: String,
    val stargazersCount: Int,
    val watchersCount: Int,
    val language: String?,
    val forksCount: Int,
    val openIssuesCount: Int,
    val licenseName: String?
)
