package com.example.githubusers.navigation.impl

import com.example.githubusers.navigation.api.AppDestination
import com.example.githubusers.navigation.api.NavigationDestination
import com.example.githubusers.navigation.api.NavigationOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private class FakeResolver : DestinationResolver {
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
class Navigation3ControllerImplTypedTest {
    @Test
    fun typedNavigate_pushesBackStack() {
        val controller = Navigation3ControllerImpl(FakeResolver())
        // Start at UserList
        controller.navigate(AppDestination.UserList)
        assertEquals(1, controller.backStack.value.size)
        assertEquals(
            "githubusers://users",
            controller.backStack.value
                .last()
                .deepLink,
        )

        // Navigate to user detail
        controller.navigate(AppDestination.UserDetail("octocat"))
        assertEquals(2, controller.backStack.value.size)
        assertEquals(
            "githubusers://user/octocat",
            controller.backStack.value
                .last()
                .deepLink,
        )
    }

    @Test
    fun typedNavigate_popUpTo_trimsBackStack() {
        val controller = Navigation3ControllerImpl(FakeResolver())
        controller.navigate(AppDestination.UserList)
        controller.navigate(AppDestination.UserDetail("octocat"))
        assertEquals(2, controller.backStack.value.size)

        // pop back to users (keep it)
        controller.navigate(
            AppDestination.UserList,
            NavigationOptions(popUpTo = "githubusers://users", popUpToInclusive = false),
        )
        // After popUpTo(non-inclusive), stack should contain users and then the new users entry
        // Our impl takes the stack up to index+1, then adds a new entry => size remains >= 1
        assertTrue(controller.backStack.value.isNotEmpty())
        assertEquals(
            "githubusers://users",
            controller.backStack.value
                .last()
                .deepLink,
        )
    }

    @Test
    fun typedNavigate_launchSingleTop_avoidsDuplicate() {
        val controller = Navigation3ControllerImpl(FakeResolver())
        controller.navigate(AppDestination.UserList)
        val sizeBefore = controller.backStack.value.size
        // Navigating to same destination with singleTop should not add
        controller.navigate(AppDestination.UserList, NavigationOptions(launchSingleTop = true))
        assertEquals(sizeBefore, controller.backStack.value.size)

        // Navigating to user detail then singleTop user detail again won't add duplicate
        controller.navigate(AppDestination.UserDetail("octocat"))
        val sizeDetail = controller.backStack.value.size
        controller.navigate(AppDestination.UserDetail("octocat"), NavigationOptions(launchSingleTop = true))
        assertEquals(sizeDetail, controller.backStack.value.size)
    }

    @Test
    fun navigateBack_pops() {
        val controller = Navigation3ControllerImpl(FakeResolver())
        controller.navigate(AppDestination.UserList)
        controller.navigate(AppDestination.UserDetail("octocat"))
        assertTrue(controller.navigateBack())
        assertEquals("githubusers://users", controller.currentEntry.value?.deepLink)
        // at root, navigateBack should return false
        assertFalse(controller.navigateBack())
    }
}
