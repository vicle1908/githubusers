package com.example.githubusers.navigation.impl

import android.net.Uri
import com.example.githubusers.navigation.api.AppDeepLinks
import com.example.githubusers.navigation.api.AppDestination
import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private class FakeTypedUsersHandler : DeepLinkHandler {
    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> =
        listOf(
            "githubusers://users",
            "githubusers://user/{username}",
            "githubusers://search",
            "https://githubusers.example.com/users",
            "https://githubusers.example.com/user/{username}",
            "https://githubusers.example.com/search",
        )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val scheme = uri.scheme ?: return null
        val host = uri.host
        val path = uri.path.orEmpty()
        return when {
            // List
            (scheme == "githubusers" && host == "users") || (scheme == "https" && host == "githubusers.example.com" && path == "/users") ->
                DeepLinkResult(destination = AppDestination.UserList)
            // Detail
            (scheme == "githubusers" && host == "user" && uri.pathSegments.size == 1) -> {
                val username = uri.pathSegments.first()
                DeepLinkResult(destination = AppDestination.UserDetail(username))
            }
            (scheme == "https" && host == "githubusers.example.com" && path.startsWith("/user/")) -> {
                val username = path.removePrefix("/user/")
                DeepLinkResult(destination = AppDestination.UserDetail(username))
            }
            // Search
            ((scheme == "githubusers" || scheme == "https") && (host == "search" || path.startsWith("/search"))) -> {
                val q = uri.getQueryParameter("q")
                DeepLinkResult(destination = AppDestination.Search(q))
            }
            else -> null
        }
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DefaultDestinationResolverTypedTest {
    private fun resolver(): DestinationResolver = DefaultDestinationResolver(setOf(FakeTypedUsersHandler()))

    @Test
    fun resolves_userList_from_typed_handler() {
        val r = resolver()
        val dest = r.resolve("githubusers://users")
        assertNotNull(dest)
        assertEquals("githubusers://users", dest!!.deepLink)
    }

    @Test
    fun resolves_userDetail_from_typed_handler() {
        val r = resolver()
        val link = AppDeepLinks.build(AppDestination.UserDetail("octocat"))
        val dest = r.resolve(link)
        assertNotNull(dest)
        assertEquals("githubusers://user/octocat", dest!!.deepLink)
    }

    @Test
    fun resolves_search_from_typed_handler_with_query() {
        val r = resolver()
        val link = AppDeepLinks.build(AppDestination.Search("android ui"))
        val dest = r.resolve(link)
        assertNotNull(dest)
        assertEquals("githubusers://search?q=android%20ui", dest!!.deepLink)
    }

    @Test
    fun falls_back_to_generic_for_unknown() {
        val r = resolver()
        val dest = r.resolve("githubusers://unknown/path")
        assertNotNull(dest)
        assertEquals("githubusers://unknown/path", dest!!.deepLink)
    }
}
