package com.example.githubusers.core.networking.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * Base API service that provides common HTTP operations.
 * Features can extend this or use it as a reference for their own API services.
 */
abstract class BaseApiService(
    protected val httpClient: HttpClient,
) {
    /**
     * Generic GET request with query parameters
     */
    protected suspend inline fun <reified T> get(
        url: String,
        parameters: Map<String, Any> = emptyMap(),
    ): T =
        httpClient
            .get(url) {
                parameters.forEach { (key, value) ->
                    parameter(key, value)
                }
            }.body()

    /**
     * Generic GET request for paginated results
     */
    protected suspend inline fun <reified T> getPaginated(
        url: String,
        page: Int = 1,
        perPage: Int = 30,
        additionalParameters: Map<String, Any> = emptyMap(),
    ): T {
        val allParameters = additionalParameters.toMutableMap()
        allParameters["page"] = page
        allParameters["per_page"] = perPage

        return get(url, allParameters)
    }
}
