package com.example.githubusers.di

import com.example.githubusers.BuildConfig
import com.example.githubusers.navigation.api.Navigation3Controller
import com.example.githubusers.navigation.impl.BackStackStore
import com.example.githubusers.navigation.impl.DestinationResolver
import com.example.githubusers.navigation.impl.Navigation3ControllerImpl
import com.example.githubusers.navigation.impl.NavigationTelemetry
import com.example.githubusers.navigation.impl.NoOpNavigationMetrics
import com.example.githubusers.navigation.impl.PersistenceConfig
import com.example.githubusers.navigation.impl.guard.ReadOnlyNavGate
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object AppNavigationRetainedModule {
    @Provides
    @ActivityRetainedScoped
    fun provideNavigation3Controller(
        destinationResolver: DestinationResolver,
        backStackStore: BackStackStore,
        navGate: ReadOnlyNavGate,
        telemetry: NavigationTelemetry,
    ): Navigation3Controller {
        val config =
            PersistenceConfig(
                enabled = BuildConfig.NAV3_PERSISTENCE_ENABLED,
                maxEntries = 5,
                maxTotalEntriesLength = 20000,
                ttlMillis = 7L * 24L * 60L * 60L * 1000L,
                schemaVersion = 1,
                navGraphVersion = 1,
                appVersionProvider = { BuildConfig.VERSION_NAME },
            )
        return Navigation3ControllerImpl(
            destinationResolver = destinationResolver,
            backStackStore = backStackStore,
            persistenceConfig = config,
            metrics = NoOpNavigationMetrics,
            navGate = navGate,
            telemetry = telemetry,
        )
    }
}
