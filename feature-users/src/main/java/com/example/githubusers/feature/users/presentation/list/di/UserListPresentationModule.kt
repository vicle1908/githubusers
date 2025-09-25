package com.example.githubusers.feature.users.list.presentation.di

import com.example.githubusers.feature.users.list.presentation.analytics.DefaultUserListAnalytics
import com.example.githubusers.feature.users.list.presentation.analytics.UserListAnalytics
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
