package com.example.githubusers.core.data.network

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ktor HTTP client configuration for the application.
 * Uses pure Ktor with Kotlin serialization, no Retrofit needed.
 */
@Singleton
class KtorClient @Inject constructor() {
    
    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    val client = HttpClient(OkHttp) {
        // Install JSON content negotiation
        install(ContentNegotiation) {
            json(json)
        }
        
        // Install logging for debugging
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
            sanitizeHeader { header -> 
                header == HttpHeaders.Authorization 
            }
        }
        
        
        // Default request configuration
        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Accept, ContentType.Application.Json)
        }
        
        // Configure timeouts
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 30_000
        }
        
        // Retry configuration
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 3)
            exponentialDelay()
        }
        
        // Optional: Add authentication if needed
        install(Auth) {
            bearer {
                loadTokens {
                    // Load tokens from secure storage
                    BearerTokens(
                        accessToken = getAccessToken(),
                        refreshToken = getRefreshToken()
                    )
                }
                
                refreshTokens {
                    // Refresh token logic
                    val newTokens = refreshTokens()
                    BearerTokens(
                        accessToken = newTokens.accessToken,
                        refreshToken = newTokens.refreshToken
                    )
                }
            }
        }
        
        // OkHttp specific configuration
        engine {
            config {
                followRedirects(true)
            }
        }
    }
    
    // Mock token functions - replace with actual implementation
    private fun getAccessToken(): String = ""
    private fun getRefreshToken(): String = ""
    private suspend fun refreshTokens(): BearerTokens = BearerTokens("", "")
    
    /**
     * Clean up resources
     */
    fun close() {
        client.close()
    }
}
