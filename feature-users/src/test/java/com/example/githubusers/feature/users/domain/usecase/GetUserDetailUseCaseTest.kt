package com.example.githubusers.feature.users.domain.usecase

import androidx.paging.PagingData
import com.example.githubusers.core.search.SearchQueryNormalizer
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.users.detail.domain.usecase.GetUserDetailUseCase
import com.example.githubusers.feature.users.domain.model.UserDetail
import com.example.githubusers.feature.users.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetUserDetailUseCaseTest {

    @Test
    fun `returns repository result`() = runTest {
        val expected = UserDetail(
            id = 1,
            login = "octocat",
            avatarUrl = "https://avatars.githubusercontent.com/u/1",
            htmlUrl = "https://github.com/octocat",
            name = "Octo Cat",
            company = "GitHub",
            blog = "https://blog.github.com",
            location = "SF",
            email = null,
            bio = "",
            twitterUsername = null,
            publicRepos = 10,
            publicGists = 2,
            followers = 100,
            following = 5,
            createdAt = java.time.Instant.EPOCH,
            updatedAt = java.time.Instant.EPOCH,
            type = "User",
            siteAdmin = false,
            hireable = null
        )
        val fakeRepository = object : UserRepository {
            override fun observeUsers(
                query: SearchQueryNormalizer.NormalizedSearchQuery
            ): Flow<PagingData<UserSummary>> = throw UnsupportedOperationException()

            override suspend fun fetchUserDetail(username: String): Result<UserDetail> = Result.success(expected)
        }

        val result = GetUserDetailUseCase(fakeRepository)("octocat")

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrThrow())
    }
}
