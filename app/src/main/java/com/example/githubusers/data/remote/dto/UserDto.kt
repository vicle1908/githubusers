package com.example.githubusers.data.remote.dto

import com.example.githubusers.domain.entity.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the basic user data fetched from the list API.
 */
@Serializable
data class UserDto(
    @SerialName("id") val id: Int,
    @SerialName("login") val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("html_url") val htmlUrl: String,
)

fun UserDto.toEntity(): User =
    User(
        id = this.id,
        login = this.login,
        avatarUrl = this.avatarUrl,
        htmlUrl = this.htmlUrl,
    )
