package com.example.githubusers.feature.repository.navigation

import android.net.Uri

object RepositoryDeepLinks {
    const val LIST: String = "app://repository/list"
    const val DETAIL_PATTERN: String = "app://repository/{owner}/{name}"

    fun list(): String = LIST

    fun detail(owner: String, name: String): String = Uri.Builder()
        .scheme("app")
        .authority("repository")
        .appendPath(owner)
        .appendPath(name)
        .build()
        .toString()
}
