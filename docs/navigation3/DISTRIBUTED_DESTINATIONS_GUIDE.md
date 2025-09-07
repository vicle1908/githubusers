# Distributed Destinations Development Guide

## Overview

This guide provides step-by-step instructions for implementing distributed destinations in new feature modules, following the established patterns from the completed migration.

## 🎯 Quick Star

For a new feature module, follow these steps:

1. **Create Feature Destination**
2. **Create Feature API**
3. **Create Deep Link Handler**
4. **Set up Dependency Injection**
5. **Test Navigation**

## 📋 Step-by-Step Implementation

### Step 1: Create Feature Destination

Create a sealed class/interface for your feature's destinations:

```kotlin
```kotlin
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureDestination.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.NavigationDestination
import kotlinx.serialization.Serializable

/**
 * Navigation destinations for the YourFeature module.
 */
sealed interface YourFeatureDestination : NavigationDestination {

    @Serializable
    data object YourFeatureList : YourFeatureDestination {
        override val route = "yourfeature/list"
        override val deepLink = "app://yourfeature/list"
    }

    @Serializable
    data class YourFeatureDetail(
        val id: String
    ) : YourFeatureDestination {
        override val route = "yourfeature/detail/$id"
        override val deepLink = "app://yourfeature/detail/$id"
    }

    @Serializable
    data class YourFeatureSearch(
        val query: String = ""
    ) : YourFeatureDestination {
        override val route = "yourfeature/search"
        override val deepLink = "app://yourfeature/search${if (query.isNotEmpty()) "?q=$query" else ""}"
    }
}
```

### Step 2: Create Feature API

Create a type-safe API for cross-module navigation:

```kotlin
```kotlin
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureDestination.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.NavigationDestination
import kotlinx.serialization.Serializable

/**
 * Navigation destinations for the YourFeature module.
 */
sealed interface YourFeatureDestination : NavigationDestination {

    @Serializable
    data object YourFeatureList : YourFeatureDestination {
        override val route = "yourfeature/list"
        override val deepLink = "app://yourfeature/list"
    }

    @Serializable
    data class YourFeatureDetail(
        val id: String
    ) : YourFeatureDestination {
        override val route = "yourfeature/detail/$id"
        override val deepLink = "app://yourfeature/detail/$id"
    }

    @Serializable
    data class YourFeatureSearch(
        val query: String = ""
    ) : YourFeatureDestination {
        override val route = "yourfeature/search"
        override val deepLink = "app://yourfeature/search${if (query.isNotEmpty()) "?q=$query" else ""}"
    }
}
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureApi.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.FeatureApi
import com.example.githubusers.navigation.api.NavCommand

/**
 * Feature API for the YourFeature module that provides type-safe navigation methods.
 */
interface YourFeatureApi : FeatureApi {

    override val featureId: String
        get() = "yourfeature"

    /**
     * Creates a navigation command to navigate to the feature list screen.
     */
    fun navigateToYourFeatureList(): NavCommand

    /**
     * Creates a navigation command to navigate to the feature detail screen.
     */
    fun navigateToYourFeatureDetail(id: String): NavCommand

    /**
     * Creates a navigation command to navigate to the feature search screen.
     */
    fun navigateToYourFeatureSearch(query: String = ""): NavCommand
}
```

### Step 3: Create Feature API Implementation

Implement the Feature API:

```kotlin
```kotlin
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureDestination.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.NavigationDestination
import kotlinx.serialization.Serializable

/**
 * Navigation destinations for the YourFeature module.
 */
sealed interface YourFeatureDestination : NavigationDestination {

    @Serializable
    data object YourFeatureList : YourFeatureDestination {
        override val route = "yourfeature/list"
        override val deepLink = "app://yourfeature/list"
    }

    @Serializable
    data class YourFeatureDetail(
        val id: String
    ) : YourFeatureDestination {
        override val route = "yourfeature/detail/$id"
        override val deepLink = "app://yourfeature/detail/$id"
    }

    @Serializable
    data class YourFeatureSearch(
        val query: String = ""
    ) : YourFeatureDestination {
        override val route = "yourfeature/search"
        override val deepLink = "app://yourfeature/search${if (query.isNotEmpty()) "?q=$query" else ""}"
    }
}
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureApi.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.FeatureApi
import com.example.githubusers.navigation.api.NavCommand

/**
 * Feature API for the YourFeature module that provides type-safe navigation methods.
 */
interface YourFeatureApi : FeatureApi {

    override val featureId: String
        get() = "yourfeature"

    /**
     * Creates a navigation command to navigate to the feature list screen.
     */
    fun navigateToYourFeatureList(): NavCommand

    /**
     * Creates a navigation command to navigate to the feature detail screen.
     */
    fun navigateToYourFeatureDetail(id: String): NavCommand

    /**
     * Creates a navigation command to navigate to the feature search screen.
     */
    fun navigateToYourFeatureSearch(query: String = ""): NavCommand
}
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureApiImpl.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.NavCommand
import javax.inject.Injec

/**
 * Implementation of YourFeatureApi that creates navigation commands
 * using the distributed YourFeatureDestination types.
 */
class YourFeatureApiImpl @Inject constructor() : YourFeatureApi {

    override fun navigateToYourFeatureList(): NavCommand {
        val destination = YourFeatureDestination.YourFeatureLis
        return YourFeatureNavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = emptyMap()
        )
    }

    override fun navigateToYourFeatureDetail(id: String): NavCommand {
        val destination = YourFeatureDestination.YourFeatureDetail(id)
        return YourFeatureNavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = mapOf("id" to id)
        )
    }

    override fun navigateToYourFeatureSearch(query: String): NavCommand {
        val destination = YourFeatureDestination.YourFeatureSearch(query)
        return YourFeatureNavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = mapOf("query" to query)
        )
    }
}

/**
 * Concrete implementation of NavCommand for your feature navigation.
 */
private data class YourFeatureNavCommand(
    override val route: String,
    override val deepLink: String,
    override val arguments: Map<String, Any>
) : NavCommand
```

### Step 4: Create Deep Link Handler

Create a deep link handler for your feature:

```kotlin
```kotlin
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureDestination.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.NavigationDestination
import kotlinx.serialization.Serializable

/**
 * Navigation destinations for the YourFeature module.
 */
sealed interface YourFeatureDestination : NavigationDestination {

    @Serializable
    data object YourFeatureList : YourFeatureDestination {
        override val route = "yourfeature/list"
        override val deepLink = "app://yourfeature/list"
    }

    @Serializable
    data class YourFeatureDetail(
        val id: String
    ) : YourFeatureDestination {
        override val route = "yourfeature/detail/$id"
        override val deepLink = "app://yourfeature/detail/$id"
    }

    @Serializable
    data class YourFeatureSearch(
        val query: String = ""
    ) : YourFeatureDestination {
        override val route = "yourfeature/search"
        override val deepLink = "app://yourfeature/search${if (query.isNotEmpty()) "?q=$query" else ""}"
    }
}
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureApi.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.FeatureApi
import com.example.githubusers.navigation.api.NavCommand

/**
 * Feature API for the YourFeature module that provides type-safe navigation methods.
 */
interface YourFeatureApi : FeatureApi {

    override val featureId: String
        get() = "yourfeature"

    /**
     * Creates a navigation command to navigate to the feature list screen.
     */
    fun navigateToYourFeatureList(): NavCommand

    /**
     * Creates a navigation command to navigate to the feature detail screen.
     */
    fun navigateToYourFeatureDetail(id: String): NavCommand

    /**
     * Creates a navigation command to navigate to the feature search screen.
     */
    fun navigateToYourFeatureSearch(query: String = ""): NavCommand
}
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureApiImpl.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.NavCommand
import javax.inject.Injec

/**
 * Implementation of YourFeatureApi that creates navigation commands
 * using the distributed YourFeatureDestination types.
 */
class YourFeatureApiImpl @Inject constructor() : YourFeatureApi {

    override fun navigateToYourFeatureList(): NavCommand {
        val destination = YourFeatureDestination.YourFeatureLis
        return YourFeatureNavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = emptyMap()
        )
    }

    override fun navigateToYourFeatureDetail(id: String): NavCommand {
        val destination = YourFeatureDestination.YourFeatureDetail(id)
        return YourFeatureNavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = mapOf("id" to id)
        )
    }

    override fun navigateToYourFeatureSearch(query: String): NavCommand {
        val destination = YourFeatureDestination.YourFeatureSearch(query)
        return YourFeatureNavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = mapOf("query" to query)
        )
    }
}

/**
 * Concrete implementation of NavCommand for your feature navigation.
 */
private data class YourFeatureNavCommand(
    override val route: String,
    override val deepLink: String,
    override val arguments: Map<String, Any>
) : NavCommand
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureDeepLinkHandler.k
package com.example.githubusers.feature.yourfeature.navigation

import android.net.Uri
import com.example.githubusers.navigation.annotations.OwnsDeepLinks
import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkResul
import javax.inject.Injec

/**
 * Deep link handler for the YourFeature module.
 */
@OwnsDeepLinks(moduleId = "yourfeature")
class YourFeatureDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "yourfeature"

    override fun supportedPatterns(): List<String> = listOf(
        "app://yourfeature/list",
        "app://yourfeature/detail/{id}",
        "app://yourfeature/search",
        "app://yourfeature/search?q={query}",
        "githubusers://yourfeature/list",
        "githubusers://yourfeature/detail/{id}",
        "https://githubusers.example.com/yourfeature/list",
        "https://githubusers.example.com/yourfeature/detail/{id}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val scheme = uri.scheme ?: return null
        val path = uri.path ?: ""

        return when {
            isYourFeatureListUri(scheme, uri.host, path) -> {
                DeepLinkResult(
                    route = "yourfeature/list",
                    deepLink = uri.toString(),
                    arguments = emptyMap(),
                    clearBackStack = uri.getBooleanQueryParameter("clear_stack", false)
                )
            }

            isYourFeatureDetailUri(scheme, uri.host, path) -> {
                val id = extractId(path)
                if (id != null) {
                    DeepLinkResult(
                        route = "yourfeature/detail/$id",
                        deepLink = uri.toString(),
                        arguments = mapOf("id" to id),
                        clearBackStack = uri.getBooleanQueryParameter("clear_stack", false)
                    )
                } else null
            }

            isYourFeatureSearchUri(scheme, uri.host, path) -> {
                val query = uri.getQueryParameter("q") ?: ""
                DeepLinkResult(
                    route = "yourfeature/search",
                    deepLink = uri.toString(),
                    arguments = mapOf("query" to query),
                    clearBackStack = uri.getBooleanQueryParameter("clear_stack", false)
                )
            }

            else -> null
        }
    }

    private fun isYourFeatureListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path == "/yourfeature/list" -> true
            scheme == "githubusers" && (host == "yourfeature" || path == "/yourfeature/list") -> true
            scheme in listOf("http", "https") && host == "githubusers.example.com" && path == "/yourfeature/list" -> true
            else -> false
        }

    private fun isYourFeatureDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/yourfeature/detail/") -> true
            scheme == "githubusers" && path.startsWith("/yourfeature/detail/") -> true
            scheme in listOf("http", "https") && host == "githubusers.example.com" && path.startsWith("/yourfeature/detail/") -> true
            else -> false
        }

    private fun isYourFeatureSearchUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/yourfeature/search") -> true
            scheme == "githubusers" && path.startsWith("/yourfeature/search") -> true
            scheme in listOf("http", "https") && host == "githubusers.example.com" && path.startsWith("/yourfeature/search") -> true
            else -> false
        }

    private fun extractId(path: String): String? {
        return when {
            path.startsWith("/yourfeature/detail/") -> {
                path.removePrefix("/yourfeature/detail/").takeIf { it.isNotEmpty() }
            }
            else -> null
        }
    }
}
```

### Step 5: Set up Dependency Injection

Create a Hilt module for your feature navigation:

```kotlin
```kotlin
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureDestination.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.NavigationDestination
import kotlinx.serialization.Serializable

/**
 * Navigation destinations for the YourFeature module.
 */
sealed interface YourFeatureDestination : NavigationDestination {

    @Serializable
    data object YourFeatureList : YourFeatureDestination {
        override val route = "yourfeature/list"
        override val deepLink = "app://yourfeature/list"
    }

    @Serializable
    data class YourFeatureDetail(
        val id: String
    ) : YourFeatureDestination {
        override val route = "yourfeature/detail/$id"
        override val deepLink = "app://yourfeature/detail/$id"
    }

    @Serializable
    data class YourFeatureSearch(
        val query: String = ""
    ) : YourFeatureDestination {
        override val route = "yourfeature/search"
        override val deepLink = "app://yourfeature/search${if (query.isNotEmpty()) "?q=$query" else ""}"
    }
}
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureApi.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.FeatureApi
import com.example.githubusers.navigation.api.NavCommand

/**
 * Feature API for the YourFeature module that provides type-safe navigation methods.
 */
interface YourFeatureApi : FeatureApi {

    override val featureId: String
        get() = "yourfeature"

    /**
     * Creates a navigation command to navigate to the feature list screen.
     */
    fun navigateToYourFeatureList(): NavCommand

    /**
     * Creates a navigation command to navigate to the feature detail screen.
     */
    fun navigateToYourFeatureDetail(id: String): NavCommand

    /**
     * Creates a navigation command to navigate to the feature search screen.
     */
    fun navigateToYourFeatureSearch(query: String = ""): NavCommand
}
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureApiImpl.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.NavCommand
import javax.inject.Injec

/**
 * Implementation of YourFeatureApi that creates navigation commands
 * using the distributed YourFeatureDestination types.
 */
class YourFeatureApiImpl @Inject constructor() : YourFeatureApi {

    override fun navigateToYourFeatureList(): NavCommand {
        val destination = YourFeatureDestination.YourFeatureLis
        return YourFeatureNavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = emptyMap()
        )
    }

    override fun navigateToYourFeatureDetail(id: String): NavCommand {
        val destination = YourFeatureDestination.YourFeatureDetail(id)
        return YourFeatureNavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = mapOf("id" to id)
        )
    }

    override fun navigateToYourFeatureSearch(query: String): NavCommand {
        val destination = YourFeatureDestination.YourFeatureSearch(query)
        return YourFeatureNavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = mapOf("query" to query)
        )
    }
}

/**
 * Concrete implementation of NavCommand for your feature navigation.
 */
private data class YourFeatureNavCommand(
    override val route: String,
    override val deepLink: String,
    override val arguments: Map<String, Any>
) : NavCommand
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureDeepLinkHandler.k
package com.example.githubusers.feature.yourfeature.navigation

import android.net.Uri
import com.example.githubusers.navigation.annotations.OwnsDeepLinks
import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkResul
import javax.inject.Injec

/**
 * Deep link handler for the YourFeature module.
 */
@OwnsDeepLinks(moduleId = "yourfeature")
class YourFeatureDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "yourfeature"

    override fun supportedPatterns(): List<String> = listOf(
        "app://yourfeature/list",
        "app://yourfeature/detail/{id}",
        "app://yourfeature/search",
        "app://yourfeature/search?q={query}",
        "githubusers://yourfeature/list",
        "githubusers://yourfeature/detail/{id}",
        "https://githubusers.example.com/yourfeature/list",
        "https://githubusers.example.com/yourfeature/detail/{id}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val scheme = uri.scheme ?: return null
        val path = uri.path ?: ""

        return when {
            isYourFeatureListUri(scheme, uri.host, path) -> {
                DeepLinkResult(
                    route = "yourfeature/list",
                    deepLink = uri.toString(),
                    arguments = emptyMap(),
                    clearBackStack = uri.getBooleanQueryParameter("clear_stack", false)
                )
            }

            isYourFeatureDetailUri(scheme, uri.host, path) -> {
                val id = extractId(path)
                if (id != null) {
                    DeepLinkResult(
                        route = "yourfeature/detail/$id",
                        deepLink = uri.toString(),
                        arguments = mapOf("id" to id),
                        clearBackStack = uri.getBooleanQueryParameter("clear_stack", false)
                    )
                } else null
            }

            isYourFeatureSearchUri(scheme, uri.host, path) -> {
                val query = uri.getQueryParameter("q") ?: ""
                DeepLinkResult(
                    route = "yourfeature/search",
                    deepLink = uri.toString(),
                    arguments = mapOf("query" to query),
                    clearBackStack = uri.getBooleanQueryParameter("clear_stack", false)
                )
            }

            else -> null
        }
    }

    private fun isYourFeatureListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path == "/yourfeature/list" -> true
            scheme == "githubusers" && (host == "yourfeature" || path == "/yourfeature/list") -> true
            scheme in listOf("http", "https") && host == "githubusers.example.com" && path == "/yourfeature/list" -> true
            else -> false
        }

    private fun isYourFeatureDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/yourfeature/detail/") -> true
            scheme == "githubusers" && path.startsWith("/yourfeature/detail/") -> true
            scheme in listOf("http", "https") && host == "githubusers.example.com" && path.startsWith("/yourfeature/detail/") -> true
            else -> false
        }

    private fun isYourFeatureSearchUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/yourfeature/search") -> true
            scheme == "githubusers" && path.startsWith("/yourfeature/search") -> true
            scheme in listOf("http", "https") && host == "githubusers.example.com" && path.startsWith("/yourfeature/search") -> true
            else -> false
        }

    private fun extractId(path: String): String? {
        return when {
            path.startsWith("/yourfeature/detail/") -> {
                path.removePrefix("/yourfeature/detail/").takeIf { it.isNotEmpty() }
            }
            else -> null
        }
    }
}
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/di/YourFeatureNavigationModule.k
package com.example.githubusers.feature.yourfeature.di

import com.example.githubusers.feature.yourfeature.navigation.YourFeatureApi
import com.example.githubusers.feature.yourfeature.navigation.YourFeatureApiImpl
import com.example.githubusers.feature.yourfeature.navigation.YourFeatureDeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponen
import dagger.multibindings.IntoSe

/**
 * Hilt module for YourFeature navigation.
 * Registers the feature's deep link handler and API with the navigation system.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class YourFeatureNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindYourFeatureDeepLinkHandler(handler: YourFeatureDeepLinkHandler): DeepLinkHandler

    @Binds
    abstract fun bindYourFeatureApi(impl: YourFeatureApiImpl): YourFeatureApi
}
```

### Step 6: Update MainActivity

Add your feature's destinations to the MainActivity navigation:

```kotlin
```kotlin
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureDestination.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.NavigationDestination
import kotlinx.serialization.Serializable

/**
 * Navigation destinations for the YourFeature module.
 */
sealed interface YourFeatureDestination : NavigationDestination {

    @Serializable
    data object YourFeatureList : YourFeatureDestination {
        override val route = "yourfeature/list"
        override val deepLink = "app://yourfeature/list"
    }

    @Serializable
    data class YourFeatureDetail(
        val id: String
    ) : YourFeatureDestination {
        override val route = "yourfeature/detail/$id"
        override val deepLink = "app://yourfeature/detail/$id"
    }

    @Serializable
    data class YourFeatureSearch(
        val query: String = ""
    ) : YourFeatureDestination {
        override val route = "yourfeature/search"
        override val deepLink = "app://yourfeature/search${if (query.isNotEmpty()) "?q=$query" else ""}"
    }
}
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureApi.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.FeatureApi
import com.example.githubusers.navigation.api.NavCommand

/**
 * Feature API for the YourFeature module that provides type-safe navigation methods.
 */
interface YourFeatureApi : FeatureApi {

    override val featureId: String
        get() = "yourfeature"

    /**
     * Creates a navigation command to navigate to the feature list screen.
     */
    fun navigateToYourFeatureList(): NavCommand

    /**
     * Creates a navigation command to navigate to the feature detail screen.
     */
    fun navigateToYourFeatureDetail(id: String): NavCommand

    /**
     * Creates a navigation command to navigate to the feature search screen.
     */
    fun navigateToYourFeatureSearch(query: String = ""): NavCommand
}
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureApiImpl.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.NavCommand
import javax.inject.Injec

/**
 * Implementation of YourFeatureApi that creates navigation commands
 * using the distributed YourFeatureDestination types.
 */
class YourFeatureApiImpl @Inject constructor() : YourFeatureApi {

    override fun navigateToYourFeatureList(): NavCommand {
        val destination = YourFeatureDestination.YourFeatureLis
        return YourFeatureNavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = emptyMap()
        )
    }

    override fun navigateToYourFeatureDetail(id: String): NavCommand {
        val destination = YourFeatureDestination.YourFeatureDetail(id)
        return YourFeatureNavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = mapOf("id" to id)
        )
    }

    override fun navigateToYourFeatureSearch(query: String): NavCommand {
        val destination = YourFeatureDestination.YourFeatureSearch(query)
        return YourFeatureNavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = mapOf("query" to query)
        )
    }
}

/**
 * Concrete implementation of NavCommand for your feature navigation.
 */
private data class YourFeatureNavCommand(
    override val route: String,
    override val deepLink: String,
    override val arguments: Map<String, Any>
) : NavCommand
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureDeepLinkHandler.k
package com.example.githubusers.feature.yourfeature.navigation

import android.net.Uri
import com.example.githubusers.navigation.annotations.OwnsDeepLinks
import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkResul
import javax.inject.Injec

/**
 * Deep link handler for the YourFeature module.
 */
@OwnsDeepLinks(moduleId = "yourfeature")
class YourFeatureDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "yourfeature"

    override fun supportedPatterns(): List<String> = listOf(
        "app://yourfeature/list",
        "app://yourfeature/detail/{id}",
        "app://yourfeature/search",
        "app://yourfeature/search?q={query}",
        "githubusers://yourfeature/list",
        "githubusers://yourfeature/detail/{id}",
        "https://githubusers.example.com/yourfeature/list",
        "https://githubusers.example.com/yourfeature/detail/{id}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val scheme = uri.scheme ?: return null
        val path = uri.path ?: ""

        return when {
            isYourFeatureListUri(scheme, uri.host, path) -> {
                DeepLinkResult(
                    route = "yourfeature/list",
                    deepLink = uri.toString(),
                    arguments = emptyMap(),
                    clearBackStack = uri.getBooleanQueryParameter("clear_stack", false)
                )
            }

            isYourFeatureDetailUri(scheme, uri.host, path) -> {
                val id = extractId(path)
                if (id != null) {
                    DeepLinkResult(
                        route = "yourfeature/detail/$id",
                        deepLink = uri.toString(),
                        arguments = mapOf("id" to id),
                        clearBackStack = uri.getBooleanQueryParameter("clear_stack", false)
                    )
                } else null
            }

            isYourFeatureSearchUri(scheme, uri.host, path) -> {
                val query = uri.getQueryParameter("q") ?: ""
                DeepLinkResult(
                    route = "yourfeature/search",
                    deepLink = uri.toString(),
                    arguments = mapOf("query" to query),
                    clearBackStack = uri.getBooleanQueryParameter("clear_stack", false)
                )
            }

            else -> null
        }
    }

    private fun isYourFeatureListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path == "/yourfeature/list" -> true
            scheme == "githubusers" && (host == "yourfeature" || path == "/yourfeature/list") -> true
            scheme in listOf("http", "https") && host == "githubusers.example.com" && path == "/yourfeature/list" -> true
            else -> false
        }

    private fun isYourFeatureDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/yourfeature/detail/") -> true
            scheme == "githubusers" && path.startsWith("/yourfeature/detail/") -> true
            scheme in listOf("http", "https") && host == "githubusers.example.com" && path.startsWith("/yourfeature/detail/") -> true
            else -> false
        }

    private fun isYourFeatureSearchUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/yourfeature/search") -> true
            scheme == "githubusers" && path.startsWith("/yourfeature/search") -> true
            scheme in listOf("http", "https") && host == "githubusers.example.com" && path.startsWith("/yourfeature/search") -> true
            else -> false
        }

    private fun extractId(path: String): String? {
        return when {
            path.startsWith("/yourfeature/detail/") -> {
                path.removePrefix("/yourfeature/detail/").takeIf { it.isNotEmpty() }
            }
            else -> null
        }
    }
}
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/di/YourFeatureNavigationModule.k
package com.example.githubusers.feature.yourfeature.di

import com.example.githubusers.feature.yourfeature.navigation.YourFeatureApi
import com.example.githubusers.feature.yourfeature.navigation.YourFeatureApiImpl
import com.example.githubusers.feature.yourfeature.navigation.YourFeatureDeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponen
import dagger.multibindings.IntoSe

/**
 * Hilt module for YourFeature navigation.
 * Registers the feature's deep link handler and API with the navigation system.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class YourFeatureNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindYourFeatureDeepLinkHandler(handler: YourFeatureDeepLinkHandler): DeepLinkHandler

    @Binds
    abstract fun bindYourFeatureApi(impl: YourFeatureApiImpl): YourFeatureApi
}
// In app/src/main/java/com/example/githubusers/presentation/MainActivity.k
// Add to the Navigation3Host content lambda:

when {
    // ... existing cases ...

    entry.destination.route == "yourfeature/list" -> {
        // Your feature list screen
        YourFeatureListScreen()
    }

    entry.destination.route.startsWith("yourfeature/detail/") -> {
        val id = entry.arguments["id"] as? String ?: ""
        // Your feature detail screen
        YourFeatureDetailScreen(id = id)
    }

    entry.destination.route == "yourfeature/search" -> {
        val query = entry.arguments["query"] as? String ?: ""
        // Your feature search screen
        YourFeatureSearchScreen(query = query)
    }
}
```

## 🔧 Best Practices

### 1. **Naming Conventions**

- Use descriptive names: `YourFeatureDestination`, `YourFeatureApi`, `YourFeatureDeepLinkHandler`
- Keep module ID consistent: `"yourfeature"` (lowercase, no spaces)
- Use consistent route patterns: `"yourfeature/list"`, `"yourfeature/detail/{id}"`

### 2. **Deep Link Patterns**

- Support multiple schemes: `app://`, `githubusers://`, `https://`
- Use consistent path structures
- Include query parameters for optional data
- Support `clear_stack` parameter for navigation options

### 3. **Error Handling**

- Return `null` from `handleDeepLink` for unsupported URIs
- Validate required parameters (e.g., ID extraction)
- Use safe navigation and null checks

### 4. **Type Safety**

- Use sealed classes/interfaces for destinations
- Implement `FeatureApi` for cross-module navigation
- Use `NavCommand` for type-safe navigation commands

### 5. **Testing**

- Test deep link handling with various URI formats
- Test navigation commands
- Test error cases (invalid URIs, missing parameters)

## 🚀 Usage Examples

### Cross-Module Navigation

```kotlin
```kotlin
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureDestination.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.NavigationDestination
import kotlinx.serialization.Serializable

/**
 * Navigation destinations for the YourFeature module.
 */
sealed interface YourFeatureDestination : NavigationDestination {

    @Serializable
    data object YourFeatureList : YourFeatureDestination {
        override val route = "yourfeature/list"
        override val deepLink = "app://yourfeature/list"
    }

    @Serializable
    data class YourFeatureDetail(
        val id: String
    ) : YourFeatureDestination {
        override val route = "yourfeature/detail/$id"
        override val deepLink = "app://yourfeature/detail/$id"
    }

    @Serializable
    data class YourFeatureSearch(
        val query: String = ""
    ) : YourFeatureDestination {
        override val route = "yourfeature/search"
        override val deepLink = "app://yourfeature/search${if (query.isNotEmpty()) "?q=$query" else ""}"
    }
}
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureApi.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.FeatureApi
import com.example.githubusers.navigation.api.NavCommand

/**
 * Feature API for the YourFeature module that provides type-safe navigation methods.
 */
interface YourFeatureApi : FeatureApi {

    override val featureId: String
        get() = "yourfeature"

    /**
     * Creates a navigation command to navigate to the feature list screen.
     */
    fun navigateToYourFeatureList(): NavCommand

    /**
     * Creates a navigation command to navigate to the feature detail screen.
     */
    fun navigateToYourFeatureDetail(id: String): NavCommand

    /**
     * Creates a navigation command to navigate to the feature search screen.
     */
    fun navigateToYourFeatureSearch(query: String = ""): NavCommand
}
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureApiImpl.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.NavCommand
import javax.inject.Injec

/**
 * Implementation of YourFeatureApi that creates navigation commands
 * using the distributed YourFeatureDestination types.
 */
class YourFeatureApiImpl @Inject constructor() : YourFeatureApi {

    override fun navigateToYourFeatureList(): NavCommand {
        val destination = YourFeatureDestination.YourFeatureLis
        return YourFeatureNavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = emptyMap()
        )
    }

    override fun navigateToYourFeatureDetail(id: String): NavCommand {
        val destination = YourFeatureDestination.YourFeatureDetail(id)
        return YourFeatureNavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = mapOf("id" to id)
        )
    }

    override fun navigateToYourFeatureSearch(query: String): NavCommand {
        val destination = YourFeatureDestination.YourFeatureSearch(query)
        return YourFeatureNavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = mapOf("query" to query)
        )
    }
}

/**
 * Concrete implementation of NavCommand for your feature navigation.
 */
private data class YourFeatureNavCommand(
    override val route: String,
    override val deepLink: String,
    override val arguments: Map<String, Any>
) : NavCommand
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureDeepLinkHandler.k
package com.example.githubusers.feature.yourfeature.navigation

import android.net.Uri
import com.example.githubusers.navigation.annotations.OwnsDeepLinks
import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkResul
import javax.inject.Injec

/**
 * Deep link handler for the YourFeature module.
 */
@OwnsDeepLinks(moduleId = "yourfeature")
class YourFeatureDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "yourfeature"

    override fun supportedPatterns(): List<String> = listOf(
        "app://yourfeature/list",
        "app://yourfeature/detail/{id}",
        "app://yourfeature/search",
        "app://yourfeature/search?q={query}",
        "githubusers://yourfeature/list",
        "githubusers://yourfeature/detail/{id}",
        "https://githubusers.example.com/yourfeature/list",
        "https://githubusers.example.com/yourfeature/detail/{id}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val scheme = uri.scheme ?: return null
        val path = uri.path ?: ""

        return when {
            isYourFeatureListUri(scheme, uri.host, path) -> {
                DeepLinkResult(
                    route = "yourfeature/list",
                    deepLink = uri.toString(),
                    arguments = emptyMap(),
                    clearBackStack = uri.getBooleanQueryParameter("clear_stack", false)
                )
            }

            isYourFeatureDetailUri(scheme, uri.host, path) -> {
                val id = extractId(path)
                if (id != null) {
                    DeepLinkResult(
                        route = "yourfeature/detail/$id",
                        deepLink = uri.toString(),
                        arguments = mapOf("id" to id),
                        clearBackStack = uri.getBooleanQueryParameter("clear_stack", false)
                    )
                } else null
            }

            isYourFeatureSearchUri(scheme, uri.host, path) -> {
                val query = uri.getQueryParameter("q") ?: ""
                DeepLinkResult(
                    route = "yourfeature/search",
                    deepLink = uri.toString(),
                    arguments = mapOf("query" to query),
                    clearBackStack = uri.getBooleanQueryParameter("clear_stack", false)
                )
            }

            else -> null
        }
    }

    private fun isYourFeatureListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path == "/yourfeature/list" -> true
            scheme == "githubusers" && (host == "yourfeature" || path == "/yourfeature/list") -> true
            scheme in listOf("http", "https") && host == "githubusers.example.com" && path == "/yourfeature/list" -> true
            else -> false
        }

    private fun isYourFeatureDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/yourfeature/detail/") -> true
            scheme == "githubusers" && path.startsWith("/yourfeature/detail/") -> true
            scheme in listOf("http", "https") && host == "githubusers.example.com" && path.startsWith("/yourfeature/detail/") -> true
            else -> false
        }

    private fun isYourFeatureSearchUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/yourfeature/search") -> true
            scheme == "githubusers" && path.startsWith("/yourfeature/search") -> true
            scheme in listOf("http", "https") && host == "githubusers.example.com" && path.startsWith("/yourfeature/search") -> true
            else -> false
        }

    private fun extractId(path: String): String? {
        return when {
            path.startsWith("/yourfeature/detail/") -> {
                path.removePrefix("/yourfeature/detail/").takeIf { it.isNotEmpty() }
            }
            else -> null
        }
    }
}
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/di/YourFeatureNavigationModule.k
package com.example.githubusers.feature.yourfeature.di

import com.example.githubusers.feature.yourfeature.navigation.YourFeatureApi
import com.example.githubusers.feature.yourfeature.navigation.YourFeatureApiImpl
import com.example.githubusers.feature.yourfeature.navigation.YourFeatureDeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponen
import dagger.multibindings.IntoSe

/**
 * Hilt module for YourFeature navigation.
 * Registers the feature's deep link handler and API with the navigation system.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class YourFeatureNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindYourFeatureDeepLinkHandler(handler: YourFeatureDeepLinkHandler): DeepLinkHandler

    @Binds
    abstract fun bindYourFeatureApi(impl: YourFeatureApiImpl): YourFeatureApi
}
// In app/src/main/java/com/example/githubusers/presentation/MainActivity.k
// Add to the Navigation3Host content lambda:

when {
    // ... existing cases ...

    entry.destination.route == "yourfeature/list" -> {
        // Your feature list screen
        YourFeatureListScreen()
    }

    entry.destination.route.startsWith("yourfeature/detail/") -> {
        val id = entry.arguments["id"] as? String ?: ""
        // Your feature detail screen
        YourFeatureDetailScreen(id = id)
    }

    entry.destination.route == "yourfeature/search" -> {
        val query = entry.arguments["query"] as? String ?: ""
        // Your feature search screen
        YourFeatureSearchScreen(query = query)
    }
}
// In another feature module
@Injec
lateinit var yourFeatureApi: YourFeatureApi

// Navigate to your feature
navigationController.navigate(yourFeatureApi.navigateToYourFeatureList())

// Navigate with parameters
navigationController.navigate(yourFeatureApi.navigateToYourFeatureDetail("123"))
```

### Deep Link Navigation

```kotlin
```kotlin
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureDestination.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.NavigationDestination
import kotlinx.serialization.Serializable

/**
 * Navigation destinations for the YourFeature module.
 */
sealed interface YourFeatureDestination : NavigationDestination {

    @Serializable
    data object YourFeatureList : YourFeatureDestination {
        override val route = "yourfeature/list"
        override val deepLink = "app://yourfeature/list"
    }

    @Serializable
    data class YourFeatureDetail(
        val id: String
    ) : YourFeatureDestination {
        override val route = "yourfeature/detail/$id"
        override val deepLink = "app://yourfeature/detail/$id"
    }

    @Serializable
    data class YourFeatureSearch(
        val query: String = ""
    ) : YourFeatureDestination {
        override val route = "yourfeature/search"
        override val deepLink = "app://yourfeature/search${if (query.isNotEmpty()) "?q=$query" else ""}"
    }
}
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureApi.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.FeatureApi
import com.example.githubusers.navigation.api.NavCommand

/**
 * Feature API for the YourFeature module that provides type-safe navigation methods.
 */
interface YourFeatureApi : FeatureApi {

    override val featureId: String
        get() = "yourfeature"

    /**
     * Creates a navigation command to navigate to the feature list screen.
     */
    fun navigateToYourFeatureList(): NavCommand

    /**
     * Creates a navigation command to navigate to the feature detail screen.
     */
    fun navigateToYourFeatureDetail(id: String): NavCommand

    /**
     * Creates a navigation command to navigate to the feature search screen.
     */
    fun navigateToYourFeatureSearch(query: String = ""): NavCommand
}
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureApiImpl.k
package com.example.githubusers.feature.yourfeature.navigation

import com.example.githubusers.navigation.api.NavCommand
import javax.inject.Injec

/**
 * Implementation of YourFeatureApi that creates navigation commands
 * using the distributed YourFeatureDestination types.
 */
class YourFeatureApiImpl @Inject constructor() : YourFeatureApi {

    override fun navigateToYourFeatureList(): NavCommand {
        val destination = YourFeatureDestination.YourFeatureLis
        return YourFeatureNavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = emptyMap()
        )
    }

    override fun navigateToYourFeatureDetail(id: String): NavCommand {
        val destination = YourFeatureDestination.YourFeatureDetail(id)
        return YourFeatureNavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = mapOf("id" to id)
        )
    }

    override fun navigateToYourFeatureSearch(query: String): NavCommand {
        val destination = YourFeatureDestination.YourFeatureSearch(query)
        return YourFeatureNavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = mapOf("query" to query)
        )
    }
}

/**
 * Concrete implementation of NavCommand for your feature navigation.
 */
private data class YourFeatureNavCommand(
    override val route: String,
    override val deepLink: String,
    override val arguments: Map<String, Any>
) : NavCommand
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/navigation/YourFeatureDeepLinkHandler.k
package com.example.githubusers.feature.yourfeature.navigation

import android.net.Uri
import com.example.githubusers.navigation.annotations.OwnsDeepLinks
import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkResul
import javax.inject.Injec

/**
 * Deep link handler for the YourFeature module.
 */
@OwnsDeepLinks(moduleId = "yourfeature")
class YourFeatureDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "yourfeature"

    override fun supportedPatterns(): List<String> = listOf(
        "app://yourfeature/list",
        "app://yourfeature/detail/{id}",
        "app://yourfeature/search",
        "app://yourfeature/search?q={query}",
        "githubusers://yourfeature/list",
        "githubusers://yourfeature/detail/{id}",
        "https://githubusers.example.com/yourfeature/list",
        "https://githubusers.example.com/yourfeature/detail/{id}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val scheme = uri.scheme ?: return null
        val path = uri.path ?: ""

        return when {
            isYourFeatureListUri(scheme, uri.host, path) -> {
                DeepLinkResult(
                    route = "yourfeature/list",
                    deepLink = uri.toString(),
                    arguments = emptyMap(),
                    clearBackStack = uri.getBooleanQueryParameter("clear_stack", false)
                )
            }

            isYourFeatureDetailUri(scheme, uri.host, path) -> {
                val id = extractId(path)
                if (id != null) {
                    DeepLinkResult(
                        route = "yourfeature/detail/$id",
                        deepLink = uri.toString(),
                        arguments = mapOf("id" to id),
                        clearBackStack = uri.getBooleanQueryParameter("clear_stack", false)
                    )
                } else null
            }

            isYourFeatureSearchUri(scheme, uri.host, path) -> {
                val query = uri.getQueryParameter("q") ?: ""
                DeepLinkResult(
                    route = "yourfeature/search",
                    deepLink = uri.toString(),
                    arguments = mapOf("query" to query),
                    clearBackStack = uri.getBooleanQueryParameter("clear_stack", false)
                )
            }

            else -> null
        }
    }

    private fun isYourFeatureListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path == "/yourfeature/list" -> true
            scheme == "githubusers" && (host == "yourfeature" || path == "/yourfeature/list") -> true
            scheme in listOf("http", "https") && host == "githubusers.example.com" && path == "/yourfeature/list" -> true
            else -> false
        }

    private fun isYourFeatureDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/yourfeature/detail/") -> true
            scheme == "githubusers" && path.startsWith("/yourfeature/detail/") -> true
            scheme in listOf("http", "https") && host == "githubusers.example.com" && path.startsWith("/yourfeature/detail/") -> true
            else -> false
        }

    private fun isYourFeatureSearchUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/yourfeature/search") -> true
            scheme == "githubusers" && path.startsWith("/yourfeature/search") -> true
            scheme in listOf("http", "https") && host == "githubusers.example.com" && path.startsWith("/yourfeature/search") -> true
            else -> false
        }

    private fun extractId(path: String): String? {
        return when {
            path.startsWith("/yourfeature/detail/") -> {
                path.removePrefix("/yourfeature/detail/").takeIf { it.isNotEmpty() }
            }
            else -> null
        }
    }
}
// In your-feature/src/main/java/com/example/githubusers/feature/yourfeature/di/YourFeatureNavigationModule.k
package com.example.githubusers.feature.yourfeature.di

import com.example.githubusers.feature.yourfeature.navigation.YourFeatureApi
import com.example.githubusers.feature.yourfeature.navigation.YourFeatureApiImpl
import com.example.githubusers.feature.yourfeature.navigation.YourFeatureDeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponen
import dagger.multibindings.IntoSe

/**
 * Hilt module for YourFeature navigation.
 * Registers the feature's deep link handler and API with the navigation system.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class YourFeatureNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindYourFeatureDeepLinkHandler(handler: YourFeatureDeepLinkHandler): DeepLinkHandler

    @Binds
    abstract fun bindYourFeatureApi(impl: YourFeatureApiImpl): YourFeatureApi
}
// In app/src/main/java/com/example/githubusers/presentation/MainActivity.k
// Add to the Navigation3Host content lambda:

when {
    // ... existing cases ...

    entry.destination.route == "yourfeature/list" -> {
        // Your feature list screen
        YourFeatureListScreen()
    }

    entry.destination.route.startsWith("yourfeature/detail/") -> {
        val id = entry.arguments["id"] as? String ?: ""
        // Your feature detail screen
        YourFeatureDetailScreen(id = id)
    }

    entry.destination.route == "yourfeature/search" -> {
        val query = entry.arguments["query"] as? String ?: ""
        // Your feature search screen
        YourFeatureSearchScreen(query = query)
    }
}
// In another feature module
@Injec
lateinit var yourFeatureApi: YourFeatureApi

// Navigate to your feature
navigationController.navigate(yourFeatureApi.navigateToYourFeatureList())

// Navigate with parameters
navigationController.navigate(yourFeatureApi.navigateToYourFeatureDetail("123"))
// Navigate using deep links
navigationController.navigate("app://yourfeature/list")
navigationController.navigate("app://yourfeature/detail/123")
navigationController.navigate("app://yourfeature/search?q=test")
```

## ✅ Checklis

Before considering your feature complete, ensure:

- [ ] Feature destination sealed class/interface created
- [ ] Feature API interface and implementation created
- [ ] Deep link handler implemented with proper URI patterns
- [ ] Hilt module created and registered
- [ ] MainActivity updated to handle your destinations
- [ ] Deep link patterns tested
- [ ] Cross-module navigation tested
- [ ] Error cases handled properly

## 🎯 Benefits

Following this pattern provides:

1. **Feature Independence**: Your feature owns its navigation
2. **Type Safety**: Compile-time navigation safety
3. **Scalability**: Easy to add new destinations
4. **Maintainability**: Changes don't affect other features
5. **Testability**: Clear interfaces for testing
6. **Consistency**: Follows established patterns

## 📚 References

- [Distributed Destinations Migration](./DISTRIBUTED_DESTINATIONS_MIGRATION.md) - Complete migration documentation
- [Navigation 3 Implementation](./NAVIGATION_3_IMPLEMENTATION.md) - Core navigation system documentation
- [Multi-Module Deep Link Architecture](./MULTI_MODULE_DEEPLINK_ARCHITECTURE.md) - Deep link architecture details
