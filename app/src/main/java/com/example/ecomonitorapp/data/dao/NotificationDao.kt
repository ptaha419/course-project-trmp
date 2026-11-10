package com.example.ecomonitorapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.ecomonitorapp.data.entities.NotificationEntity

@Dao
interface NotificationDao {
    @Insert
    suspend fun insert(notification: NotificationEntity): Long

    @Query("SELECT * FROM notification ORDER BY created_at DESC")
    suspend fun getAll(): List<NotificationEntity>

    @Query("SELECT * FROM notification WHERE id = :id")
    suspend fun getById(id: Long): NotificationEntity?

    @Update
    suspend fun update(notification: NotificationEntity)

    @Delete
    suspend fun delete(notification: NotificationEntity)

    @Query("DELETE FROM notification WHERE id = :id")
    suspend fun deleteById(id: Long)
}