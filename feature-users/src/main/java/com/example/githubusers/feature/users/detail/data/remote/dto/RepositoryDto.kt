package com.example.githubusers.feature.users.detail.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for repository response from GitHub API.
 */
@Serializable
data class RepositoryDto(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("language")
    val language: String? = null,
    @SerialName("stargazers_count")
    val stargazersCount: Int,
    @SerialName("watchers_count")
    val watchersCount: Int,
    @SerialName("forks_count")
    val forksCount: Int,
    @SerialName("open_issues_count")
    val openIssuesCount: Int,
    @SerialName("private")
    val private: Boolean,
    @SerialName("fork")
    val fork: Boolean,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("pushed_at")
    val pushedAt: String? = null,
    @SerialName("size")
    val size: Int,
    @SerialName("default_branch")
    val defaultBranch: String,
    @SerialName("topics")
    val topics: List<String> = emptyList(),
    @SerialName("license")
    val license: LicenseDto? = null,
    @SerialName("visibility")
    val visibility: String,
    @SerialName("archived")
    val archived: Boolean = false,
    @SerialName("disabled")
    val disabled: Boolean = false,
    @SerialName("owner")
    val owner: OwnerDto? = null
)

/**
 * DTO for license information.
 */
@Serializable
data class LicenseDto(
    @SerialName("key")
    val key: String,
    @SerialName("name")
    val name: String,
    @SerialName("spdx_id")
    val spdxId: String? = null,
    @SerialName("url")
    val url: String? = null
)

/**
 * DTO for repository owner information.
 */
@Serializable
data class OwnerDto(
    @SerialName("id")
    val id: Long,
    @SerialName("login")
    val login: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("type")
    val type: String
)
