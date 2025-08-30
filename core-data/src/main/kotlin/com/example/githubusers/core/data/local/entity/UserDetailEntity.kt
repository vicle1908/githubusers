package com.example.githubusers.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/**
 * Room entity for caching detailed GitHub user information
 */
@Entity(tableName = "user_details")
data class UserDetailEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,
    
    @ColumnInfo(name = "login")
    val login: String,
    
    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String,
    
    @ColumnInfo(name = "html_url")
    val htmlUrl: String,
    
    @ColumnInfo(name = "name")
    val name: String?,
    
    @ColumnInfo(name = "company")
    val company: String?,
    
    @ColumnInfo(name = "blog")
    val blog: String?,
    
    @ColumnInfo(name = "location")
    val location: String?,
    
    @ColumnInfo(name = "email")
    val email: String?,
    
    @ColumnInfo(name = "bio")
    val bio: String?,
    
    @ColumnInfo(name = "public_repos")
    val publicRepos: Int,
    
    @ColumnInfo(name = "public_gists")
    val publicGists: Int,
    
    @ColumnInfo(name = "followers")
    val followers: Int,
    
    @ColumnInfo(name = "following")
    val following: Int,
    
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: String,
    
    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
)
