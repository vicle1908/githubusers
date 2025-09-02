package com.example.githubusers.core.data.local.database

import androidx.room.TypeConverter
import java.util.Date

/**
 * Type converters for Room database
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}
