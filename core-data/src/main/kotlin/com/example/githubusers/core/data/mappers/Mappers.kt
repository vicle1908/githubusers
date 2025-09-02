package com.example.githubusers.core.data.mappers

import com.example.githubusers.core.data.api.GitHubUser
import com.example.githubusers.core.data.api.GitHubUserDetail
import com.example.githubusers.core.data.api.Repository
import com.example.githubusers.core.data.local.entity.RepositoryEntity
import com.example.githubusers.core.data.local.entity.UserDetailEntity
import com.example.githubusers.core.data.local.entity.UserEntity
import com.example.githubusers.core.domain.entity.Repo
import com.example.githubusers.core.domain.entity.User
import com.example.githubusers.core.domain.entity.UserDetail

fun GitHubUser.toEntity(page: Int = 0) =
    UserEntity(
        id = id,
        login = login,
        avatarUrl = avatar_url,
        htmlUrl = html_url,
        type = type,
        page = page,
    )

fun GitHubUserDetail.toEntity() =
    UserDetailEntity(
        id = id,
        login = login,
        avatarUrl = avatar_url,
        htmlUrl = html_url,
        name = name,
        company = company,
        blog = blog,
        location = location,
        email = email,
        bio = bio,
        publicRepos = public_repos,
        publicGists = public_gists,
        followers = followers,
        following = following,
        createdAt = created_at,
        updatedAt = updated_at,
    )

fun Repository.toEntity() =
    RepositoryEntity(
        id = id,
        name = name,
        fullName = full_name,
        ownerId = owner.id,
        ownerLogin = owner.login,
        ownerAvatarUrl = owner.avatar_url,
        private = private,
        htmlUrl = html_url,
        description = description,
        fork = fork,
        createdAt = created_at,
        updatedAt = updated_at,
        pushedAt = pushed_at,
        homepage = homepage,
        size = size,
        stargazersCount = stargazers_count,
        watchersCount = watchers_count,
        language = language,
        forksCount = forks_count,
        openIssuesCount = open_issues_count,
        defaultBranch = default_branch,
    )

fun UserEntity.toDomain() =
    User(
        id = id.toInt(),
        login = login,
        avatarUrl = avatarUrl,
        htmlUrl = htmlUrl,
    )

fun UserDetailEntity.toDomain() =
    UserDetail(
        id = id.toInt(),
        login = login,
        avatarUrl = avatarUrl,
        htmlUrl = htmlUrl,
        location = location,
        followers = followers,
        following = following,
        blog = blog,
    )

fun RepositoryEntity.toDomain() =
    Repo(
        id = id,
        name = name,
        fullName = fullName,
        ownerLogin = ownerLogin,
        ownerAvatarUrl = ownerAvatarUrl,
        description = description,
        stargazersCount = stargazersCount,
        forksCount = forksCount,
        language = language,
        htmlUrl = htmlUrl,
        updatedAt = updatedAt,
    )
