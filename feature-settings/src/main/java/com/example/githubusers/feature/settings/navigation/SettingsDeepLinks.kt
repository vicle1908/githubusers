package com.example.githubusers.feature.settings.navigation

import android.net.Uri

object SettingsDeepLinks {
    const val ROOT: String = "app://settings"

    fun root(section: String? = null): String {
        val builder = Uri.Builder()
            .scheme("app")
            .authority("settings")
        section?.takeIf { it.isNotBlank() }?.let { builder.appendQueryParameter("section", it) }
        return builder.build().toString()
    }
}
