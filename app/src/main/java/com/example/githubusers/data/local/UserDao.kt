package com.example.githubusers.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.githubusers.domain.entity.User
import com.example.githubusers.domain.entity.UserDetail
import kotlinx.coroutines.flow.Flow

/**
 * DAO for managing the `User` and `UserDetail` tables in Room.
 */
@Dao
interface UserDao {
    /**
     * Inserts a list of users into the database.
     * If the user already exists, it will be replaced.
     *
     * @param users The list of users to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    /**
     * Retrieves all users in paginated form.
     * @return A PagingSource for the users.
     */
    @Query("SELECT * FROM github_users")
    fun getUsersPaged(): PagingSource<Int, User>

    /**
     * Clears all users in the database.
     */
    @Query("DELETE FROM github_users")
    suspend fun clearAll()

    /**
     * Inserts a detailed user entry into the database.
     * If the user already exists, it will be replaced.
     *
     * @param userDetail The detailed user information to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserDetail(userDetail: UserDetail)

    /**
     * Retrieves a single user by their login username from the database.
     *
     * @param username The login username of the user to be retrieved.
     * @return A Flow emitting the user data if found, otherwise `null`.
     */
    @Query("SELECT * FROM github_user_details WHERE login = :username LIMIT 1")
    fun getUserDetail(username: String): Flow<UserDetail?>
}
