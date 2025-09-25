package com.example.githubusers.feature.repository.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchRepositoriesResponse(
    @SerialName("total_count") val totalCount: Int,
    @SerialName("incomplete_results") val incompleteResults: Boolean,
    val items: List<RepositoryDto>
)

@Serializable
data class RepositoryDto(
    val id: Long,
    val name: String,
    @SerialName("full_name") val fullName: String,
    val owner: RepositoryOwnerDto,
    val description: String? = null,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("stargazers_count") val stargazersCount: Int,
    @SerialName("watchers_count") val watchersCount: Int,
    val language: String? = null,
    @SerialName("forks_count") val forksCount: Int,
    @SerialName("open_issues_count") val openIssuesCount: Int,
    val license: RepositoryLicenseDto? = null
)

@Serializable
data class RepositoryOwnerDto(
    val login: String,
    val id: Long,
    @SerialName("avatar_url") val avatarUrl: String?,
    @SerialName("html_url") val htmlUrl: String
)

@Serializable
data class RepositoryLicenseDto(
    val key: String,
    val name: String,
    @SerialName("spdx_id") val spdxId: String,
    val url: String? = null,
    @SerialName("node_id") val nodeId: String
)
