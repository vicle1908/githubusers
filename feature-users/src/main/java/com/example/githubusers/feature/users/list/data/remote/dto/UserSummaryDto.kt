package com.example.githubusers.feature.users.list.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data transfer object for user summary from GitHub API.
 */
@Serializable
data class UserSummaryDto(
    @SerialName("id") val id: Int,
    @SerialName("login") val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("type") val type: String = "User",
    @SerialName("site_admin") val siteAdmin: Boolean = false
)
