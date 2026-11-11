package com.example.ecomonitorapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.ecomonitorapp.data.entities.ViolationCardEntity
import com.example.ecomonitorapp.ui.screens.violationCards.CardWithReportRow
import kotlinx.coroutines.flow.Flow

@Dao
interface ViolationCardDao {
    @Insert
    suspend fun insert(violationCard: ViolationCardEntity): Long

    @Query("SELECT * FROM violation_card ORDER BY created_at DESC")
    suspend fun getAll(): List<ViolationCardEntity>

    @Transaction
    @Query("SELECT * FROM violation_card ORDER BY created_at DESC")
    fun observeAllWithReports(): Flow<List<CardWithReportRow>>

    @Query("SELECT * FROM violation_card WHERE id = :id")
    suspend fun getById(id: Long): ViolationCardEntity?

    @Query("SELECT * FROM violation_card WHERE report = :reportId LIMIT 1")
    suspend fun getByReportId(reportId: Long): ViolationCardEntity?

    @Update
    suspend fun update(violationCard: ViolationCardEntity)

    @Delete
    suspend fun delete(violationCard: ViolationCardEntity)

    @Query("DELETE FROM violation_card WHERE id = :id")
    suspend fun deleteById(id: Long)
}