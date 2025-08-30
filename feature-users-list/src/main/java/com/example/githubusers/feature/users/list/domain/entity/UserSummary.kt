package com.example.githubusers.feature.users.list.domain.entity

/**
 * Domain entity representing a user summary in the list.
 * This is a lightweight representation optimized for list display.
 */
data class UserSummary(
    val id: Int,
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val type: String = "User",
)
