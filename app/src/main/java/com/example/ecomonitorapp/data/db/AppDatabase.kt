package com.example.ecomonitorapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ecomonitorapp.models.User
import com.example.ecomonitorapp.data.dao.UserDao

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}