package com.example.githubusers.feature.repository.data

import com.example.githubusers.core.search.SearchQueryNormalizer
import com.example.githubusers.core.search.SearchQueryNormalizer.NormalizedSearchQuery
import org.junit.Assert.assertEquals
import org.junit.Test

class RepositoryQueryParserTest {

    @Test
    fun `parser falls back to default query when empty`() {
        val normalized = NormalizedSearchQuery(
            original = "",
            qualifiers = emptyList(),
            terms = emptyList()
        )

        val parsed = RepositoryQueryParser.parse(
            normalized = normalized,
            fallbackQuery = "language:kotlin"
        )

        assertEquals("language:kotlin", parsed.networkQuery)
        assertEquals(null, parsed.language)
        assertEquals("", parsed.plainQuery)
    }

    @Test
    fun `parser extracts language qualifier and terms`() {
        val normalized = SearchQueryNormalizer.normalize("android language:java stars:>1000")

        val parsed = RepositoryQueryParser.parse(
            normalized = normalized,
            fallbackQuery = "language:kotlin"
        )

        assertEquals("android language:java stars:>1000", parsed.networkQuery)
        assertEquals("java", parsed.language)
        assertEquals("android", parsed.plainQuery)
    }
}
