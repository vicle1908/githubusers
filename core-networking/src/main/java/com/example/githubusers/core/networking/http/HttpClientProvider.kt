package com.example.githubusers.core.networking.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json

/**
 * Provides configured HTTP client for the application.
 * This is shared infrastructure that features can use.
 */
@Singleton
class HttpClientProvider
@Inject
constructor() {
    companion object {
        private const val CONNECT_TIMEOUT_SECONDS = 30L
        private const val READ_TIMEOUT_SECONDS = 30L
        private const val WRITE_TIMEOUT_SECONDS = 30L
        private const val CALL_TIMEOUT_SECONDS = 60L
        private const val CONNECTION_POOL_MAX_IDLE = 10
        private const val CONNECTION_KEEP_ALIVE_MINUTES = 5L
        private const val REQUEST_TIMEOUT_MS = 60_000L
        private const val CONNECT_TIMEOUT_MS = 30_000L
        private const val SOCKET_TIMEOUT_MS = 30_000L
        private const val RETRY_SERVER_MAX = 3
        private const val RETRY_EXCEPTION_MAX = 2
        private const val EXP_BACKOFF_BASE = 2.0
        private const val EXP_MAX_DELAY_MS = 10_000L
    }

    /**
     * Creates a configured HTTP client with common settings.
     * Features can use this as a base and add feature-specific configuration.
     */
    fun createHttpClient(): HttpClient = HttpClient(OkHttp) {
        // Enhanced OkHttp engine configuration for better performance
        engine {
            config {
                connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)

                // Connection pooling for better performance
                connectionPool(
                    okhttp3.ConnectionPool(
                        maxIdleConnections = CONNECTION_POOL_MAX_IDLE,
                        keepAliveDuration = CONNECTION_KEEP_ALIVE_MINUTES,
                        timeUnit = TimeUnit.MINUTES
                    )
                )

                // Enable HTTP/2 and connection compression
                retryOnConnectionFailure(true)
            }
        }

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                }
            )
        }

        // Add timeout configuration for better reliability
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MS // 60 seconds for requests
            connectTimeoutMillis = CONNECT_TIMEOUT_MS // 30 seconds to establish connection
            socketTimeoutMillis = SOCKET_TIMEOUT_MS // 30 seconds for socket reads
        }

        // Add retry logic for failed requests
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = RETRY_SERVER_MAX)
            retryOnException(maxRetries = RETRY_EXCEPTION_MAX, retryOnTimeout = true)
            exponentialDelay(base = EXP_BACKOFF_BASE, maxDelayMs = EXP_MAX_DELAY_MS) // Max 10 second delay

            modifyRequest { request ->
                // Add retry headers for better debugging
                request.headers.append("X-Retry-Count", retryCount.toString())
            }
        }

        install(Logging) {
            // Configure logging level based on build type
            // This will be handled by the feature modules
        }
    }
}
