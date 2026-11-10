package com.example.ecomonitorapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ecomonitorapp.data.entities.ReportEntity
import kotlinx.coroutines.flow.Flow

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

    @Query("UPDATE report SET status = :status WHERE id = :reportId")
    suspend fun updateStatus(reportId: Long, status: String)

    @Update
    suspend fun update(report: ReportEntity)

    @Delete
    suspend fun delete(report: ReportEntity)

    @Query("DELETE FROM report WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(report: ReportEntity): Long

    @Query("SELECT * FROM report ORDER BY created_at DESC")
    fun observeAll(): Flow<List<ReportEntity>>

    // только те заявки, которые были созданы текущим пользователем
    @Query("SELECT * FROM report WHERE reported_by = :userId ORDER BY created_at DESC")
    fun observeByUser(userId: Long): Flow<List<ReportEntity>>
}