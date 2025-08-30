package com.example.githubusers.navigation.impl.di

import android.content.Context
import com.example.githubusers.navigation.impl.BackStackStore
import com.example.githubusers.navigation.impl.DeepLinkOwnershipSource
import com.example.githubusers.navigation.impl.NavigationMetrics
import com.example.githubusers.navigation.impl.NavigationTelemetry
import com.example.githubusers.navigation.impl.NoOpDeepLinkOwnershipSource
import com.example.githubusers.navigation.impl.NoOpNavigationMetrics
import com.example.githubusers.navigation.impl.NoOpNavigationTelemetry
import com.example.githubusers.navigation.impl.PersistenceConfig
import com.example.githubusers.navigation.impl.SharedPrefsBackStackStore
import com.example.githubusers.navigation.impl.guard.ReadOnlyNavGate
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Default providers for Navigation-impl module. These are safe defaults and can be
 * overridden by app modules if needed.
 */
@Module
@InstallIn(SingletonComponent::class)
object NavigationImplModule {
    @Provides
    @Singleton
    fun providePersistenceConfig(): PersistenceConfig = PersistenceConfig()

    @Provides
    @Singleton
    fun provideNavigationMetrics(): NavigationMetrics = NoOpNavigationMetrics

    @Provides
    @Singleton
    fun provideNavigationTelemetry(): NavigationTelemetry = NoOpNavigationTelemetry
    // To enable logging by default, switch to: LoggingNavigationTelemetry()

    @Provides
    @Singleton
    fun provideReadOnlyNavGate(): ReadOnlyNavGate = ReadOnlyNavGate(enabled = false)

    // Qualified default BackStackStore to avoid collisions with app-level bindings.
    @Provides
    @Singleton
    @DefaultNavBackStackStore
    fun provideDefaultBackStackStore(
        @ApplicationContext context: Context,
    ): BackStackStore = SharedPrefsBackStackStore(context)

    @Provides
    @Singleton
    fun provideDeepLinkOwnershipSource(): DeepLinkOwnershipSource =
        try {
            val loader =
                java.util.ServiceLoader.load(
                    com.example.githubusers.navigation.generated.DeepLinkOwnersProvider::class.java,
                )
            val aggregated = mutableMapOf<String, String>()
            loader.forEach { provider -> aggregated.putAll(provider.owners()) }
            if (aggregated.isEmpty()) {
                NoOpDeepLinkOwnershipSource
            } else {
                object : DeepLinkOwnershipSource {
                    override val owners: Map<String, String> = aggregated
                }
            }
        } catch (t: Throwable) {
            NoOpDeepLinkOwnershipSource
        }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultNavBackStackStore
