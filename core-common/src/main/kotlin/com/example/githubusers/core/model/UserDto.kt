package com.example.githubusers.core.model

// Consolidated UserDto from features
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("id") val id: Long,
    @SerialName("login") val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("type") val type: String? = null,
    @SerialName("score") val score: Double? = 1.0,
    @SerialName("url") val url: String
)
