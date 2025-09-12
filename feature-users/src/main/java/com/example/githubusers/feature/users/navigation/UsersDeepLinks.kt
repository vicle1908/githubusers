package com.example.githubusers.feature.users.navigation

/** Feature-owned deep links for Users. */
object UsersDeepLinks {
    const val LIST: String = "app://users/list"

    fun detail(username: String): String = "app://users/user/$username"
}
