package com.example.githubusers.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey
    val userId: Int, // Identifier for each user
    val nextKey: Int?, // Next page key (used for appending)
)
