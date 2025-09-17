package com.example.githubusers.feature.search.data.util

import com.example.githubusers.core.search.domain.SearchError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.HttpStatusCode
import java.io.IOException
import java.net.SocketTimeoutException

/** Map a Throwable from data/network layers to a domain-level SearchError. */
fun Throwable.toSearchError(): SearchError = when (this) {
    is HttpRequestTimeoutException, is SocketTimeoutException -> SearchError.Timeout
    is IOException -> SearchError.Network
    is ClientRequestException -> {
        val code = response.status.value
        when (response.status) {
            HttpStatusCode.TooManyRequests -> SearchError.RateLimited
            HttpStatusCode.Forbidden -> SearchError.RateLimited // GitHub often uses 403 for rate limit
            else -> SearchError.Client(code, message)
        }
    }
    is ServerResponseException -> SearchError.Server(response.status.value, message)
    is RedirectResponseException -> SearchError.Client(response.status.value, message)
    else -> SearchError.Unknown(this)
}