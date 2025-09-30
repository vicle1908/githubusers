package com.example.githubusers.presentation.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import com.example.githubusers.feature.repository.navigation.RepositoryNavKey
import com.example.githubusers.feature.search.navigation.SearchNavKey
import com.example.githubusers.feature.settings.navigation.SettingsNavKey
import com.example.githubusers.feature.users.navigation.UserNavKey
import com.example.githubusers.presentation.debug.navigation.DebugNavKey
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer

@OptIn(ExperimentalSerializationApi::class)
private val baseNavKeySerializersModule: SerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(UserNavKey.UserList::class, serializer<UserNavKey.UserList>())
        subclass(UserNavKey.UserDetail::class, serializer<UserNavKey.UserDetail>())
        subclass(UserNavKey.UserSettingsDialog::class, serializer<UserNavKey.UserSettingsDialog>())
        subclass(SearchNavKey.Search::class, serializer<SearchNavKey.Search>())
        subclass(SettingsNavKey.Settings::class, serializer<SettingsNavKey.Settings>())
        subclass(RepositoryNavKey.RepositoryList::class, serializer<RepositoryNavKey.RepositoryList>())
        subclass(RepositoryNavKey.RepositoryDetail::class, serializer<RepositoryNavKey.RepositoryDetail>())
        subclass(DebugNavKey.CorePagingShowcase::class, serializer<DebugNavKey.CorePagingShowcase>())
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun navSavedStateConfiguration(vararg extraModules: SerializersModule): SavedStateConfiguration {
    val combinedModule = extraModules.fold(baseNavKeySerializersModule) { acc, module -> acc + module }
    return SavedStateConfiguration {
        serializersModule += combinedModule
    }
}
