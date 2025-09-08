package com.example.githubusers.core.networking.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides configured HTTP client for the application.
 * This is shared infrastructure that features can use.
 */
@Singleton
class HttpClientProvider
    @Inject
    constructor() {
        /**
         * Creates a configured HTTP client with common settings.
         * Features can use this as a base and add feature-specific configuration.
         */
        fun createHttpClient(): HttpClient =
            HttpClient(Android) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                            encodeDefaults = true
                        },
                    )
                }
                install(Logging) {
                    // Configure logging level based on build type
                    // This will be handled by the feature modules
                }
            }
    }
