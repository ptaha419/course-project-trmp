package com.example.ecomonitorapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.ecomonitorapp.data.entities.ReportEntity

@Dao
interface ReportDao {
    @Insert
    suspend fun insert(report: ReportEntity): Long

    @Query("SELECT * FROM report ORDER BY created_at DESC")
    suspend fun getAll(): List<ReportEntity>

    @Query("SELECT * FROM report WHERE id = :id")
    suspend fun getById(id: Long): ReportEntity?

    @Query("SELECT * FROM report WHERE reported_by = :userId ORDER BY created_at DESC")
    suspend fun getByUserId(userId: Long): List<ReportEntity>

    @Update
    suspend fun update(report: ReportEntity)

    @Delete
    suspend fun delete(report: ReportEntity)
}