package com.example.githubusers.core.networking.di

import com.example.githubusers.core.networking.http.HttpClientProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {
    @Provides
    @Singleton
    fun provideHttpClient(provider: HttpClientProvider): HttpClient = provider.createHttpClient()
}
