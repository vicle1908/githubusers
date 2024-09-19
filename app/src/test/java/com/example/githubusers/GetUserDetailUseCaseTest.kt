package com.example.githubusers

import com.example.githubusers.domain.entity.UserDetail
import com.example.githubusers.domain.repository.UserRepository
import com.example.githubusers.domain.usecase.GetUserDetailUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetUserDetailUseCaseTest {
    private lateinit var getUserDetailUseCase: GetUserDetailUseCase
    private val userRepository: UserRepository = mockk()

    @Before
    fun setup() {
        getUserDetailUseCase = GetUserDetailUseCase(userRepository)
    }

    @Test
    fun `invoke use case returns success`() =
        runTest {
            // Mock the repository to return a successful result
            coEvery { userRepository.getUserDetail("testuser") } returns
                flowOf(
                    Result.success(
                        UserDetail(
                            id = 1,
                            login = "testuser",
                            avatarUrl = "https://avatar.url",
                            htmlUrl = "https://html.url",
                            location = "Test Location",
                            followers = 100,
                            following = 50,
                            blog = "https://blog.url",
                        ),
                    ),
                )

            // Execute the use case
            val result = getUserDetailUseCase.invoke("testuser").first()

            // Verify the result is successful
            assertTrue(result.isSuccess)
            assertEquals("testuser", result.getOrNull()?.login)
        }

    @Test
    fun `invoke use case returns failure`() =
        runTest {
            // Mock the repository to return a failure result
            coEvery { userRepository.getUserDetail("invaliduser") } returns
                flowOf(
                    Result.failure(Exception("User not found")),
                )

            // Execute the use case
            val result = getUserDetailUseCase.invoke("invaliduser").first()

            // Verify the result is a failure
            assertTrue(result.isFailure)
            assertEquals("User not found", result.exceptionOrNull()?.message)
        }
}
