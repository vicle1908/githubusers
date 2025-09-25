package com.example.githubusers.feature.repository.data.mapper

import com.example.githubusers.feature.repository.data.remote.RepositoryDetailDto
import com.example.githubusers.feature.repository.domain.model.RepositoryDetail

fun RepositoryDetailDto.toDomain(): RepositoryDetail = RepositoryDetail(
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
    licenseName = license?.name,
    defaultBranch = defaultBranch,
    createdAt = createdAt,
    updatedAt = updatedAt,
    pushedAt = pushedAt,
    size = size,
    sshUrl = sshUrl,
    cloneUrl = cloneUrl,
    homepage = homepage,
    topics = topics
)
