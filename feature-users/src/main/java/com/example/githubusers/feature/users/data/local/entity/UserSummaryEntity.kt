package com.example.githubusers.feature.users.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for caching user summaries.
 */
@Entity(tableName = "user_summaries")
data class UserSummaryEntity(
    @PrimaryKey val id: Int,
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val type: String
)
