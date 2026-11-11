package com.example.ecomonitorapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ecomonitorapp.data.dao.NotificationDao
import com.example.ecomonitorapp.data.dao.ReportDao
import com.example.ecomonitorapp.data.dao.UserDao
import com.example.ecomonitorapp.data.dao.ViolationCardDao
import com.example.ecomonitorapp.data.entities.NotificationEntity
import com.example.ecomonitorapp.data.entities.ReportEntity
import com.example.ecomonitorapp.data.entities.UserEntity
import com.example.ecomonitorapp.data.entities.ViolationCardEntity

@Database(entities =
    [UserEntity::class,
    ReportEntity::class,
    ViolationCardEntity::class,
    NotificationEntity::class],
    version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun reportDao(): ReportDao
    abstract fun violationCardDao(): ViolationCardDao
    abstract fun notificationDao(): NotificationDao
}