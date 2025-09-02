package com.example.githubusers.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.githubusers.core.data.local.entity.RepositoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Repository operations
 */
@Dao
interface RepositoryDao {
    @Query("SELECT * FROM repositories WHERE owner_login = :ownerLogin ORDER BY updated_at DESC")
    fun getRepositoriesByOwner(ownerLogin: String): Flow<List<RepositoryEntity>>

    @Query("SELECT * FROM repositories WHERE owner_login = :ownerLogin ORDER BY updated_at DESC")
    fun getRepositoriesByOwnerPaging(ownerLogin: String): PagingSource<Int, RepositoryEntity>

    @Query("SELECT * FROM repositories WHERE owner_login = :ownerLogin ORDER BY updated_at DESC")
    suspend fun getRepositoriesByOwnerSync(ownerLogin: String): List<RepositoryEntity>

    @Query("SELECT * FROM repositories WHERE id = :repoId")
    suspend fun getRepositoryById(repoId: Long): RepositoryEntity?

    @Query("SELECT * FROM repositories WHERE full_name = :fullName")
    suspend fun getRepositoryByFullName(fullName: String): RepositoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepository(repository: RepositoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepositories(repositories: List<RepositoryEntity>)

    @Update
    suspend fun updateRepository(repository: RepositoryEntity)

    @Delete
    suspend fun deleteRepository(repository: RepositoryEntity)

    @Query("DELETE FROM repositories WHERE owner_login = :ownerLogin")
    suspend fun deleteRepositoriesByOwner(ownerLogin: String)

    @Query("DELETE FROM repositories")
    suspend fun deleteAllRepositories()

    @Query("DELETE FROM repositories WHERE cached_at < :timestamp")
    suspend fun deleteRepositoriesOlderThan(timestamp: Long)

    @Query("SELECT COUNT(*) FROM repositories WHERE owner_login = :ownerLogin")
    suspend fun getRepositoryCountByOwner(ownerLogin: String): Int

    @Query(
        """
        SELECT * FROM repositories 
        WHERE language = :language 
        ORDER BY stargazers_count DESC
        LIMIT :limit
    """,
    )
    suspend fun getTopRepositoriesByLanguage(
        language: String,
        limit: Int,
    ): List<RepositoryEntity>

    @Transaction
    suspend fun refreshRepositories(
        ownerLogin: String,
        repositories: List<RepositoryEntity>,
    ) {
        // Delete old repositories for this owner
        deleteRepositoriesByOwner(ownerLogin)
        // Insert new repositories
        insertRepositories(repositories)
    }
}
