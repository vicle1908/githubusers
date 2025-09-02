package com.example.githubusers.core.ui.di

import android.content.Context
import coil.ImageLoader
import coil.network.ktor3.Ktor3NetworkFetcher
import com.example.githubusers.core.data.network.KtorClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageModule {

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        ktorClient: KtorClient
    ): ImageLoader =
        ImageLoader.Builder(context)
            .components {
                add(Ktor3NetworkFetcher.Factory(ktorClient.client))
            }
            .build()
}
