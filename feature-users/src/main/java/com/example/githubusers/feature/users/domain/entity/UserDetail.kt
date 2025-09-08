package com.example.githubusers.feature.users.domain.entity

/**
 * User detail domain entity
 * Owned by feature-users module per feature-based architecture
 */
data class UserDetail(
    val id: Long,
    val login: String,
    val name: String?,
    val avatarUrl: String,
    val htmlUrl: String,
    val bio: String?,
    val location: String?,
    val company: String?,
    val blog: String?,
    val publicRepos: Int,
    val followers: Int,
    val following: Int,
    val createdAt: String,
    val updatedAt: String,
)
