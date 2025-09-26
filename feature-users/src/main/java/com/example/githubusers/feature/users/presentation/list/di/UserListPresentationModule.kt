package com.example.githubusers.feature.users.presentation.list.di

import com.example.githubusers.feature.users.presentation.list.analytics.DefaultUserListAnalytics
import com.example.githubusers.feature.users.presentation.list.analytics.UserListAnalytics
import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface UserListPresentationModule {

    @Binds
    @Reusable
    fun bindUserListAnalytics(impl: DefaultUserListAnalytics): UserListAnalytics
}
