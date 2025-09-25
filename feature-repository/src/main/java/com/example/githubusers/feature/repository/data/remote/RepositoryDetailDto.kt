package com.example.githubusers.feature.repository.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepositoryDetailDto(
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
    val license: RepositoryLicenseDto? = null,
    @SerialName("default_branch") val defaultBranch: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("pushed_at") val pushedAt: String,
    val size: Int,
    @SerialName("ssh_url") val sshUrl: String,
    @SerialName("clone_url") val cloneUrl: String,
    @SerialName("homepage") val homepage: String? = null,
    @SerialName("topics") val topics: List<String> = emptyList()
)
