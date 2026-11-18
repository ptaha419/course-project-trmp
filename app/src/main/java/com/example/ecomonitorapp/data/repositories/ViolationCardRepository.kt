package com.example.ecomonitorapp.data.repositories

import android.content.Context
import com.example.ecomonitorapp.data.dao.ReportDao
import com.example.ecomonitorapp.data.dao.ViolationCardDao
import com.example.ecomonitorapp.data.db.DbProvider
import com.example.ecomonitorapp.data.entities.ReportEntity
import com.example.ecomonitorapp.data.entities.ViolationCardEntity
import com.example.ecomonitorapp.data.managers.SessionManager
import com.example.ecomonitorapp.models.ReportStatus
import com.example.ecomonitorapp.models.User
import com.example.ecomonitorapp.models.UserRole
import com.example.ecomonitorapp.models.ViolationStatus
import com.example.ecomonitorapp.ui.screens.violationCards.CardWithReportRow
import kotlinx.coroutines.flow.Flow

class ViolationCardRepository(
    private val context: Context,
    private val reportRepository: ReportRepository,
    private val session: SessionManager)
{
    private val dao: ViolationCardDao = DbProvider.getDatabase(context).violationCardDao()

    suspend fun getViolationCardById(id: Long): ViolationCardEntity? = dao.getById(id)

    val currentUserId: Long?
        get() = session.getCurrentUserIdOrNull()

    suspend fun getCurrentUser(): User? =
        currentUserId?.let { reportRepository.getUserById(it) }

    suspend fun isCurrentUserInspector(): Boolean =
        getCurrentUser()?.role == UserRole.INSPECTOR

    suspend fun updateStatus(cardId: Long, newStatus: ViolationStatus) {
        val card = dao.getById(cardId) ?: return
        if (card.status == ViolationStatus.LIQUIDATED.name) return

        dao.update(card.copy(status = newStatus.name))
    }
}