package com.example.githubusers.feature.users.detail.domain.entity

import java.time.Instant

/**
 * Domain entity representing detailed user information.
 */
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
    val twitterUsername: String?,
    val publicRepos: Int,
    val publicGists: Int,
    val followers: Int,
    val following: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
    val type: String,
    val siteAdmin: Boolean,
    val hireable: Boolean?,
)
