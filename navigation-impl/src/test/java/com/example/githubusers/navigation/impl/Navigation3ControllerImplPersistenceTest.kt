package com.example.githubusers.navigation.impl

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private class FakeResolverPersist : DestinationResolver {
    override fun resolve(deepLink: String) = GenericDestination(deepLink)
}

private class FakeBackStackStore : BackStackStore {
    var payload: PersistedBackStack? = null
    var cleared: Boolean = false

    override fun save(payload: PersistedBackStack) {
        this.payload = payload
    }

    override fun load(): PersistedBackStack? = payload

    override fun clear() {
        payload = null
        cleared = true
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Navigation3ControllerImplPersistenceTest {
    @Test
    fun restore_success_rebuilds_back_stack() {
        val store = FakeBackStackStore()
        val controller1 =
            Navigation3ControllerImpl(
                destinationResolver = FakeResolverPersist(),
                backStackStore = store,
                persistenceConfig = PersistenceConfig(enabled = true),
            )
        controller1.navigate("githubusers://users")
        controller1.navigate("githubusers://search?q=android")
        assertEquals(2, controller1.backStack.value.size)
        // Simulate new process/controller
        val controller2 =
            Navigation3ControllerImpl(
                destinationResolver = FakeResolverPersist(),
                backStackStore = store,
                persistenceConfig = PersistenceConfig(enabled = true),
            )
        val restored = controller2.restoreFromPersistence()
        assertTrue(restored)
        assertEquals(2, controller2.backStack.value.size)
        assertEquals("githubusers://search?q=android", controller2.currentEntry.value?.deepLink)
    }

    @Test
    fun restore_fails_on_ttl_expired_and_clears_store() {
        val store = FakeBackStackStore()
        val oldTs = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000) - 1L
        store.payload =
            PersistedBackStack(
                schemaVersion = 1,
                timestamp = oldTs,
                appVersion = "1.0",
                navGraphVersion = 1,
                entries = listOf("githubusers://users"),
            )
        val controller =
            Navigation3ControllerImpl(
                destinationResolver = FakeResolverPersist(),
                backStackStore = store,
                persistenceConfig = PersistenceConfig(enabled = true),
            )
        val restored = controller.restoreFromPersistence()
        assertFalse(restored)
        assertTrue(store.cleared)
    }

    @Test
    fun restore_fails_on_schema_mismatch() {
        val store = FakeBackStackStore()
        store.payload =
            PersistedBackStack(
                schemaVersion = 1,
                timestamp = System.currentTimeMillis(),
                appVersion = "1.0",
                navGraphVersion = 1,
                entries = listOf("githubusers://users"),
            )
        val controller =
            Navigation3ControllerImpl(
                destinationResolver = FakeResolverPersist(),
                backStackStore = store,
                persistenceConfig = PersistenceConfig(enabled = true, schemaVersion = 2),
            )
        val restored = controller.restoreFromPersistence()
        assertFalse(restored)
        assertTrue(store.cleared)
    }

    @Test
    fun restore_fails_on_navgraph_mismatch() {
        val store = FakeBackStackStore()
        store.payload =
            PersistedBackStack(
                schemaVersion = 1,
                timestamp = System.currentTimeMillis(),
                appVersion = "1.0",
                navGraphVersion = 1,
                entries = listOf("githubusers://users"),
            )
        val controller =
            Navigation3ControllerImpl(
                destinationResolver = FakeResolverPersist(),
                backStackStore = store,
                persistenceConfig = PersistenceConfig(enabled = true, navGraphVersion = 2),
            )
        val restored = controller.restoreFromPersistence()
        assertFalse(restored)
        assertTrue(store.cleared)
    }

    @Test
    fun restore_fails_on_app_version_mismatch() {
        val store = FakeBackStackStore()
        store.payload =
            PersistedBackStack(
                schemaVersion = 1,
                timestamp = System.currentTimeMillis(),
                appVersion = "1.0",
                navGraphVersion = 1,
                entries = listOf("githubusers://users"),
            )
        val controller =
            Navigation3ControllerImpl(
                destinationResolver = FakeResolverPersist(),
                backStackStore = store,
                persistenceConfig = PersistenceConfig(enabled = true, appVersionProvider = { "2.0" }),
            )
        val restored = controller.restoreFromPersistence()
        assertFalse(restored)
        assertTrue(store.cleared)
    }
}
