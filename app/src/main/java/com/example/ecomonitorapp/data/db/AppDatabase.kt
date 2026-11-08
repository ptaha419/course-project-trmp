package com.example.ecomonitorapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ecomonitorapp.data.dao.ReportDao
import com.example.ecomonitorapp.data.dao.UserDao
import com.example.ecomonitorapp.data.entities.ReportEntity
import com.example.ecomonitorapp.data.entities.UserEntity

@Database(entities =
    [UserEntity::class,
    ReportEntity::class],
    version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun reportDao(): ReportDao
}