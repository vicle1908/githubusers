package com.example.githubusers.feature.users.data.detail.mapper

import com.example.githubusers.core.model.RepositoryDto
import com.example.githubusers.feature.users.data.local.entity.RepositoryEntity
import com.example.githubusers.feature.users.domain.model.Repository
import com.example.githubusers.feature.users.domain.model.RepositoryLicense
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
        stargazersCount = stargazersCount ?: 0,
        watchersCount = watchersCount ?: 0,
        forksCount = forksCount ?: 0,
        openIssuesCount = openIssuesCount ?: 0,
        isPrivate = private,
        isFork = fork,
        createdAt = createdAt?.let { parseInstant(it) } ?: Instant.now(),
        updatedAt = updatedAt?.let { parseInstant(it) } ?: Instant.now(),
        pushedAt = pushedAt?.let { parseInstant(it) },
        size = size ?: 0,
        defaultBranch = defaultBranch ?: "",
        topics = topics,
        license = license?.let { RepositoryLicense(it.key, it.name, it.spdxId, it.url) },
        visibility = visibility ?: ""
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
        stargazersCount = stargazersCount ?: 0,
        watchersCount = watchersCount ?: 0,
        forksCount = forksCount ?: 0,
        openIssuesCount = openIssuesCount ?: 0,
        isPrivate = private,
        isFork = fork,
        createdAt = createdAt?.let { parseInstant(it) } ?: Instant.now(),
        updatedAt = updatedAt?.let { parseInstant(it) } ?: Instant.now(),
        pushedAt = pushedAt?.let { parseInstant(it) },
        size = size ?: 0,
        defaultBranch = defaultBranch ?: "",
        topics = topics,
        licenseKey = license?.key,
        licenseName = license?.name,
        visibility = visibility ?: ""
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
            RepositoryLicense(licenseKey, licenseName, null, null)
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
