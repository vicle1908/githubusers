package com.example.githubusers.feature.search.presentation.navigation

/**
 * Navigator interface for search feature
 */
interface SearchNavigator {
    fun navigateToUserDetail(username: String)

    fun navigateToRepository(owner: String, name: String)

    fun navigateBack()
}
