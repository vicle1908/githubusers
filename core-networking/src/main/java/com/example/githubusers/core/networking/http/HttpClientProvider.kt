package com.example.githubusers.core.networking.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
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
            HttpClient(OkHttp) {
                // Enhanced OkHttp engine configuration for better performance
                engine {
                    config {
                        connectTimeout(30, TimeUnit.SECONDS)
                        readTimeout(30, TimeUnit.SECONDS) 
                        writeTimeout(30, TimeUnit.SECONDS)
                        callTimeout(60, TimeUnit.SECONDS)
                        
                        // Connection pooling for better performance
                        connectionPool(
                            okhttp3.ConnectionPool(
                                maxIdleConnections = 10,
                                keepAliveDuration = 5,
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
                        },
                    )
                }
                
                // Add timeout configuration for better reliability
                install(HttpTimeout) {
                    requestTimeoutMillis = 60_000    // 60 seconds for requests
                    connectTimeoutMillis = 30_000    // 30 seconds to establish connection  
                    socketTimeoutMillis = 30_000     // 30 seconds for socket reads
                }
                
                // Add retry logic for failed requests
                install(HttpRequestRetry) {
                    retryOnServerErrors(maxRetries = 3)
                    retryOnException(maxRetries = 2, retryOnTimeout = true)
                    exponentialDelay(base = 2.0, maxDelayMs = 10_000) // Max 10 second delay
                    
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
