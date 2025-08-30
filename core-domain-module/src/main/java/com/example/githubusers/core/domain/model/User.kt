package com.example.githubusers.core.domain.model

/**
 * Core domain model representing a GitHub user.
 * This is the base user model shared across features.
 */
data class User(
    val id: Long,
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val type: UserType,
    val siteAdmin: Boolean
) {
    companion object {
        /**
         * Creates a sample user for testing/preview purposes.
         */
        fun sample() = User(
            id = 1,
            login = "octocat",
            avatarUrl = "https://avatars.githubusercontent.com/u/1?v=4",
            htmlUrl = "https://github.com/octocat",
            type = UserType.USER,
            siteAdmin = false
        )
    }
}

/**
 * Enum representing different types of GitHub accounts.
 */
enum class UserType {
    USER,
    ORGANIZATION,
    BOT;
    
    companion object {
        fun fromString(value: String): UserType {
            return when (value.uppercase()) {
                "ORGANIZATION" -> ORGANIZATION
                "BOT" -> BOT
                else -> USER
            }
        }
    }
}
