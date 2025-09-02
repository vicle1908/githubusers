package com.example.githubusers.core.data.di

import com.example.githubusers.core.data.api.GitHubApiService
import com.example.githubusers.core.data.network.KtorClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideKtorClient(): KtorClient = KtorClient()

    @Provides
    @Singleton
    fun provideHttpClient(ktorClient: KtorClient): HttpClient = ktorClient.client

    @Provides
    @Singleton
    fun provideGitHubApiService(ktorClient: KtorClient): GitHubApiService = GitHubApiService(ktorClient.client)
}
