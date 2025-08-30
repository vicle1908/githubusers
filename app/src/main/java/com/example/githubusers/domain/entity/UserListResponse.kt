package com.example.githubusers.domain.entity

/**
 * Domain entity for user list response.
 * Contains list of users and metadata about the response.
 */
data class UserListResponse(
    // Total number of users (null for browse endpoint)
    val totalCount: Int?,
    // Whether the results are incomplete
    val incompleteResults: Boolean,
    // List of users
    val items: List<User>,
)
