package com.example.githubusers.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
    darkColorScheme(
        primary = Color(0xFF6200EE),
        secondary = Color(0xFF03DAC5),
        tertiary = Color(0xFF3700B3),
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Color(0xFF6200EE),
        secondary = Color(0xFF03DAC5),
        tertiary = Color(0xFF3700B3),
    )

@Composable
fun GithubUsersTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors =
        if (darkTheme) {
            DarkColorScheme
        } else {
            LightColorScheme
        }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
