package com.example.githubusers

import com.example.githubusers.domain.entity.UserDetail
import com.example.githubusers.domain.usecase.GetUserDetailUseCase
import com.example.githubusers.presentation.state.userdetail.UserDetailState
import com.example.githubusers.presentation.viewmodel.UserDetailViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserDetailViewModelTest {
    private lateinit var viewModel: UserDetailViewModel
    private val getUserDetailUseCase: GetUserDetailUseCase = mockk()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = UserDetailViewModel(getUserDetailUseCase)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchUserDetail returns success`() =
        runTest {
            // Mock the successful use case result
            coEvery { getUserDetailUseCase.invoke("testuser") } returns
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

            // Trigger the ViewModel function
            viewModel.fetchUserDetail("testuser")

            // Verify that the state is success
            assertTrue(viewModel.userDetailState.value is UserDetailState.Success)
            val userDetail = (viewModel.userDetailState.value as UserDetailState.Success).userDetail
            assertEquals("testuser", userDetail?.login)
            assertEquals("Test Location", userDetail?.location)
        }

    @Test
    fun `fetchUserDetail returns failure`() =
        runTest {
            // Mock the use case to return a failure
            coEvery { getUserDetailUseCase.invoke("invaliduser") } returns
                flowOf(
                    Result.failure(Exception("User not found")),
                )

            // Trigger the ViewModel function
            viewModel.fetchUserDetail("invaliduser")

            // Verify that the state is error
            assertTrue(viewModel.userDetailState.value is UserDetailState.Error)
            val errorState = viewModel.userDetailState.value as UserDetailState.Error
            assertEquals("User not found", errorState.message)
        }
}
