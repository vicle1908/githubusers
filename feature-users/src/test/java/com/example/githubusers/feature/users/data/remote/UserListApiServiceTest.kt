package com.example.githubusers.feature.users.data.remote

import com.example.githubusers.core.ui.performance.PerformanceMonitor
import com.example.githubusers.testing.CoroutineTestRule
import com.example.githubusers.testing.GitHubApiFixtures
import com.example.githubusers.testing.MockWebServerUtils
import com.google.common.truth.Truth.assertThat
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import java.net.HttpURLConnection
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserListApiServiceTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var mockWebServer: MockWebServer
    private lateinit var httpClient: HttpClient
    private lateinit var service: UserListApiService
    private val performanceMonitor = PerformanceMonitor().apply { setEnabled(false) }

    @Before
    fun setUp() {
        mockWebServer = MockWebServerUtils.createMockWebServer()
        mockWebServer.start()

        httpClient =
            HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }

        val baseUrl = mockWebServer.url("/").toString().trimEnd('/')
        service = UserListApiService(httpClient, performanceMonitor, baseUrl = baseUrl)
    }

    @After
    fun tearDown() {
        httpClient.close()
        mockWebServer.shutdown()
    }

    @Test
    fun `getUsers returns users on success`() = runTest {
        val expectedUsers = listOf("mojombo", "defunkt", "pjhyett")
        mockWebServer.enqueue(
            MockWebServerUtils.createJsonResponse(
                GitHubApiFixtures.createUsersListResponse(expectedUsers)
            )
        )

        val result = service.getUsers(since = 0, perPage = 3)

        assertThat(result.isSuccess).isTrue()
        val users = result.getOrThrow()
        assertThat(users).hasSize(3)
        assertThat(users.map { it.login }).containsExactlyElementsIn(expectedUsers)

        val request = mockWebServer.takeRequest()
        assertThat(request.method).isEqualTo("GET")
        assertThat(request.requestUrl?.encodedPath).isEqualTo("/users")
        assertThat(request.requestUrl?.query).isEqualTo("since=0&per_page=3")
    }

    @Test
    fun `getUsers returns empty list`() = runTest {
        mockWebServer.enqueue(MockWebServerUtils.createJsonResponse("[]"))

        val result = service.getUsers(since = null, perPage = 10)

        assertThat(result.isSuccess).isTrue()
        val users = result.getOrThrow()
        assertThat(users).isEmpty()

        val request = mockWebServer.takeRequest()
        assertThat(request.requestUrl?.query).isEqualTo("per_page=10")
    }

    @Test
    fun `getUsers returns failure on HTTP error`() = runTest {
        mockWebServer.enqueue(
            MockWebServerUtils.createErrorResponse(HttpURLConnection.HTTP_FORBIDDEN, "rate limit")
        )

        val result = service.getUsers(since = 100, perPage = 5)

        assertThat(result.isFailure).isTrue()
        val exception = result.exceptionOrNull()
        assertThat(exception).isNotNull()
        assertThat(exception?.message).contains("Failed to fetch users")
    }
}
