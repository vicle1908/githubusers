package com.example.githubusers.di

import com.example.githubusers.core.analytics.AnalyticsFacade
import com.example.githubusers.core.analytics.FakeAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @Singleton
    fun provideAnalyticsFacade(): AnalyticsFacade = FakeAnalytics()
}
