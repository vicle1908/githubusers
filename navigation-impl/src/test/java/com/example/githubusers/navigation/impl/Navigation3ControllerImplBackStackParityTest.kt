package com.example.githubusers.navigation.impl

import com.example.githubusers.navigation.api.NavigationDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private class FakeResolver2 : DestinationResolver {
    override fun resolve(deepLink: String): NavigationDestination? =
        when {
            deepLink == "githubusers://users" -> TestDest(route = "users", deepLink = deepLink)
            deepLink.startsWith("githubusers://user/") -> TestDest(route = "user/{username}", deepLink = deepLink)
            deepLink.startsWith("githubusers://search") -> TestDest(route = "search", deepLink = deepLink)
            deepLink == "githubusers://settings" -> TestDest(route = "settings", deepLink = deepLink)
            else -> null
        }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Navigation3ControllerImplBackStackParityTest {
    @Test
    fun popBackStackTo_nonInclusive_keeps_target_and_above_trimmed() {
        val controller = Navigation3ControllerImpl(FakeResolver2())
        controller.navigate("githubusers://users")
        controller.navigate("githubusers://user/octocat")
        controller.navigate("githubusers://search?q=android")
        assertEquals(3, controller.backStack.value.size)

        // Pop back to users (non-inclusive) => target remains top
        val ok = controller.popBackStackTo("githubusers://users", inclusive = false)
        assertTrue(ok)
        assertEquals(1, controller.backStack.value.size)
        assertEquals(
            "githubusers://users",
            controller.backStack.value
                .last()
                .deepLink,
        )
    }

    @Test
    fun popBackStackTo_inclusive_removes_target_as_well() {
        val controller = Navigation3ControllerImpl(FakeResolver2())
        controller.navigate("githubusers://users")
        controller.navigate("githubusers://user/octocat")
        controller.navigate("githubusers://search?q=android")
        assertEquals(3, controller.backStack.value.size)

        // Pop back to users (inclusive) => stack is empty
        val ok = controller.popBackStackTo("githubusers://users", inclusive = true)
        assertTrue(ok)
        assertTrue(controller.backStack.value.isEmpty())
        assertEquals(null, controller.currentEntry.value)
    }

    @Test
    fun clearBackStack_empties_all_and_currentEntry() {
        val controller = Navigation3ControllerImpl(FakeResolver2())
        controller.navigate("githubusers://users")
        controller.navigate("githubusers://user/octocat")
        assertFalse(controller.backStack.value.isEmpty())

        controller.clearBackStack()
        assertTrue(controller.backStack.value.isEmpty())
        assertEquals(null, controller.currentEntry.value)
    }

    @Test
    fun handleDeepLink_known_returns_true_and_navigates() {
        val controller = Navigation3ControllerImpl(FakeResolver2())
        val ok = controller.handleDeepLink("githubusers://user/octocat")
        assertTrue(ok)
        assertEquals("githubusers://user/octocat", controller.currentEntry.value?.deepLink)
    }

    @Test
    fun handleDeepLink_unknown_returns_false_does_not_navigate() {
        val controller = Navigation3ControllerImpl(FakeResolver2())
        val ok = controller.handleDeepLink("githubusers://unknown/path")
        assertFalse(ok)
        assertEquals(null, controller.currentEntry.value)
    }

    @Ignore("Process death save/restore not implemented yet; tracked in docs and backlog")
    @Test
    fun saveRestore_backStack_state_across_process_death() {
        // TODO: Implement when persistence layer is added to Navigation3ControllerImpl
        // Acceptance: serialize current backStack and restore on restart
        assertTrue(true)
    }
}
