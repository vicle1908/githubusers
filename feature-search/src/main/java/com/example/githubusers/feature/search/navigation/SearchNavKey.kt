package com.example.githubusers.feature.search.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface SearchNavKey : NavKey {
    @Serializable
    data class Search(val query: String?) : SearchNavKey
}


