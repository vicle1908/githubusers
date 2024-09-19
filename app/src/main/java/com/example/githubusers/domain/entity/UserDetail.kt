package com.example.githubusers.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents detailed user data in the domain layer.
 */
@Entity(tableName = "github_user_details")
data class UserDetail(
    @PrimaryKey val id: Int,
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val location: String?,
    val followers: Int,
    val following: Int,
    val blog: String?
)
