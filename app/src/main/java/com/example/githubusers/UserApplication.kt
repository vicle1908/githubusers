package com.example.githubusers

import android.app.Application
import com.example.githubusers.performance.StartupPerformanceTracker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class UserApplication : Application() {

    @Inject
    lateinit var startupPerformanceTracker: StartupPerformanceTracker

    override fun onCreate() {
        super.onCreate()
        startupPerformanceTracker.markProcessStart()
        startupPerformanceTracker.markApplicationCreated()
    }
}
