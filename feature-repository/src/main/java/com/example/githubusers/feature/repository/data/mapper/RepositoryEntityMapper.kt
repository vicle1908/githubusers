package com.example.githubusers.feature.repository.data.mapper

import com.example.githubusers.feature.repository.data.local.RepositoryEntity
import com.example.githubusers.feature.repository.domain.model.Repository

fun RepositoryEntity.toDomain(): Repository = Repository(
    id = id,
    name = name,
    fullName = fullName,
    ownerLogin = ownerLogin,
    description = description,
    htmlUrl = htmlUrl,
    stargazersCount = stargazersCount,
    watchersCount = watchersCount,
    language = language,
    forksCount = forksCount,
    openIssuesCount = openIssuesCount,
    licenseName = licenseName
)
