package com.example.ecomonitorapp.data.repositories

import android.content.Context
import android.util.Log
import com.example.ecomonitorapp.data.dao.ReportDao
import com.example.ecomonitorapp.data.dao.UserDao
import com.example.ecomonitorapp.data.dao.ViolationCardDao
import com.example.ecomonitorapp.data.db.DbProvider
import com.example.ecomonitorapp.data.entities.ReportEntity
import com.example.ecomonitorapp.data.entities.ViolationCardEntity
import com.example.ecomonitorapp.data.managers.GeoLoc
import com.example.ecomonitorapp.data.managers.SessionManager
import com.example.ecomonitorapp.data.mappers.toDomain
import com.example.ecomonitorapp.data.mappers.toDomainOrNull
import com.example.ecomonitorapp.data.mappers.toEntity
import com.example.ecomonitorapp.data.mappers.toUser
import com.example.ecomonitorapp.models.Report
import com.example.ecomonitorapp.models.ReportPriority
import com.example.ecomonitorapp.models.ReportStatus
import com.example.ecomonitorapp.models.User
import com.example.ecomonitorapp.models.ViolationCard
import com.example.ecomonitorapp.models.ViolationStatus
import com.example.ecomonitorapp.models.ViolationType
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock

class ReportRepository(private val context: Context,
                       private val session: SessionManager
) {
    private val dao: ReportDao = DbProvider.getDatabase(context).reportDao()
    private val userDao: UserDao = DbProvider.getDatabase(context).userDao()
    private val violationCardDao: ViolationCardDao = DbProvider.getDatabase(context).violationCardDao()

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
            reportedBy = session.currentUserId,
            status = ReportStatus.SENT.name,
        )
        return try {
            val newId = dao.insert(entity)
            entity.copy(id = newId).toDomainOrNull() ?: error("geoLoc не null")
        } catch (e: Exception) {
            Log.d("CreateReport", "reportedBy = ${entity.reportedBy}")
            val userExists = userDao.getById(entity.reportedBy) != null
            Log.d("CreateReport", "user exists in DB: $userExists")
            Log.e("CreateReport", "createReport failed", e)
            e.printStackTrace()
            null
        }
    }

    fun getCurrentUserId(): Long? = session.getCurrentUserIdOrNull()

    suspend fun getUserById(id: Long): User? =
        userDao.getById(id)?.toUser()

    suspend fun getReportsByUser(): List<Report?> =
        dao.getByUserId(session.currentUserId).map { it.toDomainOrNull() }

    suspend fun getAllReports(): List<Report> =
        dao.getAll()
            .filter { it.status != ReportStatus.DRAFT.name }
            .mapNotNull { it.toDomainOrNull() }

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

    // сохранить / обновить черновик (id = 0 — новая запись)
    suspend fun upsertDraft(
        id: Long?,
        violationType: ViolationType,
        description: String,
        geoLoc: GeoLoc?,
        photo: String?,
        reportPriority: ReportPriority
    ): Long? = try {
        dao.upsert(
            ReportEntity(
                id = id,
                violationType = violationType.name,
                description = description,
                latitude = geoLoc?.latitude,
                longitude = geoLoc?.longitude,
                photo = photo,
                reportPriority = reportPriority.name,
                createdAt = Clock.System.now().toEpochMilliseconds(),
                reportedBy = session.currentUserId,
                status = ReportStatus.DRAFT.name
            )
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    // загрузить черновик по id (для loadDraft)
    suspend fun getDraftById(id: Long): ReportEntity? = dao.getById(id)

    suspend fun getReportById(id: Long): Report? {
        val entity = dao.getById(id) ?: return null
        val violationCard = violationCardDao.getByReportId(id)?.toDomain()
        return entity.toDomainOrNull(violationCard)
    }

    fun observeAll(): Flow<List<ReportEntity>> = dao.observeAll()

    fun observeMyReports(): Flow<List<ReportEntity>> =
        dao.observeByUser(session.currentUserId)

    // удалить черновик после успешной отправки
    suspend fun deleteReportById(id: Long): Boolean = try {
        dao.deleteById(id)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    suspend fun rejectReport(reportId: Long): Boolean = try {
        dao.updateStatus(reportId, ReportStatus.REJECTED.name)
        true
    } catch (e: Exception) {
        false
    }

    suspend fun createViolationCard(reportId: Long, userId: Long): Long? = try {
        // защита от повторного создания карточки по одному обращению
        if (violationCardDao.getByReportId(reportId) != null) return null

        val card = ViolationCardEntity(
            report = reportId,
            status = ViolationStatus.UNDER_CONSIDERATION.name,
            verifiedBy = userId
        )
        dao.updateStatus(reportId, ReportStatus.ACCEPTED.name)
        violationCardDao.insert(card)
    } catch (e: Exception) {
        null
    }

    suspend fun getViolationCardById(id: Long): ViolationCard? =
        violationCardDao.getById(id)?.toDomain()

    suspend fun getCardByReportId(reportId: Long): ViolationCard? =
        violationCardDao.getByReportId(reportId)?.toDomain()
}