package com.example.githubusers.feature.users.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Room entity for caching repositories.
 */
@Entity(tableName = "repositories")
data class RepositoryEntity(
    @PrimaryKey val id: Long,
    val ownerLogin: String,
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
    val licenseKey: String?,
    val licenseName: String?,
    val visibility: String,
    val cachedAt: Instant = Instant.now()
)
