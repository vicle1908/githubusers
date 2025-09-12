package com.example.githubusers.feature.settings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.githubusers.feature.settings.mvi.SettingsIntent
import com.example.githubusers.feature.settings.mvi.ThemeMode
import com.example.githubusers.feature.settings.presentation.navigation.SettingsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigator: SettingsNavigator,
    section: String? = null,
    viewModel: SettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings" + (section?.let { " • ${it.replaceFirstChar { c -> c.uppercase() }}" } ?: "")) },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateBack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { inner ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            when (section?.lowercase()) {
                null -> {
                    // Settings Home
                    ListItem(
                        headlineContent = { Text("Dynamic color") },
                        trailingContent = {
                            Switch(
                                checked = state.dynamicColor,
                                onCheckedChange = { viewModel.process(SettingsIntent.ToggleDynamicColor(it)) },
                            )
                        },
                    )
                    ListItem(
                        headlineContent = { Text("Theme: ${state.themeMode}") },
                        supportingContent = { Text("Tap to cycle") },
                        modifier = Modifier.padding(vertical = 4.dp),
                        trailingContent = {
                            Switch(checked = state.themeMode != ThemeMode.System, onCheckedChange = {
                                val next = viewModel.nextTheme(state.themeMode)
                                viewModel.process(SettingsIntent.ChangeThemeMode(next))
                            })
                        },
                    )
                    ListItem(
                        headlineContent = { Text("Data saver") },
                        trailingContent = {
                            Switch(checked = state.dataSaver, onCheckedChange = { viewModel.process(SettingsIntent.ToggleDataSaver(it)) })
                        },
                    )
                    ListItem(
                        headlineContent = { Text("Show images") },
                        trailingContent = {
                            Switch(checked = state.showImages, onCheckedChange = { viewModel.process(SettingsIntent.ToggleShowImages(it)) })
                        },
                    )
                    ListItem(
                        headlineContent = { Text("Analytics") },
                        trailingContent = {
                            Switch(checked = state.analytics, onCheckedChange = { viewModel.process(SettingsIntent.ToggleAnalytics(it)) })
                        },
                    )
                    ListItem(
                        headlineContent = { Text("Crash reports") },
                        trailingContent = {
                            Switch(
                                checked = state.crashReports,
                                onCheckedChange = { viewModel.process(SettingsIntent.ToggleCrashReports(it)) },
                            )
                        },
                    )
                    ListItem(
                        headlineContent = { Text("Theme settings") },
                        supportingContent = { Text("Open theme section") },
                        modifier = Modifier.padding(vertical = 8.dp).clickable { navigator.openSection("theme") },
                        trailingContent = { Text("Open", color = MaterialTheme.colorScheme.primary) },
                    )
                    ListItem(
                        headlineContent = { Text("Data usage") },
                        supportingContent = { Text("Open data settings") },
                        modifier = Modifier.padding(vertical = 8.dp).clickable { navigator.openSection("data") },
                        trailingContent = { Text("Open", color = MaterialTheme.colorScheme.primary) },
                    )
                    ListItem(
                        headlineContent = { Text("Privacy") },
                        supportingContent = { Text("Analytics, crash reports") },
                        modifier = Modifier.padding(vertical = 8.dp).clickable { navigator.openSection("privacy") },
                        trailingContent = { Text("Open", color = MaterialTheme.colorScheme.primary) },
                    )
                    ListItem(
                        headlineContent = { Text("About / OSS") },
                        supportingContent = { Text("Licenses") },
                        modifier = Modifier.padding(vertical = 8.dp).clickable { navigator.openSection("about") },
                        trailingContent = { Text("Open", color = MaterialTheme.colorScheme.primary) },
                    )
                }
                "theme" -> {
                    ListItem(
                        headlineContent = { Text("Theme mode") },
                        supportingContent = { Text("System, Light, Dark") },
                        trailingContent = {
                            Text(state.themeMode.name)
                        },
                    )
                    ListItem(
                        headlineContent = { Text("Cycle theme") },
                        supportingContent = { Text("Tap to change to next mode") },
                        modifier =
                            Modifier.padding(vertical = 4.dp).clickable {
                                val next = viewModel.nextTheme(state.themeMode)
                                viewModel.process(SettingsIntent.ChangeThemeMode(next))
                            },
                    )
                    ListItem(
                        headlineContent = { Text("Dynamic color") },
                        trailingContent = {
                            Switch(
                                checked = state.dynamicColor,
                                onCheckedChange = { viewModel.process(SettingsIntent.ToggleDynamicColor(it)) },
                            )
                        },
                    )
                }
                "data" -> {
                    ListItem(
                        headlineContent = { Text("Data saver") },
                        trailingContent = {
                            Switch(checked = state.dataSaver, onCheckedChange = { viewModel.process(SettingsIntent.ToggleDataSaver(it)) })
                        },
                    )
                    ListItem(
                        headlineContent = { Text("Paging size") },
                        supportingContent = { Text("Current: ${state.pagingSize}") },
                        modifier =
                            Modifier.padding(vertical = 4.dp).clickable {
                                val next =
                                    when (state.pagingSize) {
                                        15 -> 30
                                        30 -> 50
                                        else -> 15
                                    }
                                viewModel.process(SettingsIntent.SetPagingSize(next))
                            },
                    )
                    ListItem(
                        headlineContent = { Text("Show images") },
                        trailingContent = {
                            Switch(checked = state.showImages, onCheckedChange = { viewModel.process(SettingsIntent.ToggleShowImages(it)) })
                        },
                    )
                }
                "privacy" -> {
                    ListItem(
                        headlineContent = { Text("Analytics") },
                        trailingContent = {
                            Switch(checked = state.analytics, onCheckedChange = { viewModel.process(SettingsIntent.ToggleAnalytics(it)) })
                        },
                    )
                    ListItem(
                        headlineContent = { Text("Crash reports") },
                        trailingContent = {
                            Switch(
                                checked = state.crashReports,
                                onCheckedChange = { viewModel.process(SettingsIntent.ToggleCrashReports(it)) },
                            )
                        },
                    )
                }
                "about", "oss" -> {
                    ListItem(
                        headlineContent = { Text("Open Source Licenses") },
                        supportingContent = { Text("Third-party libraries used in this app.") },
                    )
                    ListItem(
                        headlineContent = { Text("Version") },
                        supportingContent = { Text("Debug build") },
                    )
                }
                else -> {
                    Text("Unknown section: $section")
                }
            }
        }
    }
}
