package com.example.githubusers.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/**
 * Room entity for caching GitHub users
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,
    
    @ColumnInfo(name = "login")
    val login: String,
    
    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String,
    
    @ColumnInfo(name = "html_url")
    val htmlUrl: String,
    
    @ColumnInfo(name = "type")
    val type: String,
    
    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "page")
    val page: Int = 0 // For pagination tracking
)
