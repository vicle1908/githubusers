package com.example.githubusers.data.remote.dto

import com.example.githubusers.domain.entity.UserDetail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents detailed user data fetched from the API.
 */
@Serializable
data class UserDetailDto(
    @SerialName("id") val id: Int,
    @SerialName("login") val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("location") val location: String?,
    @SerialName("followers") val followers: Int,
    @SerialName("following") val following: Int,
    @SerialName("blog") val blog: String?
)

fun UserDetailDto.toEntity(): UserDetail =
    UserDetail(
        id = this.id,
        login = this.login,
        avatarUrl = this.avatarUrl,
        htmlUrl = this.htmlUrl,
        location = this.location,
        followers = this.followers,
        following = this.following,
        blog = this.blog,
    )
