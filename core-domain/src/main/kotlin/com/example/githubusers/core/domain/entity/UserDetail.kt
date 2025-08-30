package com.example.githubusers.core.domain.entity

data class UserDetail(
    val id: Int,
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val location: String?,
    val followers: Int,
    val following: Int,
    val blog: String?,
)

