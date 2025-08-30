package com.example.githubusers.navigation.impl

import com.example.githubusers.navigation.api.NavigationDestination
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private data class TestDestArgs(
    override val route: String,
    override val deepLink: String,
) : NavigationDestination

private class FakeResolverArgs : DestinationResolver {
    override fun resolve(deepLink: String): NavigationDestination? = TestDestArgs(route = "search", deepLink = deepLink)
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DeepLinkArgumentsExtractionTest {
    @Test
    fun extractArguments_parses_query_params_into_entry_arguments() {
        val controller = Navigation3ControllerImpl(FakeResolverArgs())
        controller.navigate("githubusers://search?q=android%20ui&source=home")
        val entry = controller.currentEntry.value
        requireNotNull(entry)
        assertEquals("android ui", entry.arguments["q"])
        assertEquals("home", entry.arguments["source"])
    }
}
