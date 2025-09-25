package com.example.githubusers.feature.users.navigation.di

import com.example.githubusers.feature.users.navigation.DefaultUserDetailNavigatorFactory
import com.example.githubusers.feature.users.navigation.UserDetailNavigatorFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface UsersNavigatorModule {

    @Binds
    @Singleton
    fun bindUserDetailNavigatorFactory(impl: DefaultUserDetailNavigatorFactory): UserDetailNavigatorFactory
}
