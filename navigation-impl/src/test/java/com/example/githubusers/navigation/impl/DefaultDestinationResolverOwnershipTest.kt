package com.example.githubusers.navigation.impl

import android.net.Uri
import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private class OwnedUsersHandler : DeepLinkHandler {
    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf("app://users")

    override fun handleDeepLink(uri: Uri): DeepLinkResult? =
        if (uri.scheme == "app" && uri.path == "/users") DeepLinkResult(TestUtils.MockUserList()) else null
}

private class FallbackSearchHandler : DeepLinkHandler {
    override val moduleId: String = "search"

    override fun supportedPatterns(): List<String> = listOf("app://users")

    override fun handleDeepLink(uri: Uri): DeepLinkResult? =
        if (uri.scheme == "app" && uri.path == "/users") DeepLinkResult(TestUtils.MockSearch("owned_vs_fallback")) else null
}

private class FakeOwnershipSource : DeepLinkOwnershipSource {
    override val owners: Map<String, String> = mapOf("users" to "com.example.Owner")
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DefaultDestinationResolverOwnershipTest {
    @Test
    fun prefers_owned_handler_when_multiple_match() {
        val resolver =
            DefaultDestinationResolver(
                handlers = setOf(FallbackSearchHandler(), OwnedUsersHandler()),
                ownershipSource = FakeOwnershipSource(),
            )
        val dest = resolver.resolve("app://users")
        assertNotNull(dest)
        // Owned handler returns UserList -> canonical deep link should be app://users/list
        assertEquals("app://users/list", dest!!.deepLink)
    }

    @Test
    fun falls_back_when_no_ownership_present() {
        val resolver =
            DefaultDestinationResolver(
                handlers = setOf(FallbackSearchHandler()),
                ownershipSource = NoOpDeepLinkOwnershipSource,
            )
        val dest = resolver.resolve("app://users")
        assertNotNull(dest)
        // Fallback handler returns Search -> canonical deep link should be app://search?q=owned_vs_fallback
        assertEquals("app://search?q=owned_vs_fallback", dest!!.deepLink)
    }
}
