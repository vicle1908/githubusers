package com.example.githubusers.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for caching search results
 */
@Entity(tableName = "search_results")
data class SearchResultEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "query")
    val query: String,
    @ColumnInfo(name = "user_id")
    val userId: Long,
    @ColumnInfo(name = "page")
    val page: Int,
    @ColumnInfo(name = "total_count")
    val totalCount: Int,
    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis(),
)
