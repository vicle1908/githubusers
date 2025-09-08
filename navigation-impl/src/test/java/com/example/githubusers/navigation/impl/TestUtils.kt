package com.example.githubusers.navigation.impl

import com.example.githubusers.navigation.api.NavigationDestination

/**
 * Shared test utilities for navigation-impl tests
 */
object TestUtils {
    // Mock destinations for testing
    data class MockUserList(
        val dummy: String = "dummy",
    ) : NavigationDestination {
        override val route: String = "users/list"
        override val deepLink: String = "app://users/list"
    }

    data class MockUserDetail(
        val username: String,
    ) : NavigationDestination {
        override val route: String = "users/detail/$username"
        override val deepLink: String = "app://users/user/$username"
    }

    data class MockSearch(
        val query: String? = null,
    ) : NavigationDestination {
        override val route: String = "search"
        override val deepLink: String =
            "app://search${query?.let {
                "?q=${java.net.URLEncoder.encode(it, "UTF-8").replace("+", "%20")}"
            } ?: ""}"
    }
}
