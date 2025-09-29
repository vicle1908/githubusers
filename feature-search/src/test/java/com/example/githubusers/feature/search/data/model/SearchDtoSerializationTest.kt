package com.example.githubusers.feature.search.data.model

import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test

class SearchDtoSerializationTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GitHubUser deserializes avatar_url to avatarUrl`() {
        val raw = """
            {"id": 1, "login": "octocat", "avatar_url": "https://example.com/a.png", "type": "User", "score": 1.0}
        """.trimIndent()
        val user = json.decodeFromString<GitHubUser>(raw)
        assertEquals(1L, user.id)
        assertEquals("octocat", user.login)
        assertEquals("https://example.com/a.png", user.avatarUrl)
        assertEquals("User", user.type)
        assertTrue(user.score != null)
    }

    @Test
    fun `GitHubUserDetail deserializes snake case fields`() {
        val raw = """
            {
              "id": 2,
              "login": "hub",
              "name": "Hub",
              "avatar_url": "https://example.com/h.png",
              "bio": null,
              "location": "SF",
              "company": null,
              "public_repos": 7,
              "followers": 10,
              "following": 1,
              "created_at": "2020-01-01T00:00:00Z",
              "updated_at": "2025-01-01T00:00:00Z"
            }
        """.trimIndent()
        val detail = json.decodeFromString<GitHubUserDetail>(raw)
        assertEquals(2L, detail.id)
        assertEquals("Hub", detail.name)
        assertEquals("https://example.com/h.png", detail.avatarUrl)
        assertEquals(7, detail.publicRepos)
        assertEquals("2020-01-01T00:00:00Z", detail.createdAt)
        assertEquals("2025-01-01T00:00:00Z", detail.updatedAt)
    }

    @Test
    fun `GitHubSearchResponse deserializes total_count and incomplete_results`() {
        val raw = """
            {
              "total_count": 100,
              "incomplete_results": false,
              "items": [ {"id": 1, "login": "octo", "avatar_url": "https://ex.com/a.png", "type": "User"} ]
            }
        """.trimIndent()
        val resp = json.decodeFromString<GitHubSearchResponse>(raw)
        assertEquals(100, resp.totalCount)
        assertEquals(false, resp.incompleteResults)
        assertEquals(1, resp.items.size)
        assertEquals("octo", resp.items.first().login)
    }
}
