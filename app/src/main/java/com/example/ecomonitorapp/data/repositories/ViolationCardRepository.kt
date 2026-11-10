package com.example.ecomonitorapp.data.repositories

import android.content.Context
import com.example.ecomonitorapp.data.dao.ReportDao
import com.example.ecomonitorapp.data.dao.ViolationCardDao
import com.example.ecomonitorapp.data.db.DbProvider
import com.example.ecomonitorapp.data.entities.ReportEntity
import com.example.ecomonitorapp.data.entities.ViolationCardEntity
import com.example.ecomonitorapp.data.managers.SessionManager

class ViolationCardRepository(private val context: Context,
                              private val session: SessionManager)
{
    private val dao: ViolationCardDao = DbProvider.getDatabase(context).violationCardDao()

    suspend fun getViolationCardById(id: Long): ViolationCardEntity? = dao.getById(id)
}