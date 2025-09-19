package com.example.githubusers.feature.users.detail.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for user detail response from GitHub API.
 */
@Serializable
data class UserDetailDto(
    @SerialName("id")
    val id: Long,
    @SerialName("login")
    val login: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("name")
    val name: String? = null,
    @SerialName("company")
    val company: String? = null,
    @SerialName("blog")
    val blog: String? = null,
    @SerialName("location")
    val location: String? = null,
    @SerialName("email")
    val email: String? = null,
    @SerialName("bio")
    val bio: String? = null,
    @SerialName("twitter_username")
    val twitterUsername: String? = null,
    @SerialName("public_repos")
    val publicRepos: Int,
    @SerialName("public_gists")
    val publicGists: Int,
    @SerialName("followers")
    val followers: Int,
    @SerialName("following")
    val following: Int,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("type")
    val type: String,
    @SerialName("site_admin")
    val siteAdmin: Boolean,
    @SerialName("hireable")
    val hireable: Boolean? = null,
    @SerialName("gravatar_id")
    val gravatarId: String? = null,
    @SerialName("url")
    val url: String,
    @SerialName("followers_url")
    val followersUrl: String,
    @SerialName("following_url")
    val followingUrl: String,
    @SerialName("gists_url")
    val gistsUrl: String,
    @SerialName("starred_url")
    val starredUrl: String,
    @SerialName("subscriptions_url")
    val subscriptionsUrl: String,
    @SerialName("organizations_url")
    val organizationsUrl: String,
    @SerialName("repos_url")
    val reposUrl: String,
    @SerialName("events_url")
    val eventsUrl: String,
    @SerialName("received_events_url")
    val receivedEventsUrl: String
)
