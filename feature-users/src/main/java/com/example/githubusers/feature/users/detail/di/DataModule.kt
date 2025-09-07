package com.example.githubusers.feature.users.detail.di

import android.content.Context
import androidx.room.Room
import com.example.githubusers.feature.users.detail.data.local.UserDetailDatabase
import com.example.githubusers.feature.users.detail.data.local.dao.RepositoryDao
import com.example.githubusers.feature.users.detail.data.local.dao.UserDetailDao
import com.example.githubusers.feature.users.detail.data.remote.api.UserDetailRemoteDataSource
import com.example.githubusers.feature.users.detail.data.remote.api.UserDetailRemoteDataSourceImpl
import com.example.githubusers.feature.users.detail.data.repository.UserDetailRepositoryImpl
import com.example.githubusers.feature.users.detail.domain.repository.UserDetailRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserDetailHttpClient

/**
 * Hilt module for data layer dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindUserDetailRepository(impl: UserDetailRepositoryImpl): UserDetailRepository

    @Binds
    abstract fun bindUserDetailRemoteDataSource(impl: UserDetailRemoteDataSourceImpl): UserDetailRemoteDataSource

    companion object {
        @Provides
        @Singleton
        @UserDetailHttpClient
        fun provideHttpClient(): HttpClient =
            HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                        },
                    )
                }

                install(Logging) {
                    level = LogLevel.INFO
                }

                defaultRequest {
                    url("https://api.github.com/")
                    contentType(ContentType.Application.Json)
                }
            }

        @Provides
        @Singleton
        fun provideUserDetailDatabase(
            @ApplicationContext context: Context,
        ): UserDetailDatabase =
            Room
                .databaseBuilder(
                    context,
                    UserDetailDatabase::class.java,
                    UserDetailDatabase.DATABASE_NAME,
                ).build()

        @Provides
        @Singleton
        fun provideUserDetailDao(database: UserDetailDatabase): UserDetailDao = database.userDetailDao()

        @Provides
        @Singleton
        fun provideRepositoryDao(database: UserDetailDatabase): RepositoryDao = database.repositoryDao()
    }
}
