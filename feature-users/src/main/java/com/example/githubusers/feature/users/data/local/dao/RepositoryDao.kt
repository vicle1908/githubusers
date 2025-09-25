package com.example.githubusers.feature.users.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.githubusers.feature.users.data.local.entity.RepositoryEntity

/**
 * DAO for repository operations.
 */
@Dao
interface RepositoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepositories(repositories: List<RepositoryEntity>)

    @androidx.room.Transaction
    suspend fun replaceRepositoriesForOwner(ownerLogin: String, repositories: List<RepositoryEntity>) {
        deleteRepositoriesByUser(ownerLogin)
        insertRepositories(repositories)
    }

    @Query("SELECT * FROM repositories WHERE ownerLogin = :username ORDER BY updatedAt DESC")
    fun getRepositoriesByUser(username: String): PagingSource<Int, RepositoryEntity>

    @Query("SELECT * FROM repositories WHERE ownerLogin = :username ORDER BY stargazersCount DESC")
    fun getRepositoriesByUserSortedByStars(username: String): PagingSource<Int, RepositoryEntity>

    @Query("SELECT * FROM repositories WHERE ownerLogin = :username ORDER BY createdAt DESC")
    fun getRepositoriesByUserSortedByCreated(username: String): PagingSource<Int, RepositoryEntity>

    @Query("SELECT * FROM repositories WHERE ownerLogin = :username ORDER BY pushedAt DESC")
    fun getRepositoriesByUserSortedByPushed(username: String): PagingSource<Int, RepositoryEntity>

    @Query("SELECT * FROM repositories WHERE ownerLogin = :username ORDER BY name ASC")
    fun getRepositoriesByUserSortedByName(username: String): PagingSource<Int, RepositoryEntity>

    @Query("DELETE FROM repositories WHERE ownerLogin = :username")
    suspend fun deleteRepositoriesByUser(username: String)

    @Query("DELETE FROM repositories")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM repositories WHERE ownerLogin = :username")
    suspend fun getRepositoryCountByUser(username: String): Int
}
