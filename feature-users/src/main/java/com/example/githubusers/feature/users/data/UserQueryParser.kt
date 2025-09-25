package com.example.githubusers.feature.users.data

import com.example.githubusers.core.search.SearchQueryNormalizer

/**
 * Adapts generic normalized queries for the users feature.
 */
object UserQueryParser {
    data class ParsedQuery(val networkQuery: String, val type: String?, val plainQuery: String?)

    fun parse(normalized: SearchQueryNormalizer.NormalizedSearchQuery, fallbackQuery: String): ParsedQuery {
        val networkQuery = normalized.original.ifBlank { fallbackQuery }
        val type = normalized.firstQualifier("type")
        val terms = normalized.joinedTerms(delimiter = " ")

        return ParsedQuery(
            networkQuery = networkQuery,
            type = type,
            plainQuery = terms
        )
    }
}
