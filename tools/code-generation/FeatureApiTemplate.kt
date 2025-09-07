/**
 * Code Generation Template for FeatureApi Implementation
 * 
 * This template provides a starting point for generating FeatureApi implementations
 * for new feature modules following the distributed destinations pattern.
 * 
 * Usage:
 * 1. Copy this template
 * 2. Replace {FeatureName} with your actual feature name
 * 3. Replace {featureName} with your actual feature name (lowercase)
 * 4. Add your specific navigation methods
 * 5. Implement the NavCommand data class
 */

package com.example.githubusers.feature.{featureName}.navigation

import com.example.githubusers.navigation.api.FeatureApi
import com.example.githubusers.navigation.api.NavCommand
import javax.inject.Inject

/**
 * Feature API for the {FeatureName} module that provides type-safe navigation methods.
 * 
 * This interface enables other modules to navigate to {featureName} screens without
 * directly depending on the {featureName} feature module's internal destination types.
 * 
 * Example usage:
 * ```kotlin
 * // In another feature module
 * @Inject
 * lateinit var {featureName}FeatureApi: {FeatureName}FeatureApi
 * 
 * // Navigate to {featureName} list
 * navigationController.navigate({featureName}FeatureApi.navigateTo{FeatureName}List())
 * 
 * // Navigate to {featureName} detail
 * navigationController.navigate({featureName}FeatureApi.navigateTo{FeatureName}Detail("id"))
 * ```
 */
interface {FeatureName}FeatureApi : FeatureApi {
    
    override val featureId: String
        get() = "{featureName}"
    
    /**
     * Creates a navigation command to navigate to the {featureName} list screen.
     * 
     * @return NavCommand for navigating to {featureName} list
     */
    fun navigateTo{FeatureName}List(): NavCommand
    
    /**
     * Creates a navigation command to navigate to the {featureName} detail screen.
     * 
     * @param id The ID of the {featureName} to navigate to
     * @return NavCommand for navigating to {featureName} detail
     */
    fun navigateTo{FeatureName}Detail(id: String): NavCommand
    
    // Add more navigation methods as needed
    // fun navigateTo{FeatureName}Search(query: String): NavCommand
    // fun navigateTo{FeatureName}Settings(): NavCommand
}

/**
 * Implementation of {FeatureName}FeatureApi that creates navigation commands
 * using the distributed {FeatureName}Destination types.
 */
class {FeatureName}FeatureApiImpl @Inject constructor() : {FeatureName}FeatureApi {
    
    override fun navigateTo{FeatureName}List(): NavCommand {
        val destination = {FeatureName}Destination.{FeatureName}List
        return {FeatureName}NavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = emptyMap()
        )
    }
    
    override fun navigateTo{FeatureName}Detail(id: String): NavCommand {
        val destination = {FeatureName}Destination.{FeatureName}Detail(id)
        return {FeatureName}NavCommand(
            route = destination.route,
            deepLink = destination.deepLink,
            arguments = mapOf("id" to id)
        )
    }
    
    // Implement additional navigation methods
    // override fun navigateTo{FeatureName}Search(query: String): NavCommand {
    //     val destination = {FeatureName}Destination.{FeatureName}Search(query)
    //     return {FeatureName}NavCommand(
    //         route = destination.route,
    //         deepLink = destination.deepLink,
    //         arguments = mapOf("query" to query)
    //     )
    // }
}

/**
 * Concrete implementation of NavCommand for {featureName} navigation.
 */
private data class {FeatureName}NavCommand(
    override val route: String,
    override val deepLink: String,
    override val arguments: Map<String, Any>
) : NavCommand

/**
 * Hilt module for {FeatureName} navigation.
 * Add this to your feature's DI module.
 */
/*
@Module
@InstallIn(SingletonComponent::class)
abstract class {FeatureName}NavigationModule {
    
    @Binds
    @IntoSet
    abstract fun bind{FeatureName}DeepLinkHandler(handler: {FeatureName}DeepLinkHandler): DeepLinkHandler
    
    @Binds
    abstract fun bind{FeatureName}FeatureApi(impl: {FeatureName}FeatureApiImpl): {FeatureName}FeatureApi
}
*/

/**
 * Example {FeatureName}Destination sealed class.
 * Replace with your actual destination definitions.
 */
/*
sealed interface {FeatureName}Destination : NavigationDestination {
    
    data object {FeatureName}List : {FeatureName}Destination {
        override val route = "{featureName}/list"
        override val deepLink = "app://{featureName}/list"
    }
    
    data class {FeatureName}Detail(
        val id: String
    ) : {FeatureName}Destination {
        override val route = "{featureName}/detail/$id"
        override val deepLink = "app://{featureName}/detail/$id"
    }
    
    // Add more destinations as needed
    // data class {FeatureName}Search(
    //     val query: String = ""
    // ) : {FeatureName}Destination {
    //     override val route = "{featureName}/search"
    //     override val deepLink = "app://{featureName}/search${if (query.isNotEmpty()) "?q=$query" else ""}"
    // }
}
*/

/**
 * Example {FeatureName}DeepLinkHandler.
 * Replace with your actual deep link handler implementation.
 */
/*
@OwnsDeepLinks(moduleId = "{featureName}")
class {FeatureName}DeepLinkHandler @Inject constructor() : DeepLinkHandler {
    
    override val moduleId: String = "{featureName}"
    
    override fun supportedPatterns(): List<String> = listOf(
        "app://{featureName}/list",
        "app://{featureName}/detail/{id}",
        "githubusers://{featureName}/list",
        "githubusers://{featureName}/detail/{id}",
        "https://githubusers.example.com/{featureName}/list",
        "https://githubusers.example.com/{featureName}/detail/{id}"
    )
    
    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val scheme = uri.scheme ?: return null
        val path = uri.path ?: ""
        
        return when {
            is{FeatureName}ListUri(scheme, uri.host, path) -> {
                DeepLinkResult(
                    route = "{featureName}/list",
                    deepLink = uri.toString(),
                    arguments = emptyMap(),
                    clearBackStack = uri.getBooleanQueryParameter("clear_stack", false)
                )
            }
            
            is{FeatureName}DetailUri(scheme, uri.host, path) -> {
                val id = extractId(path)
                if (id != null) {
                    DeepLinkResult(
                        route = "{featureName}/detail/$id",
                        deepLink = uri.toString(),
                        arguments = mapOf("id" to id),
                        clearBackStack = uri.getBooleanQueryParameter("clear_stack", false)
                    )
                } else null
            }
            
            else -> null
        }
    }
    
    private fun is{FeatureName}ListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path == "/{featureName}/list" -> true
            scheme == "githubusers" && (host == "{featureName}" || path == "/{featureName}/list") -> true
            scheme in listOf("http", "https") && host == "githubusers.example.com" && path == "/{featureName}/list" -> true
            else -> false
        }
    
    private fun is{FeatureName}DetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/{featureName}/detail/") -> true
            scheme == "githubusers" && path.startsWith("/{featureName}/detail/") -> true
            scheme in listOf("http", "https") && host == "githubusers.example.com" && path.startsWith("/{featureName}/detail/") -> true
            else -> false
        }
    
    private fun extractId(path: String): String? {
        return when {
            path.startsWith("/{featureName}/detail/") -> {
                path.removePrefix("/{featureName}/detail/").takeIf { it.isNotEmpty() }
            }
            else -> null
        }
    }
}
*/
