package com.example.githubusers.core.search.domain

/**
 * Domain-level error types for search-related operations.
 */
sealed interface SearchError {
    /** Network connectivity or I/O failure (e.g., UnknownHostException). */
    object Network : SearchError

    /** HTTP 3xx/4xx classified as client errors when meaningful to surface. */
    data class Client(val code: Int, val message: String? = null) : SearchError

    /** HTTP 5xx server errors. */
    data class Server(val code: Int, val message: String? = null) : SearchError

    /** API rate limiting (429 or GitHub rate limit variants). */
    object RateLimited : SearchError

    /** Request timed out. */
    object Timeout : SearchError

    /** Unknown or uncategorized error. */
    data class Unknown(val throwable: Throwable? = null) : SearchError
}

/**
 * Exception carrying a domain-level SearchError plus original cause.
 */
class SearchException(val error: SearchError, message: String? = null, cause: Throwable? = null) :
    Exception(message, cause)
