package com.example.githubusers.feature.users.list.data.mapper

import com.example.githubusers.feature.users.list.data.local.entity.UserSummaryEntity
import com.example.githubusers.feature.users.list.data.remote.dto.UserSummaryDto
import com.example.githubusers.feature.users.list.domain.entity.UserSummary

/**
 * Mappers for converting between data layer models and domain entities.
 */

fun UserSummaryDto.toDomainModel(): UserSummary =
    UserSummary(
        id = id,
        login = login,
        avatarUrl = avatarUrl,
        htmlUrl = htmlUrl,
        type = type,
    )

fun UserSummaryDto.toEntity(): UserSummaryEntity =
    UserSummaryEntity(
        id = id,
        login = login,
        avatarUrl = avatarUrl,
        htmlUrl = htmlUrl,
        type = type,
    )

fun UserSummaryEntity.toDomainModel(): UserSummary =
    UserSummary(
        id = id,
        login = login,
        avatarUrl = avatarUrl,
        htmlUrl = htmlUrl,
        type = type,
    )

fun UserSummary.toEntity(): UserSummaryEntity =
    UserSummaryEntity(
        id = id,
        login = login,
        avatarUrl = avatarUrl,
        htmlUrl = htmlUrl,
        type = type,
    )
