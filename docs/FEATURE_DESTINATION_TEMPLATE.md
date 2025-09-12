# Feature Destination Template

This template provides a complete guide for creating navigation destinations and deep link handling in feature modules following the feature-based architecture.

## Overview

Each feature module should own its own:
- **Destinations**: Define the screens/routes within the feature
- **Deep Link Handlers**: Handle incoming deep links for the feature
- **Deep Link Builders**: Build deep links for navigation to the feature
- **Navigation Commands**: Define navigation actions within the feature

## Template Structure

### 1. Feature Destination Definition

Create a sealed interface that extends `NavigationDestination`:

```kotlin
// feature-{name}/src/main/java/com/example/githubusers/feature/{name}/navigation/{Name}Destination.kt
package com.example.githubusers.feature.{name}.navigation

import com.example.githubusers.navigation.api.NavigationDestination
import kotlinx.serialization.Serializable

/**
 * Navigation destinations for the {Name} feature module.
 * Owned by feature-{name} per feature-based architecture.
 */
sealed interface {Name}Destination : NavigationDestination {
    @Serializable
    data object {Name}List : {Name}Destination {
        override val route: String = "{name}/list"
        override val deepLink: String = "app://{name}/list"
        override val arguments: Map<String, Any> = emptyMap()
    }

    @Serializable
    data class {Name}Detail(
        val id: String,
    ) : {Name}Destination {
        override val route: String = "{name}/detail/$id"
        override val deepLink: String = "app://{name}/$id"
        override val arguments: Map<String, Any> = mapOf("id" to id)
    }

    // Add more destinations as needed
    @Serializable
    data class {Name}Search(
        val query: String = "",
    ) : {Name}Destination {
        override val route: String = "{name}/search"
        override val deepLink: String = "app://{name}/search?query=$query"
        override val arguments: Map<String, Any> = mapOf("query" to query)
    }
}
```

### 2. Deep Link Handler

Create a handler that implements `DeepLinkHandler`:

```kotlin
// feature-{name}/src/main/java/com/example/githubusers/feature/{name}/navigation/{Name}DeepLinkHandler.kt
package com.example.githubusers.feature.{name}.navigation

import android.net.Uri
import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkResult
import com.example.githubusers.navigation.api.getBooleanQueryParameter
import javax.inject.Inject

/**
 * Deep link handler for the {Name} feature module.
 * Owned by feature-{name} per feature-based architecture.
 */
class {Name}DeepLinkHandler
    @Inject
    constructor() : DeepLinkHandler {
        companion object {
            private const val PATTERN_{NAME}_LIST = "app://{name}/list"
            private const val PATTERN_{NAME}_DETAIL = "app://{name}/{id}"
            private const val PATTERN_{NAME}_SEARCH = "app://{name}/search"
            private const val WEB_PATTERN_{NAME} = "https://githubusers.example.com/{name}"
        }

        override val moduleId: String get() = "{name}"

        override fun supportedPatterns(): List<String> =
            listOf(
                PATTERN_{NAME}_LIST,
                PATTERN_{NAME}_DETAIL,
                PATTERN_{NAME}_SEARCH,
                WEB_PATTERN_{NAME},
            )

        override fun handleDeepLink(uri: Uri): DeepLinkResult? =
            when {
                is{Name}ListUri(uri) -> {
                    DeepLinkResult(
                        destination = {Name}Destination.{Name}List,
                        clearBackStack = uri.getBooleanQueryParameter("clear_stack", false),
                    )
                }
                is{Name}DetailUri(uri) -> {
                    val id = extractId(uri)
                    if (id != null) {
                        DeepLinkResult(
                            destination = {Name}Destination.{Name}Detail(id = id),
                            clearBackStack = uri.getBooleanQueryParameter("clear_stack", false),
                        )
                    } else {
                        null
                    }
                }
                is{Name}SearchUri(uri) -> {
                    val query = uri.getQueryParameter("query") ?: ""
                    DeepLinkResult(
                        destination = {Name}Destination.{Name}Search(query = query),
                        clearBackStack = uri.getBooleanQueryParameter("clear_stack", false),
                    )
                }
                else -> null
            }

        private fun is{Name}ListUri(uri: Uri): Boolean {
            val scheme = uri.scheme ?: return false
            val host = uri.host
            val path = uri.path ?: ""
            return when {
                // App scheme: app://{name}/list
                scheme == "app" && host == "{name}" && path == "/list" -> true
                // Web universal links: https://githubusers.example.com/{name}
                (scheme == "http" || scheme == "https") && host == "githubusers.example.com" && path == "/{name}" -> true
                else -> false
            }
        }

        private fun is{Name}DetailUri(uri: Uri): Boolean {
            val scheme = uri.scheme ?: return false
            val host = uri.host
            val path = uri.path ?: ""
            return when {
                // App scheme: app://{name}/{id}
                scheme == "app" && host == "{name}" && path.matches(Regex("/[^/]+")) -> true
                // Web universal links: https://githubusers.example.com/{name}/{id}
                (scheme == "http" || scheme == "https") && host == "githubusers.example.com" && path.matches(Regex("/{name}/[^/]+")) -> true
                else -> false
            }
        }

        private fun is{Name}SearchUri(uri: Uri): Boolean {
            val scheme = uri.scheme ?: return false
            val host = uri.host
            val path = uri.path ?: ""
            return when {
                // App scheme: app://{name}/search
                scheme == "app" && host == "{name}" && path == "/search" -> true
                // Web universal links: https://githubusers.example.com/{name}/search
                (scheme == "http" || scheme == "https") && host == "githubusers.example.com" && path == "/{name}/search" -> true
                else -> false
            }
        }

        private fun extractId(uri: Uri): String? {
            val path = uri.path ?: return null
            return when {
                path.matches(Regex("/[^/]+")) -> path.removePrefix("/")
                path.matches(Regex("/{name}/[^/]+")) -> path.removePrefix("/{name}/")
                else -> null
            }
        }
    }
```

### 3. Deep Link Builder

Create a utility object for building deep links:

```kotlin
// feature-{name}/src/main/java/com/example/githubusers/feature/{name}/navigation/{Name}DeepLinks.kt
package com.example.githubusers.feature.{name}.navigation

import android.net.Uri

/**
 * Deep link builders for the {Name} module.
 * Other modules should use these to navigate to {name} screens.
 */
object {Name}DeepLinks {
    /**
     * Navigate to {name} list screen
     */
    fun {name}List(
        filter: String? = null,
        clearStack: Boolean = false,
        singleTop: Boolean = true,
    ): String =
        buildString {
            append("app://{name}/list")
            val params = mutableListOf<String>()

            filter?.let {
                params.add("filter=${Uri.encode(it)}")
            }
            if (clearStack) {
                params.add("clear_stack=true")
            }
            if (!singleTop) {
                params.add("single_top=false")
            }

            if (params.isNotEmpty()) {
                append("?")
                append(params.joinToString("&"))
            }
        }

    /**
     * Navigate to {name} detail screen
     */
    fun {name}Detail(
        id: String,
        clearStack: Boolean = false,
        singleTop: Boolean = true,
    ): String =
        buildString {
            append("app://{name}/${Uri.encode(id)}")
            val params = mutableListOf<String>()

            if (clearStack) {
                params.add("clear_stack=true")
            }
            if (!singleTop) {
                params.add("single_top=false")
            }

            if (params.isNotEmpty()) {
                append("?")
                append(params.joinToString("&"))
            }
        }

    /**
     * Navigate to {name} search screen
     */
    fun {name}Search(
        query: String = "",
        clearStack: Boolean = false,
        singleTop: Boolean = true,
    ): String =
        buildString {
            append("app://{name}/search")
            val params = mutableListOf<String>()

            if (query.isNotEmpty()) {
                params.add("query=${Uri.encode(query)}")
            }
            if (clearStack) {
                params.add("clear_stack=true")
            }
            if (!singleTop) {
                params.add("single_top=false")
            }

            if (params.isNotEmpty()) {
                append("?")
                append(params.joinToString("&"))
            }
        }
}
```

### 4. Feature API Interface

Create a feature API interface for navigation commands:

```kotlin
// feature-{name}/src/main/java/com/example/githubusers/feature/{name}/navigation/{Name}FeatureApi.kt
package com.example.githubusers.feature.{name}.navigation

import com.example.githubusers.navigation.api.NavCommand

/**
 * Feature API for {Name} navigation.
 * Provides navigation commands for the {name} feature.
 */
interface {Name}FeatureApi {
    /**
     * Navigate to {name} list screen
     * @param filter Optional filter parameter
     * @return NavCommand for navigating to {name} list
     */
    fun navigateTo{Name}List(filter: String? = null): NavCommand

    /**
     * Navigate to {name} detail screen
     * @param id The {name} ID
     * @return NavCommand for navigating to {name} detail
     */
    fun navigateTo{Name}Detail(id: String): NavCommand

    /**
     * Navigate to {name} search screen
     * @param query Search query
     * @return NavCommand for navigating to {name} search
     */
    fun navigateTo{Name}Search(query: String = ""): NavCommand
}
```

### 5. Feature API Implementation

Implement the feature API:

```kotlin
// feature-{name}/src/main/java/com/example/githubusers/feature/{name}/navigation/{Name}FeatureApiImpl.kt
package com.example.githubusers.feature.{name}.navigation

import com.example.githubusers.navigation.api.NavCommand
import javax.inject.Inject

/**
 * Implementation of {Name}FeatureApi.
 */
class {Name}FeatureApiImpl
    @Inject
    constructor() : {Name}FeatureApi {
        override fun navigateTo{Name}List(filter: String?): NavCommand {
            val deepLink = {Name}DeepLinks.{name}List(filter = filter)
            return NavCommand.Navigate(
                route = {Name}Destination.{Name}List.route,
                deepLink = deepLink,
            )
        }

        override fun navigateTo{Name}Detail(id: String): NavCommand {
            val deepLink = {Name}DeepLinks.{name}Detail(id = id)
            return NavCommand.Navigate(
                route = {Name}Destination.{Name}Detail(id).route,
                deepLink = deepLink,
            )
        }

        override fun navigateTo{Name}Search(query: String): NavCommand {
            val deepLink = {Name}DeepLinks.{name}Search(query = query)
            return NavCommand.Navigate(
                route = {Name}Destination.{Name}Search(query).route,
                deepLink = deepLink,
            )
        }
    }
```

### 6. Dependency Injection Module

Create a Hilt module to bind the components:

```kotlin
// feature-{name}/src/main/java/com/example/githubusers/feature/{name}/di/{Name}NavigationModule.kt
package com.example.githubusers.feature.{name}.di

import com.example.githubusers.feature.{name}.navigation.{Name}DeepLinkHandler
import com.example.githubusers.feature.{name}.navigation.{Name}FeatureApi
import com.example.githubusers.feature.{name}.navigation.{Name}FeatureApiImpl
import com.example.githubusers.navigation.api.DeepLinkHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for {Name} navigation components.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class {Name}NavigationModule {
    @Binds
    @Singleton
    abstract fun bind{Name}DeepLinkHandler(
        {name}DeepLinkHandler: {Name}DeepLinkHandler,
    ): DeepLinkHandler

    @Binds
    @Singleton
    abstract fun bind{Name}FeatureApi(
        {name}FeatureApiImpl: {Name}FeatureApiImpl,
    ): {Name}FeatureApi
}
```

## Usage Examples

### From Another Feature Module

```kotlin
// In another feature module
class SomeOtherFeature @Inject constructor(
    private val {name}FeatureApi: {Name}FeatureApi,
) {
    fun navigateTo{Name}() {
        val command = {name}FeatureApi.navigateTo{Name}List()
        // Execute the command through your navigation system
    }
}
```

### Direct Deep Link Usage

```kotlin
// Build deep links directly
val deepLink = {Name}DeepLinks.{name}Detail(id = "123")
// Use with your navigation system
```

## Template Variables

Replace the following placeholders in the template:

- `{name}` - lowercase feature name (e.g., "users", "settings", "profile")
- `{Name}` - PascalCase feature name (e.g., "Users", "Settings", "Profile")
- `{NAME}` - UPPERCASE feature name (e.g., "USERS", "SETTINGS", "PROFILE")

## Best Practices

1. **Naming Convention**: Use consistent naming with your feature module name
2. **Deep Link Patterns**: Support both app scheme (`app://`) and web universal links
3. **Parameter Handling**: Always URL encode parameters in deep links
4. **Error Handling**: Return `null` from deep link handlers for invalid URIs
5. **Type Safety**: Use sealed interfaces for destinations to ensure compile-time safety
6. **Documentation**: Document all public APIs and navigation patterns
7. **Testing**: Create unit tests for deep link handlers and navigation logic

## Integration with Main App

1. The deep link handler will be automatically discovered via Hilt multibindings
2. The feature API can be injected into other modules that need to navigate to this feature
3. Deep links will be resolved automatically by the navigation system
4. No manual registration is required - everything is handled by dependency injection

This template ensures that each feature module is completely self-contained for navigation while maintaining type safety and following the established patterns.

