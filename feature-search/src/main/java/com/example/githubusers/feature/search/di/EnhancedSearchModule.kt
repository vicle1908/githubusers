package com.example.githubusers.feature.search.di

import com.example.githubusers.feature.search.data.repository.EnhancedSearchRepositoryImpl
import com.example.githubusers.feature.search.domain.repository.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger module for enhanced search feature dependency injection
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class EnhancedSearchModule {
    /**
     * Binds EnhancedSearchRepositoryImpl to SearchRepository interface
     */
    @Binds
    @Singleton
    abstract fun bindSearchRepository(searchRepositoryImpl: EnhancedSearchRepositoryImpl): SearchRepository
}
