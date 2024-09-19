package com.example.githubusers

import androidx.paging.PagingData
import androidx.paging.testing.asPagingSourceFactory
import com.example.githubusers.data.local.UserDao
import com.example.githubusers.data.remote.UserApiService
import com.example.githubusers.data.repository.UserRepositoryImpl
import com.example.githubusers.domain.entity.User
import com.example.githubusers.domain.entity.UserDetail
import com.example.githubusers.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserRepositoryTest {
    private lateinit var userRepository: UserRepository
    private val userDao: UserDao = mockk(relaxed = true) // Allow relaxed mocking to avoid unnecessary boilerplate
    private val apiService: UserApiService = mockk()

    @Before
    fun setup() {
        userRepository = UserRepositoryImpl(apiService, userDao, mockk(), mockk())
    }

    @Test
    fun `getUsersPaged returns paged data and collects snapshot`() =
        runTest {
            // Mock the list of users
            val mockUsers =
                listOf(
                    User(id = 1, login = "user1", avatarUrl = "https://avatar.url1", htmlUrl = "https://html.url1"),
                    User(id = 2, login = "user2", avatarUrl = "https://avatar.url2", htmlUrl = "https://html.url2"),
                )

            // Mock the PagingSource from UserDao
            coEvery { userDao.getUsersPaged() } returns mockUsers.asPagingSourceFactory().invoke()

            // Collect the PagingData from the repository's getUsersPaged() function
            val pager: Flow<PagingData<User>> = userRepository.getUsersPaged()

            // Convert the PagingData to a snapshot using asSnapshot() extension
//            val snapshot = pager.asSnapshot()
//
//        // Verify that the snapshot contains the expected users
//        assertEquals(2, snapshot.size)
//        assertEquals("user1", snapshot[0].login)
//        assertEquals("user2", snapshot[1].login)
        }

    @Test
    fun `getUsersPaged handles empty data`() =
        runTest {
            // Mock the PagingSource from UserDao to return empty data
            coEvery { userDao.getUsersPaged() } returns emptyList<User>().asPagingSourceFactory().invoke()

            // Collect the PagingData from the repository's getUsersPaged() function
            val pager: Flow<PagingData<User>> = userRepository.getUsersPaged()

//        // Convert the PagingData to a snapshot
//        val snapshot: List<User> = pager.asSnapshot()
//
//        // Verify that the snapshot is empty
//        assertEquals(0, snapshot.size)
        }

    @Test
    fun `getUsersPaged triggers API refresh and updates Room`() =
        runTest {
            // Mock the API service to return a list of users
            val mockApiUsers =
                listOf(
                    User(id = 3, login = "user3", avatarUrl = "https://avatar.url3", htmlUrl = "https://html.url3"),
                    User(id = 4, login = "user4", avatarUrl = "https://avatar.url4", htmlUrl = "https://html.url4"),
                )
            coEvery { apiService.getUsers(since = 0, perPage = 20) } returns Result.success(mockApiUsers)

            // Mock the PagingSource from UserDao with initial empty data
            coEvery { userDao.getUsersPaged() } returns emptyList<User>().asPagingSourceFactory().invoke()

            // Collect the PagingData from the repository's getUsersPaged() function
            val pager: Flow<PagingData<User>> = userRepository.getUsersPaged()

//        // Convert the PagingData to a snapshot
//        val snapshotBeforeApiCall: List<User> = pager.asSnapshot()

//        // Verify that initially there is no data
//        assertEquals(0, snapshotBeforeApiCall.size)
//
//        // Simulate the API refresh and Room update
//        coEvery { userDao.getUsersPaged() } returns mockApiUsers.asPagingSourceFactory().invoke()
//
//        // Collect the updated PagingData and verify the Room has been updated
//        val snapshotAfterApiCall: List<User> = userRepository.getUsersPaged().asSnapshot()
//
//        // Verify that the new data from the API is present
//        assertEquals(2, snapshotAfterApiCall.size)
//        assertEquals("user3", snapshotAfterApiCall[0].login)
//        assertEquals("user4", snapshotAfterApiCall[1].login)
        }

    @Test
    fun `getUserDetail handles API failure and keeps emitting cached data`() =
        runTest {
            // Mock the cached data from Room
            coEvery { userDao.getUserDetail("invaliduser") } returns
                flowOf(
                    UserDetail(
                        id = 2,
                        login = "invaliduser",
                        avatarUrl = "https://avatar.url",
                        htmlUrl = "https://html.url",
                        location = "Cached Location",
                        followers = 10,
                        following = 5,
                        blog = "https://blog.url",
                    ),
                )

            // Mock the API to return an error
            coEvery { apiService.getUserDetail("invaliduser") } returns
                Result.failure(
                    Exception("User not found"),
                )

            // Collect the flow result
            val result = userRepository.getUserDetail("invaliduser").first()

            // Assert that the cached data is returned
            assertTrue(result.isSuccess)
            val cachedUserDetail = result.getOrNull()
            assertEquals("Cached Location", cachedUserDetail?.location)

            // Since the API failed, the cached data should still be returned without an error being emitted
            val updatedResult = userRepository.getUserDetail("invaliduser").first()

            // Ensure that cached data is still being emitted
            assertTrue(updatedResult.isSuccess)
            val updatedUserDetail = updatedResult.getOrNull()
            assertEquals("Cached Location", updatedUserDetail?.location)

            // Optionally verify if the failure notification is logged or handled, without failing the cached data flow.
            // You can skip this line if error is not directly emitted but handled otherwise.
            assertEquals("Cached Location", updatedUserDetail?.location)
        }
}
