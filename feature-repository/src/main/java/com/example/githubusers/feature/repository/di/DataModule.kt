package com.example.githubusers.feature.repository.di

import android.content.Context
import androidx.room.Room
import com.example.githubusers.feature.repository.data.local.RepositoryDao
import com.example.githubusers.feature.repository.data.local.RepositoryDatabase
import com.example.githubusers.feature.repository.data.remote.RepositoryApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideRepositoryDatabase(@ApplicationContext context: Context): RepositoryDatabase = Room
        .databaseBuilder(
            context,
            RepositoryDatabase::class.java,
            RepositoryDatabase.DATABASE_NAME
        ).build()

    @Provides
    fun provideRepositoryDao(database: RepositoryDatabase): RepositoryDao = database.repositoryDao()

    @Provides
    @Singleton
    fun provideRepositoryApiService(client: HttpClient): RepositoryApiService = RepositoryApiService(client)
}
