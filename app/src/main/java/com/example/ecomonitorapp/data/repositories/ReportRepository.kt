package com.example.ecomonitorapp.data.repositories

import android.content.Context
import com.example.ecomonitorapp.data.dao.ReportDao
import com.example.ecomonitorapp.data.db.DbProvider
import com.example.ecomonitorapp.data.entities.ReportEntity
import com.example.ecomonitorapp.data.managers.GeoLoc
import com.example.ecomonitorapp.data.managers.SessionManager
import com.example.ecomonitorapp.data.mappers.toDomain
import com.example.ecomonitorapp.data.mappers.toEntity
import com.example.ecomonitorapp.models.Report
import com.example.ecomonitorapp.models.ReportPriority
import com.example.ecomonitorapp.models.ViolationType
import kotlin.time.Clock

class ReportRepository(private val context: Context,
                       private val session: SessionManager
) {
    private val dao: ReportDao = DbProvider.getDatabase(context).reportDao()

    suspend fun createReport(
        violationType: ViolationType,
        description: String,
        geoLoc: GeoLoc,
        photo: String?,
        reportPriority: ReportPriority
    ): Report? {
        val entity = ReportEntity(
            violationType = violationType.name,
            description = description,
            latitude = geoLoc.latitude,
            longitude = geoLoc.longitude,
            photo = photo,
            reportPriority = reportPriority.name,
            createdAt = Clock.System.now().toEpochMilliseconds(),
            reportedBy = session.currentUserId
        )
        return try {
            val newId = dao.insert(entity)
            entity.copy(id = newId).toDomain()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getReportsByUser(): List<Report> =
        dao.getByUserId(session.currentUserId).map { it.toDomain() }

    suspend fun getAllReports(): List<Report> =
        dao.getAll().map { it.toDomain() }

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