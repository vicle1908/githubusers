package com.example.githubusers.feature.users.domain.model

import java.time.Instant

/**
 * Canonical domain model representing a repository belonging to a user.
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
    val license: RepositoryLicense?,
    val visibility: String
)

data class RepositoryLicense(val key: String, val name: String, val spdxId: String?, val url: String?)
