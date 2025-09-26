package com.example.githubusers.feature.users.data.list.mapper

import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.users.data.local.entity.UserSummaryEntity
import com.example.githubusers.feature.users.data.remote.dto.UserSummaryDto

/**
 * Mappers for converting between data layer models and domain entities.
 */

fun UserSummaryDto.toDomainModel(): UserSummary = UserSummary(
    id = id.toLong(),
    login = login,
    avatarUrl = avatarUrl,
    htmlUrl = htmlUrl,
    type = type
)

fun UserSummaryDto.toEntity(): UserSummaryEntity = UserSummaryEntity(
    id = id,
    login = login,
    avatarUrl = avatarUrl,
    htmlUrl = htmlUrl,
    type = type
)

fun UserSummaryEntity.toDomainModel(): UserSummary = UserSummary(
    id = id.toLong(),
    login = login,
    avatarUrl = avatarUrl,
    htmlUrl = htmlUrl,
    type = type
)

fun UserSummary.toEntity(): UserSummaryEntity = UserSummaryEntity(
    id = id.toSafeInt(),
    login = login,
    avatarUrl = avatarUrl,
    htmlUrl = htmlUrl,
    type = type
)

private fun Long.toSafeInt(): Int = when {
    this > Int.MAX_VALUE -> Int.MAX_VALUE
    this < Int.MIN_VALUE -> Int.MIN_VALUE
    else -> toInt()
}
