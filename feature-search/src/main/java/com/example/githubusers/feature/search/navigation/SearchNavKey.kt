package com.example.githubusers.feature.search.navigation

import androidx.navigation3.runtime.NavKey
import com.example.githubusers.core.search.domain.SearchFilter
import kotlinx.serialization.Serializable

sealed interface SearchNavKey : NavKey {
    @Serializable
    data class Search(val query: String? = null, val origin: String? = null, val filter: SearchFilter? = null) :
        SearchNavKey
}
