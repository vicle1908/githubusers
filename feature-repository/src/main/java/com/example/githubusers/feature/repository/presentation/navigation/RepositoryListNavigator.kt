package com.example.githubusers.feature.repository.presentation.navigation

interface RepositoryListNavigator {
    fun openRepositoryDetails(owner: String, name: String)
    fun openSearch(initialQuery: String)
}
