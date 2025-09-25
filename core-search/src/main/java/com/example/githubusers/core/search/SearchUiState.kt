package com.example.githubusers.core.search

/**
 * Small UI-facing snapshot describing the current search context.
 */
data class SearchUiState(val query: String = "", val isSearching: Boolean = false, val lastError: Throwable? = null)
