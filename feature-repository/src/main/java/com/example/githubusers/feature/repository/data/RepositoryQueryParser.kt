package com.example.githubusers.feature.repository.data

import com.example.githubusers.core.search.SearchQueryNormalizer

/**
 * Adapts generic normalised queries for the repository feature.
 */
object RepositoryQueryParser {
    data class ParsedQuery(val networkQuery: String, val language: String?, val plainQuery: String?)

    fun parse(normalized: SearchQueryNormalizer.NormalizedSearchQuery, fallbackQuery: String): ParsedQuery {
        val networkQuery = normalized.original.ifBlank { fallbackQuery }
        val language = normalized.firstQualifier("language")
        val terms = normalized.joinedTerms(delimiter = " ")

        return ParsedQuery(
            networkQuery = networkQuery,
            language = language,
            plainQuery = terms
        )
    }
}
