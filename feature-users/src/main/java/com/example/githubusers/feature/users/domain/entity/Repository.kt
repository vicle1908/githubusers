package com.example.githubusers.feature.users.domain.entity

/**
 * Repository domain entity
 * Owned by feature-users module per feature-based architecture
 */
data class Repository(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val htmlUrl: String,
    val language: String?,
    val stargazersCount: Int,
    val forksCount: Int,
    val isPrivate: Boolean,
    val createdAt: String,
    val updatedAt: String,
)
