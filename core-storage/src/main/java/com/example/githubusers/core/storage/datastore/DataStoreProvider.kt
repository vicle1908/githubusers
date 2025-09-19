package com.example.githubusers.core.storage.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import javax.inject.Inject
import javax.inject.Singleton

// Extension properties at top level - this is the correct syntax per Android documentation
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Provides configured DataStore instances for the application.
 * This is shared infrastructure that features can use.
 */
@Singleton
class DataStoreProvider
@Inject
constructor() {
    /**
     * Creates a DataStore instance for settings.
     * Use this for app-wide settings.
     */
    fun getSettingsDataStore(context: Context): DataStore<Preferences> = context.settingsDataStore

    /**
     * Creates a DataStore instance for user preferences.
     * Use this for user-specific data.
     */
    fun getUserDataStore(context: Context): DataStore<Preferences> = context.userDataStore

    /**
     * For features that need custom DataStore names,
     * they should create their own extension properties at the top level of their files.
     * This method provides guidance on the pattern.
     */
    fun createDataStoreGuidance(): String =
        """
            To create a custom DataStore in your feature module:
            
            // At the top level of your feature's DataStore file:
            private val Context.yourFeatureDataStore: DataStore<Preferences> by preferencesDataStore(name = "your_feature")
            
            // Then use it:
            context.yourFeatureDataStore
        """.trimIndent()
}
