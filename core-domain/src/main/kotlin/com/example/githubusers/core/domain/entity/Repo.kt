package com.example.githubusers.core.domain.entity

import kotlinx.serialization.Serializable

/**
 * Domain entity for a GitHub repository
 */
@Serializable
data class Repo(
    val id: Long,
    val name: String,
    val fullName: String,
    val ownerLogin: String,
    val ownerAvatarUrl: String,
    val description: String?,
    val stargazersCount: Int,
    val forksCount: Int,
    val language: String?,
    val htmlUrl: String,
    val updatedAt: String,
)
