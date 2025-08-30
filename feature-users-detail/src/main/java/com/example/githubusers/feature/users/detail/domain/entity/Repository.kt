package com.example.githubusers.feature.users.detail.domain.entity

import java.time.Instant

/**
 * Domain entity representing a GitHub repository.
 */
data class Repository(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val htmlUrl: String,
    val language: String?,
    val stargazersCount: Int,
    val watchersCount: Int,
    val forksCount: Int,
    val openIssuesCount: Int,
    val isPrivate: Boolean,
    val isFork: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
    val pushedAt: Instant?,
    val size: Int,
    val defaultBranch: String,
    val topics: List<String>,
    val license: License?,
    val visibility: String,
)

/**
 * Domain entity representing a repository license.
 */
data class License(
    val key: String,
    val name: String,
    val spdxId: String?,
    val url: String?,
)
