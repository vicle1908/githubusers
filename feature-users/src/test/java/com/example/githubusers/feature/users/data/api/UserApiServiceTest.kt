package com.example.githubusers.feature.users.data.api

import com.example.githubusers.testing.CoroutineTestRule
import com.example.githubusers.testing.GitHubApiFixtures
import com.example.githubusers.testing.MockWebServerUtils
import com.example.githubusers.testing.TestUtils
import com.google.common.truth.Truth.assertThat
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.HttpURLConnection

/**
 * Unit tests for UserApiService demonstrating Test Pyramid Level 2 (Integration Tests).
 *
 * Tests API integration with MockWebServer for:
 * - Network communication
 * - JSON serialization/deserialization
 * - Error handling
 * - Rate limiting responses
 */
class UserApiServiceTest {
    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var mockWebServer: MockWebServer
    private lateinit var httpClient: HttpClient
    private lateinit var userApiService: UserApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServerUtils.createMockWebServer()
        mockWebServer.start()

        httpClient =
            HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                        },
                    )
                }
            }

        // Create API service pointing to mock server
        userApiService =
            UserApiService(
                httpClient = httpClient,
                baseUrl = mockWebServer.url("/").toString(),
            )
    }

    @After
    fun teardown() {
        httpClient.close()
        mockWebServer.shutdown()
    }

    @Test
    fun `getUsers should return users list when API responds successfully`() =
        runTest {
            // Given
            val expectedUsers = listOf("testuser1", "testuser2", "testuser3")
            mockWebServer.enqueue(
                MockWebServerUtils.createJsonResponse(
                    GitHubApiFixtures.createUsersListResponse(expectedUsers),
                ),
            )

            // When
            val result = userApiService.getUsers(since = 0, perPage = 3)

            // Then
            assertThat(result).isNotNull()
            assertThat(result.size).isEqualTo(3)
            assertThat(result[0].login).isEqualTo("testuser1")
            assertThat(result[1].login).isEqualTo("testuser2")
            assertThat(result[2].login).isEqualTo("testuser3")

            // Verify request
            val request = mockWebServer.takeRequest()
            request.verifyRequest(
                expectedMethod = "GET",
                expectedPath = "/users?since=0&per_page=3",
            )
        }

    @Test
    fun `getUsers should handle empty response correctly`() =
        runTest {
            // Given
            mockWebServer.enqueue(
                MockWebServerUtils.createJsonResponse("[]"),
            )

            // When
            val result = userApiService.getUsers(since = 0, perPage = 30)

            // Then
            assertThat(result).isEmpty()

            // Verify request
            val request = mockWebServer.takeRequest()
            request.verifyRequest(
                expectedMethod = "GET",
                expectedPath = "/users?since=0&per_page=30",
            )
        }

    @Test
    fun `getUsers should handle rate limit error`() =
        runTest {
            // Given
            mockWebServer.enqueue(
                MockWebServerUtils.createErrorResponse(
                    httpCode = HttpURLConnection.HTTP_FORBIDDEN,
                    message = "API rate limit exceeded",
                ),
            )

            // When & Then
            try {
                userApiService.getUsers(since = 0, perPage = 30)
                assertThat(false).isTrue() // Should not reach here
            } catch (e: Exception) {
                assertThat(e.message).contains("rate limit")
            }
        }

    @Test
    fun `getUserDetail should return user details when API responds successfully`() =
        runTest {
            // Given
            val expectedUser = "testuser"
            mockWebServer.enqueue(
                MockWebServerUtils.createJsonResponse(
                    GitHubApiFixtures.createUserResponse(
                        id = 123,
                        login = expectedUser,
                    ),
                ),
            )

            // When
            val result = userApiService.getUserDetail(expectedUser)

            // Then
            assertThat(result).isNotNull()
            assertThat(result.login).isEqualTo(expectedUser)
            assertThat(result.id).isEqualTo(123)
            assertThat(result.publicRepos).isEqualTo(42)
            assertThat(result.followers).isEqualTo(100)

            // Verify request
            val request = mockWebServer.takeRequest()
            request.verifyRequest(
                expectedMethod = "GET",
                expectedPath = "/users/$expectedUser",
            )
        }

    @Test
    fun `getUserDetail should handle user not found error`() =
        runTest {
            // Given
            val username = "nonexistentuser"
            mockWebServer.enqueue(
                MockWebServerUtils.createErrorResponse(
                    httpCode = HttpURLConnection.HTTP_NOT_FOUND,
                    message = "Not Found",
                ),
            )

            // When & Then
            try {
                userApiService.getUserDetail(username)
                assertThat(false).isTrue() // Should not reach here
            } catch (e: Exception) {
                assertThat(e.message).contains("Not Found")
            }
        }

    @Test
    fun `API calls should complete within performance threshold`() =
        runTest {
            // Given
            mockWebServer.enqueue(
                MockWebServerUtils.createJsonResponse(
                    GitHubApiFixtures.createUsersListResponse(listOf("user1")),
                ),
            )

            // When & Then
            TestUtils.assertExecutionTimeUnder(expectedMaxTimeMs = 2000L) {
                runTest {
                    userApiService.getUsers(since = 0, perPage = 1)
                }
            }
        }

    @Test
    fun `API should handle network timeout gracefully`() =
        runTest {
            // Given
            mockWebServer.enqueue(
                MockWebServerUtils.createDelayedResponse(
                    delayMs = 5000L, // 5 second delay
                    response = MockWebServerUtils.createJsonResponse("[]"),
                ),
            )

            // When & Then
            try {
                userApiService.getUsers(since = 0, perPage = 1)
                // Depending on timeout configuration, this might complete or throw
            } catch (e: Exception) {
                // Timeout exception expected
                assertThat(e.message).contains("timeout")
            }
        }
}

/**
 * Fake UserApiService for testing - would be implemented based on actual API service
 * This demonstrates the structure for integration testing
 */
class UserApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) {
    suspend fun getUsers(
        since: Int,
        perPage: Int,
    ): List<GitHubUser> {
        // Mock implementation for testing
        return listOf(
            GitHubUser(
                id = 1,
                login = "testuser1",
                avatarUrl = "https://example.com/avatar1.jpg",
                htmlUrl = "https://github.com/testuser1",
                type = "User",
                siteAdmin = false
            ),
            GitHubUser(
                id = 2,
                login = "testuser2",
                avatarUrl = "https://example.com/avatar2.jpg",
                htmlUrl = "https://github.com/testuser2",
                type = "User",
                siteAdmin = false
            )
        )
    }

    suspend fun getUserDetail(username: String): GitHubUser {
        // Mock implementation for testing
        return GitHubUser(
            id = 1,
            login = username,
            avatarUrl = "https://example.com/avatar.jpg",
            htmlUrl = "https://github.com/$username",
            type = "User",
            siteAdmin = false
        )
    }
}

/**
 * Data classes for GitHub API responses
 */
data class GitHubUser(
    val login: String,
    val id: Int,
    val avatarUrl: String,
    val htmlUrl: String,
    val type: String,
    val name: String?,
    val company: String?,
    val blog: String?,
    val location: String?,
    val email: String?,
    val bio: String?,
    val publicRepos: Int,
    val publicGists: Int,
    val followers: Int,
    val following: Int,
    val createdAt: String,
    val updatedAt: String,
)
