package com.example.githubusers.core.storage.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides configured Room database instances for the application.
 * This is shared infrastructure that features can use.
 */
@Singleton
class RoomDatabaseProvider
    @Inject
    constructor() {
        /**
         * Creates a Room database instance for the given context.
         * Features can use this to create their own database instances.
         */
        inline fun <reified T : RoomDatabase> createDatabase(
            context: Context,
            name: String,
            builder: RoomDatabase.Builder<T>.() -> Unit = {},
        ): T =
            Room
                .databaseBuilder(context, T::class.java, name)
                .apply(builder)
                .build()
    }
