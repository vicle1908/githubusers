package com.example.githubusers.di

import android.content.Context
import androidx.room.Room
import com.example.githubusers.data.local.RemoteKeyDao
import com.example.githubusers.data.local.UserDao
import com.example.githubusers.data.local.UserDatabase
import com.example.githubusers.data.remote.UserApiService
import com.example.githubusers.data.repository.UserRepositoryImpl
import com.example.githubusers.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideHttpClient(okHttpClient: OkHttpClient): HttpClient =
        HttpClient(OkHttp) {
            engine {
                preconfigured = okHttpClient
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                        explicitNulls = true
                    },
                )
            }
            install(Logging) {
                level = LogLevel.ALL
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 30_000
                socketTimeoutMillis = 30_000
            }
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 5)
                exponentialDelay()
                retryIf { request, response ->
                    // Retry on rate limit (status code 429)
                    response.status == HttpStatusCode.TooManyRequests
                }
            }
        }

    @Provides
    @Singleton
    fun provideUserApiService(client: HttpClient): UserApiService = UserApiService(client)

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext appContext: Context,
    ): UserDatabase =
        Room
            .databaseBuilder(
                appContext,
                UserDatabase::class.java,
                "github_users.db",
            ).fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideUserDao(database: UserDatabase): UserDao = database.userDao()

    @Provides
    @Singleton
    fun provideRemoteKeyDao(database: UserDatabase): RemoteKeyDao = database.remoteKeyDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class UserRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository
}
