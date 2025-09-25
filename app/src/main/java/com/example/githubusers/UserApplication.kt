package com.example.githubusers

import android.app.Application
import com.example.githubusers.core.ui.performance.PerformanceMonitor
import com.google.firebase.FirebaseApp
import com.google.firebase.perf.FirebasePerformance
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidApp
class UserApplication : Application() {

    @Inject
    lateinit var performanceMonitor: PerformanceMonitor

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG && Timber.forest().none { it is DebugTree }) {
            Timber.plant(DebugTree())
        }

        val firebaseInitResult = runCatching { FirebaseApp.initializeApp(this) }

        val firebasePerfEnabled = BuildConfig.FIREBASE_PERF_ENABLED
        val performanceResult = runCatching {
            FirebasePerformance.getInstance().apply {
                isPerformanceCollectionEnabled = firebasePerfEnabled
            }
        }

        performanceMonitor.setEnabled(BuildConfig.DEBUG)

        when {
            firebaseInitResult.isFailure -> Timber.tag("UserApplication").w(
                firebaseInitResult.exceptionOrNull(),
                "Firebase initialization failed; performance monitoring collection flag may be stale"
            )
            performanceResult.isFailure -> Timber.tag("UserApplication").w(
                performanceResult.exceptionOrNull(),
                "Firebase Performance instance unavailable"
            )
            else -> Timber.tag("UserApplication").d(
                "Firebase Performance Monitoring initialised (collectionEnabled=$firebasePerfEnabled, devMonitorEnabled=${BuildConfig.DEBUG})"
            )
        }
    }
}
