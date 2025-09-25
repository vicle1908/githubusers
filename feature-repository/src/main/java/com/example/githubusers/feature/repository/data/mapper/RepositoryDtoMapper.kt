package com.example.githubusers.feature.repository.data.mapper

import com.example.githubusers.feature.repository.data.local.RepositoryEntity
import com.example.githubusers.feature.repository.data.remote.RepositoryDto

fun RepositoryDto.toEntity(): RepositoryEntity = RepositoryEntity(
    id = id,
    name = name,
    fullName = fullName,
    ownerLogin = owner.login,
    description = description,
    htmlUrl = htmlUrl,
    stargazersCount = stargazersCount,
    watchersCount = watchersCount,
    language = language,
    forksCount = forksCount,
    openIssuesCount = openIssuesCount,
    licenseName = license?.name
)
