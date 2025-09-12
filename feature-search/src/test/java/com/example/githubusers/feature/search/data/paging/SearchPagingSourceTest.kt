package com.example.githubusers.feature.search.data.paging

import com.example.githubusers.feature.search.data.api.SearchApiService
import com.example.githubusers.feature.search.domain.entity.SearchFilter
import com.example.githubusers.feature.search.domain.entity.SearchResultType
import com.example.githubusers.feature.search.domain.entity.SearchSortOption
import io.ktor.http.encodeURLQueryComponent
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchPagingSourceTest {
    @Test
    fun `buildEncodedSearchQuery should handle special characters in base query`() {
        // Given
        val filter = SearchFilter()
        val pagingSource = newPagingSource(filter)

        // When
        val result = pagingSource.buildEncodedSearchQuery("test query with spaces & symbols", filter)

        // Then: derive expected using Ktor's encoder for consistency
        val expected = "test query with spaces & symbols".encodeURLQueryComponent(spaceToPlus = true)
        assertTrue(result.contains(expected))
    }

    @Test
    fun `buildEncodedSearchQuery should handle complex queries with filters`() {
        // Given
        val filter =
            SearchFilter(
                type = SearchResultType.USER,
                location = "San Francisco",
                language = "Kotlin",
                minRepos = 10,
                minFollowers = 100,
                sortBy = SearchSortOption.FOLLOWERS
            )

        val pagingSource = newPagingSource(filter)

        // When
        val searchQuery = pagingSource.buildEncodedSearchQuery("test user", filter)

        // Then
        assertTrue(searchQuery.contains("test+user"))
        assertTrue(searchQuery.contains("type:user"))
        assertTrue(searchQuery.contains("location:San+Francisco"))
        assertTrue(searchQuery.contains("language:Kotlin"))
        assertTrue(searchQuery.contains("repos:>=10"))
        assertTrue(searchQuery.contains("followers:>=100"))
        assertTrue(searchQuery.contains("sort:followers"))
    }

    @Test
    fun `buildEncodedSearchQuery should handle special characters in filter values`() {
        // Given
        val filter =
            SearchFilter(
                location = "New York, NY",
                language = "C++"
            )

        val pagingSource = newPagingSource(filter)

        // When
        val searchQuery = pagingSource.buildEncodedSearchQuery("special chars", filter)

        // Then
        assertTrue(searchQuery.contains("special+chars"))
        assertTrue(searchQuery.contains("location:New+York%2C+NY"))
        assertTrue(searchQuery.contains("language:C%2B%2B"))
    }

    @Test
    fun `buildEncodedSearchQuery should handle empty base query`() {
        // Given
        val filter =
            SearchFilter(
                location = "Tokyo"
            )

        val pagingSource = newPagingSource(filter)

        // When
        val searchQuery = pagingSource.buildEncodedSearchQuery("", filter)

        // Then
        assertTrue(searchQuery.contains("location:Tokyo"))
        assertFalse(searchQuery.startsWith(" "))
    }

    @Test
    fun `buildEncodedSearchQuery should handle blank base query`() {
        // Given
        val filter =
            SearchFilter(
                language = "JavaScript"
            )

        val pagingSource = newPagingSource(filter)

        // When
        val searchQuery = pagingSource.buildEncodedSearchQuery("   ", filter)

        // Then
        assertTrue(searchQuery.contains("language:JavaScript"))
        assertFalse(searchQuery.startsWith(" "))
    }

    private fun newPagingSource(filter: SearchFilter): SearchPagingSource {
        val api = mockk<SearchApiService>(relaxed = true)
        return SearchPagingSource(api, "", filter)
    }
}
