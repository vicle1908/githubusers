package com.example.githubusers.feature.users.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.githubusers.feature.users.data.local.dao.RepositoryDao
import com.example.githubusers.feature.users.data.local.entity.RepositoryEntity
import java.io.IOException
import java.time.Instant
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryDaoTest {

    private lateinit var database: UsersDatabase
    private lateinit var dao: RepositoryDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            UsersDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.repositoryDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    fun replaceRepositoriesForOwner_clears_then_inserts() = runBlocking {
        val owner = "octocat"
        val initial = RepositoryEntity(
            id = 1,
            ownerLogin = owner,
            name = "old",
            fullName = "octocat/old",
            description = null,
            htmlUrl = "https://github.com/octocat/old",
            language = null,
            stargazersCount = 1,
            watchersCount = 1,
            forksCount = 0,
            openIssuesCount = 0,
            isPrivate = false,
            isFork = false,
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH,
            pushedAt = Instant.EPOCH,
            size = 1,
            defaultBranch = "main",
            topics = emptyList(),
            licenseKey = null,
            licenseName = null,
            visibility = "public",
            cachedAt = Instant.EPOCH
        )
        dao.insertRepositories(listOf(initial))

        val replacement = RepositoryEntity(
            id = 2,
            ownerLogin = owner,
            name = "new",
            fullName = "octocat/new",
            description = null,
            htmlUrl = "https://github.com/octocat/new",
            language = "Kotlin",
            stargazersCount = 10,
            watchersCount = 10,
            forksCount = 1,
            openIssuesCount = 0,
            isPrivate = false,
            isFork = false,
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH,
            pushedAt = Instant.EPOCH,
            size = 1,
            defaultBranch = "main",
            topics = listOf("android"),
            licenseKey = "mit",
            licenseName = "MIT",
            visibility = "public",
            cachedAt = Instant.now()
        )

        dao.replaceRepositoriesForOwner(owner, listOf(replacement))

        assertEquals(1, dao.getRepositoryCountByUser(owner))
        val result = dao.getRepositoriesByUser(owner).loadSingle()
        assertEquals("new", result.first().name)
    }

    private suspend fun <Key : Any, Value : Any> androidx.paging.PagingSource<Key, Value>.loadSingle(): List<Value> {
        val loadResult = load(
            androidx.paging.PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )
        return when (loadResult) {
            is androidx.paging.PagingSource.LoadResult.Page -> loadResult.data
            is androidx.paging.PagingSource.LoadResult.Error -> throw loadResult.throwable
            is androidx.paging.PagingSource.LoadResult.Invalid -> emptyList()
        }
    }
}
