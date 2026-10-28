package com.example.ecomonitorapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ecomonitorapp.data.dao.UserDao
import com.example.ecomonitorapp.data.entities.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}