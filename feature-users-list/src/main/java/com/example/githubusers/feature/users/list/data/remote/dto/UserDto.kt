package com.example.githubusers.feature.users.list.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for user API responses.
 */
@Serializable
data class UserDto(
    @SerialName("id")
    val id: Long,
    @SerialName("login")
    val login: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("type")
    val type: String,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("site_admin")
    val siteAdmin: Boolean,
    @SerialName("url")
    val url: String? = null,
    @SerialName("followers_url")
    val followersUrl: String? = null,
    @SerialName("following_url")
    val followingUrl: String? = null,
    @SerialName("repos_url")
    val reposUrl: String? = null,
)
