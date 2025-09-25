package com.example.githubusers.feature.users.navigation

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.navigation.api.FeatureDestinationProvider
import com.example.githubusers.navigation.impl.Navigation3FeatureRegistry
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.Before
import org.junit.Test

class UsersNavigationIntegrationTest {

    private lateinit var registry: Navigation3FeatureRegistry
    private lateinit var entryProvider: (NavKey) -> NavEntry<NavKey>

    @Before
    fun setUp() {
        val usersProvider: FeatureDestinationProvider = UsersFeatureDestinationProvider()
        registry = Navigation3FeatureRegistry(setOf(usersProvider))
        entryProvider = registry.createEntryProvider()
    }

    @Test
    fun `list key resolves to users list entry`() {
        val entry = entryProvider.invoke(UserNavKey.UserList)

        assertEquals(UserNavKey.UserList, entry.key)
        assertNotNull(entry.content)
    }

    @Test
    fun `detail key resolves to user detail entry`() {
        val entry = entryProvider.invoke(UserNavKey.UserDetail(username = "octocat"))

        assertEquals(UserNavKey.UserDetail("octocat"), entry.key)
        assertNotNull(entry.content)
    }

    @Test
    fun `settings dialog key resolves to users settings dialog entry`() {
        val entry = entryProvider.invoke(UserNavKey.UserSettingsDialog(username = "octocat"))

        assertEquals(UserNavKey.UserSettingsDialog("octocat"), entry.key)
        assertNotNull(entry.content)
    }

    @Test
    fun `unknown key returns error entry with same key`() {
        val unknownKey = object : NavKey {}
        val entry = entryProvider.invoke(unknownKey)

        assertEquals(unknownKey, entry.key)
    }
}
