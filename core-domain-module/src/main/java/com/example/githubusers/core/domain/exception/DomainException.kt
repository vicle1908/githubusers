package com.example.githubusers.core.domain.exception

/**
 * Base class for all domain exceptions.
 */
open class DomainException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Exception thrown when a network error occurs.
 */
class NetworkException(
    message: String = "Network error occurred",
    cause: Throwable? = null
) : DomainException(message, cause)

/**
 * Exception thrown when data is not found.
 */
class DataNotFoundException(
    message: String = "Data not found",
    cause: Throwable? = null
) : DomainException(message, cause)

/**
 * Exception thrown when an API error occurs.
 */
class ApiException(
    val code: Int,
    message: String = "API error occurred",
    cause: Throwable? = null
) : DomainException(message, cause)

/**
 * Exception thrown when authentication fails.
 */
class AuthenticationException(
    message: String = "Authentication failed",
    cause: Throwable? = null
) : DomainException(message, cause)

/**
 * Exception thrown when rate limit is exceeded.
 */
class RateLimitException(
    message: String = "Rate limit exceeded",
    cause: Throwable? = null
) : DomainException(message, cause)
