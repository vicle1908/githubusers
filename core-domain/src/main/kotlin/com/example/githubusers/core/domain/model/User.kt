package com.example.githubusers.core.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model for a GitHub user
 */
@Serializable
data class User(
    val id: Long,
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val type: String,
)

/**
 * Domain model for detailed user information
 */
@Serializable
data class UserDetail(
    val id: Long,
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val name: String?,
    val company: String?,
    val blog: String?,
    val location: String?,
    val email: String?,
    val bio: String?,
    val publicRepos: Int,
    val publicGists: Int,
    val followers: Int,
    val following: Int,
    val createdAt: String,
    val updatedAt: String,
    val cachedAt: Long? = null,
)

/**
 * Domain model for a GitHub repository
 */
@Serializable
data class Repo(
    val id: Long,
    val name: String,
    val fullName: String,
    val ownerLogin: String,
    val ownerAvatarUrl: String,
    val description: String?,
    val stargazersCount: Int,
    val forksCount: Int,
    val language: String?,
    val htmlUrl: String,
    val updatedAt: String,
)
