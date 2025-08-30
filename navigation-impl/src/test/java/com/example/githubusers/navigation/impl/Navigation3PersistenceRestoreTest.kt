package com.example.githubusers.navigation.impl

import com.example.githubusers.navigation.api.NavigationDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private data class TestDestRestore(
    override val route: String,
    override val deepLink: String,
) : NavigationDestination

private class InMemoryBackStackStore : BackStackStore {
    var payload: PersistedBackStack? = null

    override fun save(payload: PersistedBackStack) {
        this.payload = payload
    }

    override fun load(): PersistedBackStack? = payload

    override fun clear() {
        payload = null
    }
}

private class FakeResolverRestore : DestinationResolver {
    override fun resolve(deepLink: String): NavigationDestination? =
        when {
            deepLink == "githubusers://users" -> TestDestRestore("users", deepLink)
            deepLink.startsWith("githubusers://user/") -> TestDestRestore("user/{username}", deepLink)
            else -> null
        }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Navigation3PersistenceRestoreTest {
    @Test
    fun restore_succeeds_with_valid_payload() {
        val store = InMemoryBackStackStore()
        val now = System.currentTimeMillis()
        store.save(
            PersistedBackStack(
                schemaVersion = 1,
                timestamp = now,
                appVersion = "1.0",
                navGraphVersion = 1,
                entries = listOf("githubusers://users", "githubusers://user/octocat"),
            ),
        )
        val controller =
            Navigation3ControllerImpl(
                destinationResolver = FakeResolverRestore(),
                backStackStore = store,
                persistenceConfig =
                    PersistenceConfig(
                        enabled = true,
                        maxEntries = 5,
                        ttlMillis = 7L * 24L * 60L * 60L * 1000L,
                        schemaVersion = 1,
                        navGraphVersion = 1,
                        appVersionProvider = { "1.0" },
                    ),
            )
        val restored = controller.restoreFromPersistence()
        assertTrue(restored)
        assertEquals("githubusers://user/octocat", controller.currentEntry.value?.deepLink)
        assertEquals(2, controller.backStack.value.size)
    }

    @Test
    fun restore_fails_when_ttl_expired() {
        val store = InMemoryBackStackStore()
        val old = System.currentTimeMillis() - (10L * 24L * 60L * 60L * 1000L)
        store.save(
            PersistedBackStack(
                schemaVersion = 1,
                timestamp = old,
                appVersion = "1.0",
                navGraphVersion = 1,
                entries = listOf("githubusers://users"),
            ),
        )
        val controller =
            Navigation3ControllerImpl(
                destinationResolver = FakeResolverRestore(),
                backStackStore = store,
                persistenceConfig =
                    PersistenceConfig(
                        enabled = true,
                        maxEntries = 5,
                        ttlMillis = 7L * 24L * 60L * 60L * 1000L,
                        schemaVersion = 1,
                        navGraphVersion = 1,
                        appVersionProvider = { "1.0" },
                    ),
            )
        val restored = controller.restoreFromPersistence()
        assertFalse(restored)
        assertEquals(null, controller.currentEntry.value)
        assertTrue(controller.backStack.value.isEmpty())
    }

    @Test
    fun restore_fails_on_version_mismatch() {
        val store = InMemoryBackStackStore()
        val now = System.currentTimeMillis()
        store.save(
            PersistedBackStack(
                schemaVersion = 2,
                timestamp = now,
                appVersion = "1.0",
                navGraphVersion = 2,
                entries = listOf("githubusers://users"),
            ),
        )
        val controller =
            Navigation3ControllerImpl(
                destinationResolver = FakeResolverRestore(),
                backStackStore = store,
                persistenceConfig =
                    PersistenceConfig(
                        enabled = true,
                        maxEntries = 5,
                        ttlMillis = 7L * 24L * 60L * 60L * 1000L,
                        schemaVersion = 1,
                        navGraphVersion = 1,
                        appVersionProvider = { "1.0" },
                    ),
            )
        val restored = controller.restoreFromPersistence()
        assertFalse(restored)
        assertEquals(null, controller.currentEntry.value)
    }
}
