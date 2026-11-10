package com.example.ecomonitorapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.ecomonitorapp.data.entities.ViolationCardEntity

@Dao
interface ViolationCardDao {
    @Insert
    suspend fun insert(violationCard: ViolationCardEntity): Long

    @Query("SELECT * FROM violation_card ORDER BY created_at DESC")
    suspend fun getAll(): List<ViolationCardEntity>

    @Query("SELECT * FROM violation_card WHERE id = :id")
    suspend fun getById(id: Long): ViolationCardEntity?

    @Update
    suspend fun update(violationCard: ViolationCardEntity)

    @Delete
    suspend fun delete(violationCard: ViolationCardEntity)

    @Query("DELETE FROM violation_card WHERE id = :id")
    suspend fun deleteById(id: Long)
}