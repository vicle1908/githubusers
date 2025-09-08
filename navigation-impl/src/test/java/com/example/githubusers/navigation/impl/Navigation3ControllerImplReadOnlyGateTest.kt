package com.example.githubusers.navigation.impl

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private class FakeResolverGate : DestinationResolver {
    override fun resolve(deepLink: String) = GenericDestination(deepLink)
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Navigation3ControllerImplReadOnlyGateTest {
    @Test
    fun navigate_is_blocked_when_gate_enabled() {
        val controller =
            Navigation3ControllerImpl(
                destinationResolver = FakeResolverGate(),
                navGate =
                    com.example.githubusers.navigation.impl.guard
                        .ReadOnlyNavGate(true),
            )
        controller.navigate("githubusers://users")
        assertTrue(controller.backStack.value.isEmpty())
        assertEquals(null, controller.currentEntry.value)
    }

    @Test
    fun handleDeepLink_returns_false_when_gate_enabled() {
        val controller =
            Navigation3ControllerImpl(
                destinationResolver = FakeResolverGate(),
                navGate =
                    com.example.githubusers.navigation.impl.guard
                        .ReadOnlyNavGate(true),
            )
        val ok = controller.handleDeepLink("githubusers://users")
        assertFalse(ok)
    }
}
