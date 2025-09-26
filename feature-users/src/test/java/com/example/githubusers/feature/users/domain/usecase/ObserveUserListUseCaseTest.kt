package com.example.githubusers.feature.users.domain.usecase

import androidx.paging.PagingData
import com.example.githubusers.core.search.SearchQueryNormalizer
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.users.domain.list.usecase.ObserveUserListUseCase
import com.example.githubusers.feature.users.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ObserveUserListUseCaseTest {

    @Test
    fun `delegates to repository with requested query`() = runTest {
        val fakeRepository = FakeUserRepository()
        val useCase = ObserveUserListUseCase(fakeRepository)
        val normalised = SearchQueryNormalizer.normalize("android")

        val pagingData = useCase(normalised).first()

        assertEquals(normalised, fakeRepository.lastQuery)
        assertNotNull(pagingData)
    }

    private class FakeUserRepository : UserRepository {
        var lastQuery: SearchQueryNormalizer.NormalizedSearchQuery? = null

        override fun observeUsers(query: SearchQueryNormalizer.NormalizedSearchQuery): Flow<PagingData<UserSummary>> =
            flow {
                lastQuery = query
                emit(PagingData.empty())
            }

        override suspend fun fetchUserDetail(username: String) = error("Not required")
    }
}
