package com.example.githubusers.feature.users.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.githubusers.feature.users.data.local.dao.UserDetailDao
import com.example.githubusers.feature.users.data.local.entity.UserDetailEntity
import java.io.IOException
import java.time.Instant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDetailDaoTest {

    private lateinit var database: UsersDatabase
    private lateinit var dao: UserDetailDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            UsersDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.userDetailDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    fun replaceUserDetail_overwritesPreviousValue() = runBlocking {
        val username = "octocat"
        val original = sampleDetail(username, bio = "Old")
        dao.insertUserDetail(original)

        val replacement = sampleDetail(username, bio = "New")
        dao.replaceUserDetail(replacement)

        val stored = dao.getUserDetail(username)
        assertEquals("New", stored?.bio)
    }

    @Test
    fun getUserDetailFlow_emitsUpdates() = runBlocking {
        val username = "octocat"
        val flow = dao.getUserDetailFlow(username)
        assertNull(flow.first())

        dao.replaceUserDetail(sampleDetail(username, company = "GitHub"))
        assertEquals("GitHub", flow.first()?.company)
    }

    private fun sampleDetail(username: String, bio: String? = null, company: String? = null) = UserDetailEntity(
        login = username,
        id = 1,
        avatarUrl = "https://avatars.githubusercontent.com/u/1",
        htmlUrl = "https://github.com/$username",
        name = "Octo Cat",
        company = company,
        blog = null,
        location = null,
        email = null,
        bio = bio,
        twitterUsername = null,
        publicRepos = 10,
        publicGists = 1,
        followers = 100,
        following = 5,
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH,
        type = "User",
        siteAdmin = false,
        hireable = null,
        cachedAt = Instant.now()
    )
}
