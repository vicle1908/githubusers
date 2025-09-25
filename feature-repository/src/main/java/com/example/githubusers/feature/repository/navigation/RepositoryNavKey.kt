package com.example.githubusers.feature.repository.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface RepositoryNavKey : NavKey {
    @Serializable
    data object RepositoryList : RepositoryNavKey

    @Serializable
    data class RepositoryDetail(val owner: String, val name: String) : RepositoryNavKey
}
