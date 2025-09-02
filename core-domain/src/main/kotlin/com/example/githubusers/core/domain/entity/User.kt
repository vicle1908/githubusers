package com.example.githubusers.core.domain.entity

data class User(
    val id: Int,
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String? = null,
)
