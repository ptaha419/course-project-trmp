package com.example.ecomonitorapp.data.repositories

import android.content.Context
import com.example.ecomonitorapp.data.dao.ReportDao
import com.example.ecomonitorapp.data.db.DbProvider
import com.example.ecomonitorapp.data.mappers.toDomain
import com.example.ecomonitorapp.data.mappers.toEntity
import com.example.ecomonitorapp.models.Report

class ReportRepository(private val context: Context) {
    private val dao: ReportDao = DbProvider.getDatabase(context).reportDao()

    suspend fun createReport(report: Report): Long = try {
        dao.insert(report.toEntity())
    } catch (e: Exception) {
        e.printStackTrace()
        -1L
    }

    suspend fun getAllReports(): List<Report> =
        dao.getAll().map { it.toDomain() }

    suspend fun getReportsByUser(userId: Long): List<Report> =
        dao.getByUserId(userId).map { it.toDomain() }

    suspend fun updateReport(report: Report): Boolean = try {
        dao.update(report.toEntity())
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    suspend fun deleteReport(report: Report): Boolean = try {
        dao.delete(report.toEntity())
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}