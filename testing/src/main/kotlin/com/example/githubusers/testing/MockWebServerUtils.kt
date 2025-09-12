package com.example.githubusers.testing

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.util.concurrent.TimeUnit

/**
 * MockWebServer utilities for API testing.
 * 
 * Provides:
 * - Easy setup and teardown of mock servers
 * - Common GitHub API response fixtures
 * - Request verification helpers
 * - Error simulation utilities
 */
object MockWebServerUtils {
    
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    
    /**
     * Creates a MockWebServer with common configuration
     */
    fun createMockWebServer(): MockWebServer {
        return MockWebServer().apply {
            // Common configuration
        }
    }
    
    /**
     * Creates a successful JSON response
     */
    fun createJsonResponse(
        body: Any,
        httpCode: Int = 200,
        headers: Map<String, String> = emptyMap()
    ): MockResponse {
        val jsonBody = when (body) {
            is String -> body
            else -> json.encodeToString(body)
        }
        
        return MockResponse()
            .setResponseCode(httpCode)
            .setHeader("Content-Type", "application/json")
            .setBody(jsonBody)
            .also { response ->
                headers.forEach { (key, value) ->
                    response.setHeader(key, value)
                }
            }
    }
    
    /**
     * Creates an error response
     */
    fun createErrorResponse(
        httpCode: Int,
        message: String = "Error occurred",
        headers: Map<String, String> = emptyMap()
    ): MockResponse {
        val errorBody = """
            {
                "message": "$message",
                "documentation_url": "https://docs.github.com/rest"
            }
        """.trimIndent()
        
        return MockResponse()
            .setResponseCode(httpCode)
            .setHeader("Content-Type", "application/json")
            .setBody(errorBody)
            .also { response ->
                headers.forEach { (key, value) ->
                    response.setHeader(key, value)
                }
            }
    }
    
    /**
     * Creates a delayed response for timeout testing
     */
    fun createDelayedResponse(
        delayMs: Long,
        response: MockResponse = createJsonResponse("{}")
    ): MockResponse {
        return response.setBodyDelay(delayMs, TimeUnit.MILLISECONDS)
    }
    
    /**
     * Verifies that a request was made with expected parameters
     */
    fun RecordedRequest.verifyRequest(
        expectedMethod: String,
        expectedPath: String,
        expectedHeaders: Map<String, String> = emptyMap()
    ) {
        assert(method == expectedMethod) { 
            "Expected method $expectedMethod, but was $method" 
        }
        assert(path == expectedPath) { 
            "Expected path $expectedPath, but was $path" 
        }
        expectedHeaders.forEach { (key, value) ->
            assert(getHeader(key) == value) { 
                "Expected header $key=$value, but was ${getHeader(key)}" 
            }
        }
    }
    
    /**
     * Verifies request body contains expected JSON
     */
    fun RecordedRequest.verifyJsonBody(expectedBody: Any) {
        val actualBodyString = body.readUtf8()
        val expectedBodyString = when (expectedBody) {
            is String -> expectedBody
            else -> json.encodeToString(expectedBody)
        }
        
        // Parse and compare as JSON to ignore formatting differences
        val actualJson = json.parseToJsonElement(actualBodyString)
        val expectedJson = json.parseToJsonElement(expectedBodyString)
        
        assert(actualJson == expectedJson) {
            "Expected JSON body:\n$expectedBodyString\nBut was:\n$actualBodyString"
        }
    }
}

/**
 * GitHub API response fixtures for testing
 */
object GitHubApiFixtures {
    
    /**
     * Creates a mock GitHub user response
     */
    fun createUserResponse(
        id: Int = 1,
        login: String = "testuser",
        avatarUrl: String = "https://github.com/images/error/testuser_happy.gif",
        url: String = "https://api.github.com/users/$login",
        type: String = "User"
    ) = """
        {
            "login": "$login",
            "id": $id,
            "avatar_url": "$avatarUrl",
            "url": "$url",
            "html_url": "https://github.com/$login",
            "type": "$type",
            "site_admin": false,
            "name": "Test User",
            "company": "Test Company",
            "blog": "https://testuser.example.com",
            "location": "Test Location",
            "email": null,
            "hireable": null,
            "bio": "Test bio",
            "public_repos": 42,
            "public_gists": 1,
            "followers": 100,
            "following": 50,
            "created_at": "2008-01-14T04:33:35Z",
            "updated_at": "2023-12-01T10:00:00Z"
        }
    """.trimIndent()
    
    /**
     * Creates a mock GitHub users list response
     */
    fun createUsersListResponse(
        users: List<String> = listOf("testuser1", "testuser2", "testuser3"),
        since: Int = 0
    ): String {
        val usersList = users.mapIndexed { index, login ->
            """
            {
                "login": "$login",
                "id": ${since + index + 1},
                "avatar_url": "https://github.com/images/error/${login}_happy.gif",
                "url": "https://api.github.com/users/$login",
                "html_url": "https://github.com/$login",
                "type": "User",
                "site_admin": false
            }
            """.trimIndent()
        }.joinToString(",\n")
        
        return "[\n$usersList\n]"
    }
    
    /**
     * Creates a mock GitHub search users response
     */
    fun createSearchUsersResponse(
        users: List<String> = listOf("searchuser1", "searchuser2"),
        totalCount: Int = 2,
        incompleteResults: Boolean = false
    ): String {
        val usersList = users.mapIndexed { index, login ->
            """
            {
                "login": "$login",
                "id": ${index + 1000},
                "avatar_url": "https://github.com/images/error/${login}_happy.gif",
                "url": "https://api.github.com/users/$login",
                "html_url": "https://github.com/$login",
                "type": "User",
                "score": ${100 - index * 10}
            }
            """.trimIndent()
        }.joinToString(",\n")
        
        return """
            {
                "total_count": $totalCount,
                "incomplete_results": $incompleteResults,
                "items": [
                    $usersList
                ]
            }
        """.trimIndent()
    }
    
    /**
     * Creates a mock rate limit exceeded response
     */
    fun createRateLimitResponse() = """
        {
            "message": "API rate limit exceeded for user ID 1.",
            "documentation_url": "https://docs.github.com/rest/overview/resources-in-the-rest-api#rate-limiting"
        }
    """.trimIndent()
    
    /**
     * Creates a mock not found response
     */
    fun createNotFoundResponse() = """
        {
            "message": "Not Found",
            "documentation_url": "https://docs.github.com/rest"
        }
    """.trimIndent()
}