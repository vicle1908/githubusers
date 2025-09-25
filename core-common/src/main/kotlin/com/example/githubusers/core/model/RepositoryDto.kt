package com.example.githubusers.core.model

// Consolidated RepositoryDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepositoryDto(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("private") val private: Boolean,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("description") val description: String? = null,
    @SerialName("fork") val fork: Boolean,
    @SerialName("url") val url: String,
    @SerialName("stargazers_count") val stargazersCount: Int,
    @SerialName("watchers_count") val watchersCount: Int,
    @SerialName("language") val language: String? = null,
    @SerialName("forks_count") val forksCount: Int,
    @SerialName("open_issues_count") val openIssuesCount: Int,
    @SerialName("license") val license: LicenseDto? = null,
    @SerialName("owner") val owner: OwnerDto
)

@Serializable
data class LicenseDto(
    @SerialName("key") val key: String,
    @SerialName("name") val name: String,
    @SerialName("url") val url: String? = null
)

@Serializable
data class OwnerDto(
    @SerialName("login") val login: String,
    @SerialName("id") val id: Long,
    @SerialName("type") val type: String,
    @SerialName("url") val url: String,
    @SerialName("avatar_url") val avatarUrl: String
)