package com.example.githubusers.feature.users.navigation

import android.net.Uri

object UsersDeepLinks {
    private const val SCHEME = "app"
    private const val USERS_AUTHORITY = "users"
    private const val SETTINGS_AUTHORITY = "settings"
    private const val REPOSITORY_AUTHORITY = "repository"

    const val LIST: String = "$SCHEME://$USERS_AUTHORITY/list"
    const val DETAIL_PATTERN: String = "$SCHEME://$USERS_AUTHORITY/user/{username}"

    fun list(): String = LIST

    fun detail(username: String): String = Uri.Builder()
        .scheme(SCHEME)
        .authority(USERS_AUTHORITY)
        .appendPath("user")
        .appendPath(username)
        .build()
        .toString()

    fun settings(): String = Uri.Builder()
        .scheme(SCHEME)
        .authority(SETTINGS_AUTHORITY)
        .build()
        .toString()

    fun repository(owner: String, repo: String): String = Uri.Builder()
        .scheme(SCHEME)
        .authority(REPOSITORY_AUTHORITY)
        .appendPath(owner)
        .appendPath(repo)
        .build()
        .toString()
}
