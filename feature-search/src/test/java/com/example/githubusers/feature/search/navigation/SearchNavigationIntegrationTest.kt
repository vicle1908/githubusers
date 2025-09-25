package com.example.githubusers.feature.search.navigation

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.navigation.api.FeatureDestinationProvider
import com.example.githubusers.navigation.impl.Navigation3FeatureRegistry
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.Before
import org.junit.Test

class SearchNavigationIntegrationTest {

    private lateinit var registry: Navigation3FeatureRegistry
    private lateinit var entryProvider: (NavKey) -> NavEntry<NavKey>

    @Before
    fun setUp() {
        val provider: FeatureDestinationProvider = SearchFeatureDestinationProvider()
        registry = Navigation3FeatureRegistry(setOf(provider))
        entryProvider = registry.createEntryProvider()
    }

    @Test
    fun `search key resolves to search entry`() {
        val key = SearchNavKey.Search(query = "kotlin", origin = "deeplink")

        val entry = entryProvider.invoke(key)

        assertEquals(key, entry.key)
        assertNotNull(entry.content)
    }

    @Test
    fun `unknown key returns error entry`() {
        val unknownKey = object : NavKey {}

        val entry = entryProvider.invoke(unknownKey)

        assertEquals(unknownKey, entry.key)
    }
}
