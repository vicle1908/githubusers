package com.example.githubusers.core.users.domain

/**
 * Shared domain representation for lightweight user rows rendered across browse and search.
 */
data class UserSummary(
    val id: Long,
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val type: String = "User"
)
