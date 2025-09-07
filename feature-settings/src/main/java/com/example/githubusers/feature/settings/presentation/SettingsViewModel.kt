package com.example.githubusers.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubusers.feature.settings.data.SettingsRepository
import com.example.githubusers.feature.settings.mvi.SettingsEffect
import com.example.githubusers.feature.settings.mvi.SettingsIntent
import com.example.githubusers.feature.settings.mvi.SettingsState
import com.example.githubusers.feature.settings.mvi.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val repo: SettingsRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(SettingsState(isLoading = true))
        val state: StateFlow<SettingsState> = _state.asStateFlow()

        val effects = MutableSharedFlow<SettingsEffect>()

        init {
            viewModelScope.launch {
                repo.themeMode.collect { mode ->
                    _state.update {
                        it.copy(
                            themeMode =
                                when (mode) {
                                    "light" -> ThemeMode.Light
                                    "dark" -> ThemeMode.Dark
                                    else -> ThemeMode.System
                                },
                            isLoading = false,
                        )
                    }
                }
            }
            viewModelScope.launch { repo.dynamicColor.collect { enabled -> _state.update { it.copy(dynamicColor = enabled) } } }
            viewModelScope.launch { repo.dataSaver.collect { enabled -> _state.update { it.copy(dataSaver = enabled) } } }
            viewModelScope.launch { repo.pagingSize.collect { size -> _state.update { it.copy(pagingSize = size) } } }
            viewModelScope.launch { repo.showImages.collect { enabled -> _state.update { it.copy(showImages = enabled) } } }
            viewModelScope.launch { repo.analytics.collect { enabled -> _state.update { it.copy(analytics = enabled) } } }
            viewModelScope.launch { repo.crashReports.collect { enabled -> _state.update { it.copy(crashReports = enabled) } } }
            viewModelScope.launch { repo.language.collect { lang -> _state.update { it.copy(languageCode = lang) } } }
        }

        fun process(intent: SettingsIntent) {
            when (intent) {
                SettingsIntent.Load -> Unit
                is SettingsIntent.ToggleDynamicColor -> viewModelScope.launch { repo.setDynamicColor(intent.enabled) }
                is SettingsIntent.ChangeThemeMode ->
                    viewModelScope.launch {
                        repo.setThemeMode(
                            when (intent.mode) {
                                ThemeMode.System -> "system"
                                ThemeMode.Light -> "light"
                                ThemeMode.Dark -> "dark"
                            },
                        )
                    }
                is SettingsIntent.ToggleDataSaver -> viewModelScope.launch { repo.setDataSaver(intent.enabled) }
                is SettingsIntent.SetPagingSize -> viewModelScope.launch { repo.setPagingSize(intent.size) }
                is SettingsIntent.ToggleShowImages -> viewModelScope.launch { repo.setShowImages(intent.enabled) }
                is SettingsIntent.ToggleAnalytics -> viewModelScope.launch { repo.setAnalytics(intent.enabled) }
                is SettingsIntent.ToggleCrashReports -> viewModelScope.launch { repo.setCrashReports(intent.enabled) }
                is SettingsIntent.ChangeLanguage -> viewModelScope.launch { repo.setLanguage(intent.languageCode) }
            }
        }

        fun nextTheme(current: ThemeMode): ThemeMode =
            when (current) {
                ThemeMode.System -> ThemeMode.Light
                ThemeMode.Light -> ThemeMode.Dark
                ThemeMode.Dark -> ThemeMode.System
            }
    }
