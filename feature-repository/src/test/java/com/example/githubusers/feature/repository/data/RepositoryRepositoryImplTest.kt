package com.example.githubusers.feature.repository.data

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.room.Room
import com.example.githubusers.core.search.SearchQueryNormalizer
import com.example.githubusers.feature.repository.data.local.RepositoryDatabase
import com.example.githubusers.feature.repository.data.remote.RepositoryApiService
import com.example.githubusers.feature.repository.data.remote.RepositoryDetailDto
import com.example.githubusers.feature.repository.data.remote.RepositoryDto
import com.example.githubusers.feature.repository.data.remote.RepositoryLicenseDto
import com.example.githubusers.feature.repository.data.remote.RepositoryOwnerDto
import com.example.githubusers.feature.repository.data.remote.SearchRepositoriesResponse
import com.example.githubusers.feature.repository.data.repository.RepositoryRepositoryImpl
import com.example.githubusers.feature.repository.domain.model.RepositoryDetail
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class RepositoryRepositoryImplTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var database: RepositoryDatabase
    private lateinit var apiService: RepositoryApiService
    private lateinit var repository: RepositoryRepositoryImpl

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val context = RuntimeEnvironment.getApplication().applicationContext
        database = Room.inMemoryDatabaseBuilder(context, RepositoryDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        apiService = RepositoryApiService(createMockClient())
        repository = RepositoryRepositoryImpl(database, apiService)
    }

    @After
    fun tearDown() {
        database.close()
        Dispatchers.resetMain()
    }

    @Test
    fun remoteMediatorRefreshStoresEntitiesInDatabase() = runTest(testDispatcher) {
        val normalizedQuery = SearchQueryNormalizer.normalize("language:kotlin awesome")
        val flow = repository.observeRepositories(normalizedQuery)

        val collectJob = launch { flow.collect { return@collect } }

        advanceUntilIdle()
        collectJob.cancel()

        val pagingSource = database.repositoryDao().searchPagingSource(
            language = "kotlin",
            plainQuery = "awesome"
        )

        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = PagingConfig(pageSize = 30).pageSize,
                placeholdersEnabled = false
            )
        )

        assertTrue(loadResult is PagingSource.LoadResult.Page)
        val data = (loadResult as PagingSource.LoadResult.Page).data
        assertEquals(1, data.size)
        assertEquals("octocat/awesome-repo", data.first().fullName)
        pagingSource.invalidate()
    }

    @Test
    fun repositoryDetailRequestsAreMappedToDomain() = runTest(testDispatcher) {
        val result = repository.getRepository(owner = "octocat", name = "awesome-repo")
        assertTrue(result.isSuccess)
        val detail = result.getOrNull()
        requireNotNull(detail)
        assertDomainDetail(detail)
    }

    private fun assertDomainDetail(detail: RepositoryDetail) {
        assertEquals("awesome-repo", detail.name)
        assertEquals("main", detail.defaultBranch)
        assertEquals(listOf("android", "compose"), detail.topics)
    }

    private fun createMockClient(): HttpClient {
        val searchPayload = SearchRepositoriesResponse(
            totalCount = 1,
            incompleteResults = false,
            items = listOf(baseRepositoryDto())
        )

        val detailPayload = baseRepositoryDetailDto()

        return HttpClient(MockEngine { request -> handleRequest(request, searchPayload, detailPayload) }) {
            install(ContentNegotiation) {
                json(this@RepositoryRepositoryImplTest.json)
            }
        }
    }

    private fun MockRequestHandleScope.handleRequest(
        request: HttpRequestData,
        searchPayload: SearchRepositoriesResponse,
        detailPayload: RepositoryDetailDto
    ) = when (request.url.encodedPath) {
        "/search/repositories" -> respond(
            content = json.encodeToString(searchPayload),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        )

        "/repos/octocat/awesome-repo" -> respond(
            content = json.encodeToString(detailPayload),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        )

        else -> respond(
            content = "{}",
            status = HttpStatusCode.NotFound,
            headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        )
    }

    private fun baseRepositoryDto(): RepositoryDto = RepositoryDto(
        id = 1L,
        name = "awesome-repo",
        fullName = "octocat/awesome-repo",
        owner = RepositoryOwnerDto(
            login = "octocat",
            id = 42L,
            avatarUrl = "https://example.com/octocat.png",
            htmlUrl = "https://github.com/octocat"
        ),
        description = "Awesome repo",
        htmlUrl = "https://github.com/octocat/awesome-repo",
        stargazersCount = 9001,
        watchersCount = 9001,
        language = "Kotlin",
        forksCount = 123,
        openIssuesCount = 7,
        license = RepositoryLicenseDto(
            key = "mit",
            name = "MIT License",
            spdxId = "MIT",
            url = null,
            nodeId = "MDc6TGljZW5zZW1pdA=="
        )
    )

    private fun baseRepositoryDetailDto(): RepositoryDetailDto = RepositoryDetailDto(
        id = 1L,
        name = "awesome-repo",
        fullName = "octocat/awesome-repo",
        owner = RepositoryOwnerDto(
            login = "octocat",
            id = 42L,
            avatarUrl = "https://example.com/octocat.png",
            htmlUrl = "https://github.com/octocat"
        ),
        description = "Awesome repo",
        htmlUrl = "https://github.com/octocat/awesome-repo",
        stargazersCount = 9001,
        watchersCount = 9001,
        language = "Kotlin",
        forksCount = 123,
        openIssuesCount = 7,
        license = RepositoryLicenseDto(
            key = "mit",
            name = "MIT License",
            spdxId = "MIT",
            url = null,
            nodeId = "MDc6TGljZW5zZW1pdA=="
        ),
        defaultBranch = "main",
        createdAt = "2025-01-01T00:00:00Z",
        updatedAt = "2025-01-02T00:00:00Z",
        pushedAt = "2025-01-03T00:00:00Z",
        size = 2048,
        sshUrl = "git@github.com:octocat/awesome-repo.git",
        cloneUrl = "https://github.com/octocat/awesome-repo.git",
        homepage = "https://octocat.dev",
        topics = listOf("android", "compose")
    )
}
