package com.example.githubusers.feature.search.data.mapper

import com.example.githubusers.core.search.domain.SearchResult
import com.example.githubusers.core.search.domain.SearchResultType
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchMapperTest {

    @Test
    fun `toUserSummary maps core fields`() {
        val result =
            SearchResult(
                id = 123L,
                login = "octocat",
                name = "The Octocat",
                avatarUrl = "https://example.com/octocat.png",
                type = SearchResultType.ORGANIZATION,
                score = 42f,
                bio = "Mascot"
            )

        val summary = result.toUserSummary()

        assertEquals(123L, summary.id)
        assertEquals("octocat", summary.login)
        assertEquals("https://example.com/octocat.png", summary.avatarUrl)
        assertEquals("https://github.com/octocat", summary.htmlUrl)
        assertEquals("Organization", summary.type)
    }
}
