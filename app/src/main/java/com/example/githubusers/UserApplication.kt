package com.example.githubusers

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class UserApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Firebase initialization temporarily disabled for authentication testing
        // FirebaseApp.initializeApp(this)
        // Firebase Performance Monitoring temporarily disabled
        // val firebasePerformance = FirebasePerformance.getInstance()
        // val isEnabled = BuildConfig.FIREBASE_PERF_ENABLED
        // firebasePerformance.isPerformanceCollectionEnabled = isEnabled
        Timber.tag("UserApplication").d("Application started - Firebase Performance Monitoring disabled for testing")
        // TODO: Re-enable Firebase Performance Monitoring after authentication testing
    }
}
