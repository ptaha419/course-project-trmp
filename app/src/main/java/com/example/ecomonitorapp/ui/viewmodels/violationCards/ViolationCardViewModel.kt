package com.example.ecomonitorapp.ui.viewmodels.violationCards

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ecomonitorapp.data.managers.SessionManager
import com.example.ecomonitorapp.data.repositories.ReportRepository
import com.example.ecomonitorapp.data.repositories.ViolationCardRepository
import com.example.ecomonitorapp.models.Report
import com.example.ecomonitorapp.models.ReportStatus
import com.example.ecomonitorapp.models.User
import com.example.ecomonitorapp.models.ViolationCard
import com.example.ecomonitorapp.models.ViolationStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CardWithReport(
    val card: ViolationCard,
    val report: Report,
    val verifiedBy: User?,
)

sealed interface ViolationCardState {
    data object Idle : ViolationCardState
    data object Loading : ViolationCardState
    data class Success(val data: CardWithReport) : ViolationCardState
    data object Error : ViolationCardState
}

class ViolationCardViewModel(
    private val reportRepository: ReportRepository,
    private val cardRepository: ViolationCardRepository
) : ViewModel() {
    private val _stateFlow = MutableStateFlow<ViolationCardState>(ViolationCardState.Idle)
    val stateFlow: StateFlow<ViolationCardState> = _stateFlow.asStateFlow()

    private val _isInspector = MutableStateFlow(false)
    val isInspector: StateFlow<Boolean> = _isInspector.asStateFlow()

    fun loadCard(id: Long) = viewModelScope.launch {
        _stateFlow.value = ViolationCardState.Loading
        _isInspector.value = cardRepository.isCurrentUserInspector()

        val card = reportRepository.getViolationCardById(id)
        _stateFlow.value = if (card != null) {
            val report = reportRepository.getReportById(card.report)
            if (report != null) {
                val verifier = reportRepository.getUserById(card.verifiedBy)
                ViolationCardState.Success(CardWithReport(card, report, verifier))
            } else {
                ViolationCardState.Error
            }
        } else ViolationCardState.Error
    }

    fun reset() {
        _stateFlow.value = ViolationCardState.Idle
    }

    // кнопка отправки инспектору — только из статуса «На рассмотрении»
    fun sendToInspector(cardId: Long) = viewModelScope.launch {
        val current = _stateFlow.value
        if (current is ViolationCardState.Success &&
            current.data.card.status == ViolationStatus.UNDER_CONSIDERATION
        ) {
            cardRepository.updateStatus(cardId, ViolationStatus.SENT_TO_INSPECTOR)
            loadCard(cardId)
        }
    }

    // статусы INSPECTOR_APPOINTED / LIQUIDATION_PROCESS / LIQUIDATED выставляет ТОЛЬКО инспектор
    fun updateStatus(cardId: Long, newStatus: ViolationStatus) = viewModelScope.launch {
        val current = _stateFlow.value
        if (current !is ViolationCardState.Success) return@launch
        val card = current.data.card
        if (current.data.card.status == ViolationStatus.LIQUIDATED) return@launch

        // назначать инспектора / вести ликвидацию может только инспектор
        if (!_isInspector.value) return@launch

        cardRepository.updateStatus(cardId, newStatus)
        if (newStatus == ViolationStatus.LIQUIDATED) {
            reportRepository.updateReportStatus(card.report, ReportStatus.DONE)
        }
        loadCard(cardId)
    }

    companion object {
        fun factory(context: Context) = viewModelFactory {
            initializer {
                val session = SessionManager(context)
                val reportRepo = ReportRepository(context, session)
                ViolationCardViewModel(
                    reportRepo,
                    ViolationCardRepository(context, reportRepo, session)
                )
            }
        }
    }
}