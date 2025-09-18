package com.example.githubusers.feature.users.domain.entity

/**
 * User domain entity
 * Owned by feature-users module per feature-based architecture
 */
data class User(val id: Long, val login: String, val avatarUrl: String, val htmlUrl: String, val type: String)
