package com.example.githubusers.feature.repository.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import javax.inject.Inject
import timber.log.Timber

class RepositoryApiService @Inject constructor(private val client: HttpClient) {
    suspend fun searchRepositories(query: String, page: Int, perPage: Int): Result<SearchRepositoriesResponse> =
        runCatching {
            val response = client.get("https://api.github.com/search/repositories") {
                parameter("q", query)
                parameter("page", page)
                parameter("per_page", perPage)
                parameter("sort", "stars")
                parameter("order", "desc")
            }

            if (response.status != HttpStatusCode.OK) {
                val body = response.body<String>()
                Timber.tag(TAG).w(
                    "Search request failed: status=%s, body=%s",
                    response.status,
                    body
                )
                error("Failed to fetch repositories (" + response.status + ")")
            }

            response.body<SearchRepositoriesResponse>()
        }

    suspend fun getRepository(owner: String, name: String): Result<RepositoryDetailDto> = runCatching {
        val response = client.get("https://api.github.com/repos/$owner/$name") {
            parameter("per_page", 1)
        }

        if (response.status != HttpStatusCode.OK) {
            val body = response.body<String>()
            Timber.tag(TAG).w(
                "Repository detail request failed: status=%s, body=%s",
                response.status,
                body
            )
            error("Failed to fetch repository details (" + response.status + ")")
        }

        response.body<RepositoryDetailDto>()
    }

    companion object {
        private const val TAG = "RepositoryApiService"
    }
}
