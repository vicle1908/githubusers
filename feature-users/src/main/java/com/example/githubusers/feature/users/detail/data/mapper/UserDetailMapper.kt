package com.example.githubusers.feature.users.detail.data.mapper

import com.example.githubusers.feature.users.detail.data.local.entity.UserDetailEntity
import com.example.githubusers.feature.users.detail.data.remote.dto.UserDetailDto
import com.example.githubusers.feature.users.detail.domain.entity.UserDetail
import java.time.Instant

/**
 * Mapper for UserDetail conversions.
 */
object UserDetailMapper {
    /**
     * Convert DTO to domain model.
     */
    fun UserDetailDto.toDomain(): UserDetail = UserDetail(
        id = id,
        login = login,
        avatarUrl = avatarUrl,
        htmlUrl = htmlUrl,
        name = name,
        company = company,
        blog = blog,
        location = location,
        email = email,
        bio = bio,
        twitterUsername = twitterUsername,
        publicRepos = publicRepos,
        publicGists = publicGists,
        followers = followers,
        following = following,
        createdAt = parseInstant(createdAt),
        updatedAt = parseInstant(updatedAt),
        type = type,
        siteAdmin = siteAdmin,
        hireable = hireable
    )

    /**
     * Convert domain model to entity for caching.
     */
    fun UserDetail.toEntity(): UserDetailEntity = UserDetailEntity(
        id = id,
        login = login,
        avatarUrl = avatarUrl,
        htmlUrl = htmlUrl,
        name = name,
        company = company,
        blog = blog,
        location = location,
        email = email,
        bio = bio,
        twitterUsername = twitterUsername,
        publicRepos = publicRepos,
        publicGists = publicGists,
        followers = followers,
        following = following,
        createdAt = createdAt,
        updatedAt = updatedAt,
        type = type,
        siteAdmin = siteAdmin,
        hireable = hireable
    )

    /**
     * Convert entity to domain model.
     */
    fun UserDetailEntity.toDomain(): UserDetail = UserDetail(
        id = id,
        login = login,
        avatarUrl = avatarUrl,
        htmlUrl = htmlUrl,
        name = name,
        company = company,
        blog = blog,
        location = location,
        email = email,
        bio = bio,
        twitterUsername = twitterUsername,
        publicRepos = publicRepos,
        publicGists = publicGists,
        followers = followers,
        following = following,
        createdAt = createdAt,
        updatedAt = updatedAt,
        type = type,
        siteAdmin = siteAdmin,
        hireable = hireable
    )

    /**
     * Convert DTO to entity for caching.
     */
    fun UserDetailDto.toEntity(): UserDetailEntity = UserDetailEntity(
        id = id,
        login = login,
        avatarUrl = avatarUrl,
        htmlUrl = htmlUrl,
        name = name,
        company = company,
        blog = blog,
        location = location,
        email = email,
        bio = bio,
        twitterUsername = twitterUsername,
        publicRepos = publicRepos,
        publicGists = publicGists,
        followers = followers,
        following = following,
        createdAt = parseInstant(createdAt),
        updatedAt = parseInstant(updatedAt),
        type = type,
        siteAdmin = siteAdmin,
        hireable = hireable
    )

    private fun parseInstant(dateString: String): Instant = try {
        Instant.parse(dateString)
    } catch (e: Exception) {
        Instant.now()
    }
}
