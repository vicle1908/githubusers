package com.example.githubusers.feature.repository.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.githubusers.feature.repository.data.local.RepositoryEntity

@Dao
interface RepositoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repositories: List<RepositoryEntity>)

    @Query("DELETE FROM repositories")
    suspend fun clearAll()

    @Query("SELECT * FROM repositories ORDER BY stargazersCount DESC")
    fun pagingSource(): PagingSource<Int, RepositoryEntity>

    @Query(
        "SELECT * FROM repositories " +
            "WHERE (:language IS NULL OR LOWER(language) = LOWER(:language)) " +
            "AND (" +
            "    :plainQuery IS NULL OR :plainQuery = '' OR " +
            "    LOWER(name) LIKE '%' || :plainQuery || '%' OR " +
            "    LOWER(fullName) LIKE '%' || :plainQuery || '%' OR " +
            "    LOWER(ownerLogin) LIKE '%' || :plainQuery || '%' OR " +
            "    LOWER(COALESCE(description, '')) LIKE '%' || :plainQuery || '%'" +
            ") " +
            "ORDER BY stargazersCount DESC"
    )
    fun searchPagingSource(language: String?, plainQuery: String?): PagingSource<Int, RepositoryEntity>
}
