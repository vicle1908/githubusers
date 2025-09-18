package com.example.githubusers.feature.users.navigation

import android.net.Uri

/**
 * Deep link builders for the Users module.
 * Other modules should use these to navigate to user screens.
 */
object UserDeepLinks {
    /**
     * Navigate to user list screen
     */
    fun userList(filter: String? = null, clearStack: Boolean = false, singleTop: Boolean = true): String = buildString {
        append("app://users")
        val params = mutableListOf<String>()

        filter?.let {
            params.add("filter=${Uri.encode(it)}")
        }
        if (clearStack) {
            params.add("clear_stack=true")
        }
        if (!singleTop) {
            params.add("single_top=false")
        }

        if (params.isNotEmpty()) {
            append("?")
            append(params.joinToString("&"))
        }
    }

    /**
     * Navigate to user detail screen
     */
    fun userDetail(username: String, clearStack: Boolean = false, singleTop: Boolean = true): String = buildString {
        append("app://users/${Uri.encode(username)}")
        val params = mutableListOf<String>()

        if (clearStack) {
            params.add("clear_stack=true")
        }
        if (!singleTop) {
            params.add("single_top=false")
        }

        if (params.isNotEmpty()) {
            append("?")
            append(params.joinToString("&"))
        }
    }

    /**
     * Navigate to user search screen
     */
    fun userSearch(query: String = "", clearStack: Boolean = false, singleTop: Boolean = true): String = buildString {
        append("app://search")
        val params = mutableListOf<String>()

        if (query.isNotEmpty()) {
            params.add("q=${Uri.encode(query)}")
        }
        if (clearStack) {
            params.add("clear_stack=true")
        }
        if (!singleTop) {
            params.add("single_top=false")
        }

        if (params.isNotEmpty()) {
            append("?")
            append(params.joinToString("&"))
        }
    }
}
