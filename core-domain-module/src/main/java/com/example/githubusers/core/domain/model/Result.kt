package com.example.githubusers.core.domain.model

/**
 * A discriminated union that encapsulates a successful outcome with a value of type [T]
 * or a failure with an arbitrary [Throwable] exception.
 */
sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val exception: Throwable) : Result<T>()
    
    /**
     * Returns `true` if this instance represents a successful outcome.
     */
    val isSuccess: Boolean get() = this is Success
    
    /**
     * Returns `true` if this instance represents a failed outcome.
     */
    val isFailure: Boolean get() = this is Error
    
    /**
     * Returns the encapsulated value if this instance represents success or `null` if it is failure.
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }
    
    /**
     * Returns the encapsulated value if this instance represents success or the default value.
     */
    fun getOrDefault(defaultValue: T): T {
        return when (this) {
            is Success -> data
            is Error -> defaultValue
        }
    }
    
    /**
     * Performs the given [action] on the encapsulated value if this instance represents success.
     * Returns the original `Result` unchanged.
     */
    inline fun onSuccess(action: (value: T) -> Unit): Result<T> {
        if (this is Success) {
            action(data)
        }
        return this
    }
    
    /**
     * Performs the given [action] on the encapsulated [Throwable] exception if this instance represents failure.
     * Returns the original `Result` unchanged.
     */
    inline fun onFailure(action: (exception: Throwable) -> Unit): Result<T> {
        if (this is Error) {
            action(exception)
        }
        return this
    }
    
    /**
     * Returns a new [Result], mapping the [Success] value using the given [transform] function.
     */
    inline fun <R> map(transform: (value: T) -> R): Result<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(exception)
        }
    }
    
    companion object {
        /**
         * Calls the specified function [block] and returns its encapsulated result if invocation was successful,
         * catching any [Throwable] exception that was thrown from the [block] function execution and encapsulating it as a failure.
         */
        inline fun <T> runCatching(block: () -> T): Result<T> {
            return try {
                Success(block())
            } catch (e: Throwable) {
                Error(e)
            }
        }
    }
}
