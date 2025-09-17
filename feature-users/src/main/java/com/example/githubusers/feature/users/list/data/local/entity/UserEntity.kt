package com.example.githubusers.feature.users.list.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for caching user data.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Long,
    val login: String,
    val avatarUrl: String,
    val type: String,
    val htmlUrl: String,
    val siteAdmin: Boolean
)
