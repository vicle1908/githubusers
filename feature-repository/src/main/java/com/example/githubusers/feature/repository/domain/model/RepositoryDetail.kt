package com.example.githubusers.feature.repository.domain.model

data class RepositoryDetail(
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
    val licenseName: String?,
    val defaultBranch: String,
    val createdAt: String,
    val updatedAt: String,
    val pushedAt: String,
    val size: Int,
    val sshUrl: String,
    val cloneUrl: String,
    val homepage: String?,
    val topics: List<String>
)
