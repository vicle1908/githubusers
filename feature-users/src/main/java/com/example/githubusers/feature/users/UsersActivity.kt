package com.example.githubusers.feature.users

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UsersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data: Uri? = intent?.data
        val (screen, arg) = parseDeepLink(data)
        setContent {
            when (screen) {
                Screen.List -> UsersListHost()
                is Screen.Detail -> UserDetailHost(screen.username)
            }
        }
    }

    private fun parseDeepLink(uri: Uri?): Pair<Screen, String?> {
        if (uri == null) return Screen.List to null
        val path = uri.path.orEmpty()
        return when {
            path.endsWith("/users") || path.endsWith("/users/list") -> Screen.List to null
            path.contains("/user/") -> Screen.Detail(path.substringAfterLast("/")) to null
            else -> Screen.List to null
        }
    }
}

private sealed interface Screen {
    data object List : Screen

    data class Detail(
        val username: String,
    ) : Screen
}

@Composable
private fun UsersListHost() {
    UsersListScreen(onUserClick = { username ->
        // Emit deep link intent to open detail in this module via system handling
        // The app shell should have an IntentFilter to route to this activity
        // For pure module independence, we simply ignore and let host wire onUserClick externally
    })
}

@Composable
private fun UserDetailHost(username: String) {
    UserDetailScreen(username = username)
}
