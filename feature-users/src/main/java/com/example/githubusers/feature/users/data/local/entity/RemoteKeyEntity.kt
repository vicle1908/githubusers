package com.example.githubusers.feature.users.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Remote key entity for managing paging state with RemoteMediator.
 */
@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(@PrimaryKey val id: String, val prevKey: Int?, val nextKey: Int?)
