package com.example.githubusers.feature.users.detail.data.mapper

import com.example.githubusers.feature.users.detail.data.local.entity.RepositoryEntity
import com.example.githubusers.feature.users.detail.data.remote.dto.RepositoryDto
import com.example.githubusers.feature.users.detail.domain.entity.License
import com.example.githubusers.feature.users.detail.domain.entity.Repository
import java.time.Instant

/**
 * Mapper for Repository conversions.
 */
object RepositoryMapper {
    /**
     * Convert DTO to domain model.
     */
    fun RepositoryDto.toDomain(): Repository = Repository(
        id = id,
        name = name,
        fullName = fullName,
        description = description,
        htmlUrl = htmlUrl,
        language = language,
        stargazersCount = stargazersCount,
        watchersCount = watchersCount,
        forksCount = forksCount,
        openIssuesCount = openIssuesCount,
        isPrivate = private,
        isFork = fork,
        createdAt = parseInstant(createdAt),
        updatedAt = parseInstant(updatedAt),
        pushedAt = pushedAt?.let { parseInstant(it) },
        size = size,
        defaultBranch = defaultBranch,
        topics = topics,
        license = license?.let { License(it.key, it.name, it.spdxId, it.url) },
        visibility = visibility
    )

    /**
     * Convert DTO to entity for caching.
     */
    fun RepositoryDto.toEntity(ownerLogin: String): RepositoryEntity = RepositoryEntity(
        id = id,
        ownerLogin = ownerLogin,
        name = name,
        fullName = fullName,
        description = description,
        htmlUrl = htmlUrl,
        language = language,
        stargazersCount = stargazersCount,
        watchersCount = watchersCount,
        forksCount = forksCount,
        openIssuesCount = openIssuesCount,
        isPrivate = private,
        isFork = fork,
        createdAt = parseInstant(createdAt),
        updatedAt = parseInstant(updatedAt),
        pushedAt = pushedAt?.let { parseInstant(it) },
        size = size,
        defaultBranch = defaultBranch,
        topics = topics,
        licenseKey = license?.key,
        licenseName = license?.name,
        visibility = visibility
    )

    /**
     * Convert entity to domain model.
     */
    fun RepositoryEntity.toDomain(): Repository = Repository(
        id = id,
        name = name,
        fullName = fullName,
        description = description,
        htmlUrl = htmlUrl,
        language = language,
        stargazersCount = stargazersCount,
        watchersCount = watchersCount,
        forksCount = forksCount,
        openIssuesCount = openIssuesCount,
        isPrivate = isPrivate,
        isFork = isFork,
        createdAt = createdAt,
        updatedAt = updatedAt,
        pushedAt = pushedAt,
        size = size,
        defaultBranch = defaultBranch,
        topics = topics,
        license =
        if (licenseKey != null && licenseName != null) {
            License(licenseKey, licenseName, null, null)
        } else {
            null
        },
        visibility = visibility
    )

    private fun parseInstant(dateString: String): Instant = try {
        Instant.parse(dateString)
    } catch (e: Exception) {
        Instant.now()
    }
}
