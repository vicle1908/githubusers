package com.example.githubusers.feature.users.detail.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Room entity for caching user details.
 */
@Entity(tableName = "user_details")
data class UserDetailEntity(
    @PrimaryKey
    val login: String,
    val id: Long,
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
    val cachedAt: Instant = Instant.now(),
)
