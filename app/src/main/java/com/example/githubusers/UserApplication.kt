package com.example.githubusers

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.perf.FirebasePerformance
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class UserApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Initialize Firebase Performance Monitoring
        val firebasePerformance = FirebasePerformance.getInstance()
        
        // Enable Firebase Performance Monitoring based on build type
        val isEnabled = BuildConfig.FIREBASE_PERF_ENABLED
        firebasePerformance.isPerformanceCollectionEnabled = isEnabled
        
        Timber.tag("FirebasePerf").d("Firebase Performance Monitoring enabled: $isEnabled")
        
        // Firebase Performance Monitoring automatically tracks:
        // - App startup time
        // - Screen rendering
        // - Network requests
        // - Custom traces
    }
}
