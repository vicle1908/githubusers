package com.example.githubusers.feature.users.domain.usecase

import androidx.paging.PagingData
import com.example.githubusers.feature.users.detail.domain.repository.RepositorySort
import com.example.githubusers.feature.users.detail.domain.repository.UserDetailRepository
import com.example.githubusers.feature.users.detail.domain.usecase.GetUserRepositoriesUseCase
import com.example.githubusers.feature.users.domain.model.Repository
import com.example.githubusers.feature.users.domain.model.RepositoryLicense
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetUserRepositoriesUseCaseTest {

    @Test
    fun `delegates to repository with sort parameter`() = runTest {
        val fakeRepository = FakeUserDetailRepository()
        val useCase = GetUserRepositoriesUseCase(fakeRepository)

        val result = useCase(username = "octocat", sort = RepositorySort.STARS).first()

        assertEquals("octocat", fakeRepository.lastUsername)
        assertEquals(RepositorySort.STARS, fakeRepository.lastSort)
        assertEquals(1, result.size)
        assertEquals("awesome", result[0].name)
    }

    private class FakeUserDetailRepository : UserDetailRepository {
        var lastUsername: String? = null
        var lastSort: RepositorySort? = null

        override suspend fun getUserDetail(username: String) = error("Not required")

        override fun getUserRepositories(
            username: String,
            sort: RepositorySort,
            perPage: Int
        ): Flow<PagingData<Repository>> {
            lastUsername = username
            lastSort = sort
            val repository = Repository(
                id = 1,
                name = "awesome",
                fullName = "octocat/awesome",
                description = "",
                htmlUrl = "https://github.com/octocat/awesome",
                language = "Kotlin",
                stargazersCount = 42,
                watchersCount = 42,
                forksCount = 5,
                openIssuesCount = 1,
                isPrivate = false,
                isFork = false,
                createdAt = Instant.EPOCH,
                updatedAt = Instant.EPOCH,
                pushedAt = Instant.EPOCH,
                size = 1,
                defaultBranch = "main",
                topics = emptyList(),
                license = RepositoryLicense("mit", "MIT", null, null),
                visibility = "public"
            )
            return flow { emit(PagingData.from(listOf(repository))) }
        }

        override suspend fun clearUserDetailCache(username: String) = Unit

        override suspend fun isFollowing(username: String) = false

        override suspend fun followUser(username: String) = Result.success(Unit)

        override suspend fun unfollowUser(username: String) = Result.success(Unit)
    }
}
