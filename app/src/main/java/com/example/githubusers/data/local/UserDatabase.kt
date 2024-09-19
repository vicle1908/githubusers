package com.example.githubusers.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.githubusers.domain.entity.RemoteKey
import com.example.githubusers.domain.entity.User
import com.example.githubusers.domain.entity.UserDetail

@Database(entities = [User::class, UserDetail::class, RemoteKey::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}
