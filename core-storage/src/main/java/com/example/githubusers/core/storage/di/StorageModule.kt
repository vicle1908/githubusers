package com.example.githubusers.core.storage.di

import com.example.githubusers.core.storage.datastore.DataStoreProvider
import com.example.githubusers.core.storage.room.RoomDatabaseProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideDataStoreProvider(): DataStoreProvider {
        return DataStoreProvider()
    }

    @Provides
    @Singleton
    fun provideRoomDatabaseProvider(): RoomDatabaseProvider {
        return RoomDatabaseProvider()
    }
}
