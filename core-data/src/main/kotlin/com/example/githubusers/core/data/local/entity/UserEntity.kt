package com.example.githubusers.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
    // For pagination tracking
    @ColumnInfo(name = "page")
    val page: Int = 0,
)
