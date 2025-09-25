package com.example.githubusers.feature.search.di

import com.example.githubusers.core.search.domain.SearchRepository
import com.example.githubusers.feature.search.data.repository.SearchRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger module for search feature dependency injection
 */
@Module
@InstallIn(SingletonComponent::class)
object SearchModule {
    /**
     * Provides SearchRepository via constructor injection.
     */
    @Provides
    @Singleton
    fun provideSearchRepository(searchRepositoryImpl: SearchRepositoryImpl): SearchRepository = searchRepositoryImpl
}
