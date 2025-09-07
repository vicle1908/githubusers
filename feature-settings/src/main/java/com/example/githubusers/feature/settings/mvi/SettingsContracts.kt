package com.example.githubusers.feature.settings.mvi

sealed interface SettingsIntent {
    data object Load : SettingsIntent

    data class ToggleDynamicColor(
        val enabled: Boolean,
    ) : SettingsIntent

    data class ChangeThemeMode(
        val mode: ThemeMode,
    ) : SettingsIntent

    data class ToggleDataSaver(
        val enabled: Boolean,
    ) : SettingsIntent

    data class SetPagingSize(
        val size: Int,
    ) : SettingsIntent

    data class ToggleShowImages(
        val enabled: Boolean,
    ) : SettingsIntent

    data class ToggleAnalytics(
        val enabled: Boolean,
    ) : SettingsIntent

    data class ToggleCrashReports(
        val enabled: Boolean,
    ) : SettingsIntent

    data class ChangeLanguage(
        val languageCode: String,
    ) : SettingsIntent
}

enum class ThemeMode { System, Light, Dark }

data class SettingsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val themeMode: ThemeMode = ThemeMode.System,
    val dynamicColor: Boolean = true,
    val dataSaver: Boolean = false,
    val pagingSize: Int = 30,
    val showImages: Boolean = true,
    val analytics: Boolean = false,
    val crashReports: Boolean = false,
    val languageCode: String = "system",
)

sealed interface SettingsEffect {
    data class ShowMessage(
        val text: String,
    ) : SettingsEffect
}
