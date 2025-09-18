package com.example.githubusers.feature.users.detail.data.local

import androidx.room.TypeConverter
import java.time.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Type converters for Room database.
 */
class Converters {
    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun fromStringList(value: List<String>): String = Json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = try {
        Json.decodeFromString(value)
    } catch (e: Exception) {
        emptyList()
    }
}
