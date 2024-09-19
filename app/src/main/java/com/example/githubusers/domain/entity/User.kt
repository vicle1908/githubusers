package com.example.githubusers.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents the basic user data in the domain layer.
 */
@Entity(tableName = "github_users")
data class User(
    @PrimaryKey val id: Int,
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String
)
