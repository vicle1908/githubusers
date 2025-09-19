package com.example.githubusers.feature.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class SettingsRepository
@Inject
constructor(private val dataStore: DataStore<Preferences>) {
    object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val DATA_SAVER = booleanPreferencesKey("data_saver")
        val PAGING_SIZE = intPreferencesKey("paging_size")
        val SHOW_IMAGES = booleanPreferencesKey("show_images")
        val ANALYTICS = booleanPreferencesKey("analytics")
        val CRASH_REPORTS = booleanPreferencesKey("crash_reports")
        val LANGUAGE = stringPreferencesKey("language")
    }

    val themeMode: Flow<String> = dataStore.data.map { it[Keys.THEME_MODE] ?: "system" }
    val dynamicColor: Flow<Boolean> = dataStore.data.map { it[Keys.DYNAMIC_COLOR] ?: true }
    val dataSaver: Flow<Boolean> = dataStore.data.map { it[Keys.DATA_SAVER] ?: false }
    val pagingSize: Flow<Int> = dataStore.data.map { it[Keys.PAGING_SIZE] ?: 30 }
    val showImages: Flow<Boolean> = dataStore.data.map { it[Keys.SHOW_IMAGES] ?: true }
    val analytics: Flow<Boolean> = dataStore.data.map { it[Keys.ANALYTICS] ?: false }
    val crashReports: Flow<Boolean> = dataStore.data.map { it[Keys.CRASH_REPORTS] ?: false }
    val language: Flow<String> = dataStore.data.map { it[Keys.LANGUAGE] ?: "system" }

    suspend fun setThemeMode(value: String) {
        dataStore.edit { it[Keys.THEME_MODE] = value }
    }

    suspend fun setDynamicColor(value: Boolean) {
        dataStore.edit { it[Keys.DYNAMIC_COLOR] = value }
    }

    suspend fun setDataSaver(value: Boolean) {
        dataStore.edit { it[Keys.DATA_SAVER] = value }
    }

    suspend fun setPagingSize(value: Int) {
        dataStore.edit { it[Keys.PAGING_SIZE] = value }
    }

    suspend fun setShowImages(value: Boolean) {
        dataStore.edit { it[Keys.SHOW_IMAGES] = value }
    }

    suspend fun setAnalytics(value: Boolean) {
        dataStore.edit { it[Keys.ANALYTICS] = value }
    }

    suspend fun setCrashReports(value: Boolean) {
        dataStore.edit { it[Keys.CRASH_REPORTS] = value }
    }

    suspend fun setLanguage(value: String) {
        dataStore.edit { it[Keys.LANGUAGE] = value }
    }
}
