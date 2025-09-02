package com.example.githubusers.core.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simple Ktor HTTP client configuration for the application.
 */
@Singleton
class KtorClient
    @Inject
    constructor() {
        private val json =
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                coerceInputValues = true
            }

        val client =
            HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(json)
                }
            }

        fun close() {
            client.close()
        }
    }
