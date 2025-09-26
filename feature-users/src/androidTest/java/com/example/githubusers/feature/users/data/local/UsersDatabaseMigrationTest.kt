package com.example.githubusers.feature.users.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Migration tests for [UsersDatabase].
 */
@RunWith(AndroidJUnit4::class)
class UsersDatabaseMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        UsersDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate1To2_preservesUserData() {
        val dbName = "users-db-migration"

        helper.createDatabase(dbName, 1).apply {
            execSQL(
                "INSERT INTO `user_summaries` (`id`, `login`, `avatarUrl`, `htmlUrl`, `type`) VALUES (1, 'mojombo', 'https://avatars.githubusercontent.com/u/1?v=4', 'https://github.com/mojombo', 'User')"
            )
            execSQL(
                "INSERT INTO `remote_keys` (`id`, `nextKey`) VALUES ('user_list_remote_key', 2)"
            )
            close()
        }

        helper.runMigrationsAndValidate(dbName, 2, true, UsersDatabase.MIGRATION_1_2).apply {
            query("SELECT COUNT(*) FROM user_summaries WHERE login='mojombo'").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(1, cursor.getInt(0))
            }

            query("SELECT name FROM sqlite_master WHERE type='table' AND name='user_details'").use { cursor ->
                assertTrue(cursor.moveToFirst())
            }

            query("SELECT name FROM sqlite_master WHERE type='table' AND name='repositories'").use { cursor ->
                assertTrue(cursor.moveToFirst())
            }

            // verify we can seed new repositories and details post-migration
            execSQL(
                """
                INSERT INTO user_details (
                    login, id, avatarUrl, htmlUrl, name, company, blog, location, email, bio,
                    twitterUsername, publicRepos, publicGists, followers, following, createdAt,
                    updatedAt, type, siteAdmin, hireable, cachedAt
                ) VALUES (
                    'mojombo', 1, 'https://avatars.githubusercontent.com/u/1?v=4', 'https://github.com/mojombo',
                    'Tom Preston-Werner', 'GitHub', NULL, NULL, NULL, NULL, NULL, 10, 1, 100, 0,
                    0, 0, 'User', 0, NULL, 0
                )
                """
            )
            execSQL(
                """
                INSERT INTO repositories (
                    id, ownerLogin, name, fullName, description, htmlUrl, language, stargazersCount,
                    watchersCount, forksCount, openIssuesCount, isPrivate, isFork, createdAt,
                    updatedAt, pushedAt, size, defaultBranch, topics, licenseKey, licenseName,
                    visibility, cachedAt
                ) VALUES (
                    1, 'mojombo', 'awesome', 'mojombo/awesome', NULL, 'https://github.com/mojombo/awesome',
                    'Kotlin', 42, 42, 1, 0, 0, 0, 0, 0, 0, 1, 'main', '[]', NULL, NULL, 'public', 0
                )
                """
            )

            query("SELECT COUNT(*) FROM repositories WHERE ownerLogin='mojombo'").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(1, cursor.getInt(0))
            }

            close()
        }
    }

    @Test
    fun migrate1To2_withoutRemoteKeysStillSucceeds() {
        val dbName = "users-db-migration-no-keys"

        helper.createDatabase(dbName, 1).apply {
            execSQL(
                "INSERT INTO `user_summaries` (`id`, `login`, `avatarUrl`, `htmlUrl`, `type`) VALUES (2, 'defunkt', 'https://avatars.githubusercontent.com/u/2?v=4', 'https://github.com/defunkt', 'User')"
            )
            close()
        }

        helper.runMigrationsAndValidate(dbName, 2, true, UsersDatabase.MIGRATION_1_2).apply {
            query("SELECT COUNT(*) FROM user_summaries WHERE login='defunkt'").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(1, cursor.getInt(0))
            }
            close()
        }
    }
}
