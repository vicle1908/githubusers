package com.example.githubusers.feature.users.domain.usecase

import androidx.paging.PagingData
import com.example.githubusers.feature.users.detail.domain.repository.RepositorySort
import com.example.githubusers.feature.users.detail.domain.repository.UserDetailRepository
import com.example.githubusers.feature.users.detail.domain.usecase.FollowUserUseCase
import com.example.githubusers.feature.users.domain.model.Repository
import com.example.githubusers.feature.users.domain.model.UserDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FollowUserUseCaseTest {

    @Test
    fun `follow action delegates to repository`() = runTest {
        val fakeRepository = ToggleRepository()
        val useCase = FollowUserUseCase(fakeRepository)

        val result = useCase(username = "octocat", isFollowing = false)

        assertTrue(result.isSuccess)
        assertEquals("octocat", fakeRepository.lastFollowed)
    }

    @Test
    fun `unfollow action delegates to repository`() = runTest {
        val fakeRepository = ToggleRepository()
        val useCase = FollowUserUseCase(fakeRepository)

        val result = useCase(username = "octocat", isFollowing = true)

        assertTrue(result.isSuccess)
        assertEquals("octocat", fakeRepository.lastUnfollowed)
    }

    private class ToggleRepository : UserDetailRepository {
        var lastFollowed: String? = null
        var lastUnfollowed: String? = null

        override suspend fun getUserDetail(username: String): Result<UserDetail> =
            Result.failure(UnsupportedOperationException())

        override fun getUserRepositories(
            username: String,
            sort: RepositorySort,
            perPage: Int
        ): Flow<PagingData<Repository>> = throw UnsupportedOperationException()

        override suspend fun clearUserDetailCache(username: String) = Unit

        override suspend fun isFollowing(username: String): Boolean = false

        override suspend fun followUser(username: String): Result<Unit> {
            lastFollowed = username
            return Result.success(Unit)
        }

        override suspend fun unfollowUser(username: String): Result<Unit> {
            lastUnfollowed = username
            return Result.success(Unit)
        }
    }
}
