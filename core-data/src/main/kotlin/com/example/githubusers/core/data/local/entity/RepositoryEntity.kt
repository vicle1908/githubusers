package com.example.githubusers.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Embedded

/**
 * Room entity for caching GitHub repositories
 */
@Entity(tableName = "repositories")
data class RepositoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "full_name")
    val fullName: String,
    
    @ColumnInfo(name = "owner_id")
    val ownerId: Long,
    
    @ColumnInfo(name = "owner_login")
    val ownerLogin: String,
    
    @ColumnInfo(name = "owner_avatar_url")
    val ownerAvatarUrl: String,
    
    @ColumnInfo(name = "private")
    val private: Boolean,
    
    @ColumnInfo(name = "html_url")
    val htmlUrl: String,
    
    @ColumnInfo(name = "description")
    val description: String?,
    
    @ColumnInfo(name = "fork")
    val fork: Boolean,
    
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: String,
    
    @ColumnInfo(name = "pushed_at")
    val pushedAt: String?,
    
    @ColumnInfo(name = "homepage")
    val homepage: String?,
    
    @ColumnInfo(name = "size")
    val size: Int,
    
    @ColumnInfo(name = "stargazers_count")
    val stargazersCount: Int,
    
    @ColumnInfo(name = "watchers_count")
    val watchersCount: Int,
    
    @ColumnInfo(name = "language")
    val language: String?,
    
    @ColumnInfo(name = "forks_count")
    val forksCount: Int,
    
    @ColumnInfo(name = "open_issues_count")
    val openIssuesCount: Int,
    
    @ColumnInfo(name = "default_branch")
    val defaultBranch: String,
    
    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
)
