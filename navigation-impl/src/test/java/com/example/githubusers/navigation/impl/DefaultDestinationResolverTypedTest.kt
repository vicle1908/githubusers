package com.example.githubusers.navigation.impl

import android.net.Uri
import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkResult
import com.example.githubusers.navigation.impl.TestUtils.MockSearch
import com.example.githubusers.navigation.impl.TestUtils.MockUserDetail
import com.example.githubusers.navigation.impl.TestUtils.MockUserList
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
            "app://users/list",
            "app://users/user/{username}",
            "app://search",
        )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val scheme = uri.scheme ?: return null
        val host = uri.host
        val path = uri.path.orEmpty()
        return when {
            // List
            scheme == "app" && host == "users" && path == "/list" ->
                DeepLinkResult(destination = MockUserList())
            // Detail
            scheme == "app" && host == "users" && path.startsWith("/user/") -> {
                val username = path.removePrefix("/user/")
                DeepLinkResult(destination = MockUserDetail(username))
            }
            // Search
            scheme == "app" && host == "search" -> {
                val q = uri.getQueryParameter("q")
                DeepLinkResult(destination = MockSearch(q))
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
        val dest = r.resolve("app://users/list")
        assertNotNull(dest)
        assertEquals("app://users/list", dest!!.deepLink)
    }

    @Test
    fun resolves_userDetail_from_typed_handler() {
        val r = resolver()
        val dest = r.resolve("app://users/user/octocat")
        assertNotNull(dest)
        assertEquals("app://users/user/octocat", dest!!.deepLink)
    }

    @Test
    fun resolves_search_from_typed_handler_with_query() {
        val r = resolver()
        val dest = r.resolve("app://search?q=android%20ui")
        assertNotNull(dest)
        assertEquals("app://search?q=android%20ui", dest!!.deepLink)
    }

    @Test
    fun falls_back_to_generic_for_unknown() {
        val r = resolver()
        val dest = r.resolve("app://unknown/path")
        assertNotNull(dest)
        assertEquals("app://unknown/path", dest!!.deepLink)
    }
}
