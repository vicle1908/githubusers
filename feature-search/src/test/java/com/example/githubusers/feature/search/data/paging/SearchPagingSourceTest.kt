package com.example.githubusers.feature.search.data.paging

import com.example.githubusers.feature.search.domain.entity.SearchFilter
import com.example.githubusers.feature.search.domain.entity.SearchResultType
import com.example.githubusers.feature.search.domain.entity.SearchSortOption
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchPagingSourceTest {
    @Test
    fun `buildSearchQuery should handle special characters correctly`() {
        // Given
        val filter = SearchFilter()
        val searchPagingSource = SearchPagingSourceSpy(null, "", filter)

        // When
        val result = searchPagingSource.buildSearchQuery("test query with spaces", filter)

        // Then
        assertTrue(result.contains("test query with spaces"))
    }

    @Test
    fun `buildSearchQuery should handle complex queries with filters`() {
        // Given
        val filter =
            SearchFilter(
                type = SearchResultType.USER,
                location = "San Francisco",
                language = "Kotlin",
                minRepos = 10,
                minFollowers = 100,
                sortBy = SearchSortOption.FOLLOWERS,
            )

        val searchPagingSource = SearchPagingSourceSpy(null, "", filter)

        // When
        val searchQuery = searchPagingSource.buildSearchQuery("test user", filter)

        // Then
        assertTrue(searchQuery.contains("test user"))
        assertTrue(searchQuery.contains("type:user"))
        assertTrue(searchQuery.contains("location:San Francisco"))
        assertTrue(searchQuery.contains("language:Kotlin"))
        assertTrue(searchQuery.contains("repos:>=10"))
        assertTrue(searchQuery.contains("followers:>=100"))
        assertTrue(searchQuery.contains("sort:followers"))
    }

    @Test
    fun `buildSearchQuery should handle special characters in filter values`() {
        // Given
        val filter =
            SearchFilter(
                location = "New York, NY",
                language = "C++",
            )

        val searchPagingSource = SearchPagingSourceSpy(null, "", filter)

        // When
        val searchQuery = searchPagingSource.buildSearchQuery("special chars", filter)

        // Then
        assertTrue(searchQuery.contains("special chars"))
        assertTrue(searchQuery.contains("location:New York, NY"))
        assertTrue(searchQuery.contains("language:C++"))
    }

    // Spy class to access the protected method
    class SearchPagingSourceSpy(
        apiService: com.example.githubusers.core.data.api.GitHubApiService?,
        query: String,
        filter: SearchFilter,
    ) : SearchPagingSource(apiService, query, filter) {
        public override fun buildSearchQuery(
            baseQuery: String,
            filter: SearchFilter,
        ): String = super.buildSearchQuery(baseQuery, filter)
    }
}
