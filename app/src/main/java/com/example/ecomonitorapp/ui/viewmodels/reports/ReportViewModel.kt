package com.example.ecomonitorapp.ui.viewmodels.reports

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ecomonitorapp.data.entities.ReportEntity
import com.example.ecomonitorapp.data.managers.GeoLoc
import com.example.ecomonitorapp.data.managers.SessionManager
import com.example.ecomonitorapp.data.mappers.toPriority
import com.example.ecomonitorapp.data.repositories.ReportRepository
import com.example.ecomonitorapp.models.Report
import com.example.ecomonitorapp.models.ReportPriority
import com.example.ecomonitorapp.models.ReportStatus
import com.example.ecomonitorapp.models.ViolationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ReportState {
    data object Idle : ReportState
    data object Loading : ReportState
    data object DraftSaved : ReportState
    data object Rejected: ReportState
    data object ViolationCardCreated: ReportState
    data class Success(val report: Report) : ReportState
    data class Error(val message: String) : ReportState
}

data class ReportFormState(
    val violationType: ViolationType = ViolationType.NOT_SELECTED,
    val description: String = "",
    val geoLoc: GeoLoc? = null,
    val photo: String? = null,
    val status: ReportStatus = ReportStatus.DRAFT
) {
    val isDescriptionValid: Boolean
        get() = description.trim().length >= 10

    val canSubmit: Boolean
        get() = violationType != ViolationType.NOT_SELECTED &&
                isDescriptionValid &&
                geoLoc != null
}

class ReportViewModel(
    private val repository: ReportRepository,
) : ViewModel() {
    private val _stateFlow = MutableStateFlow<ReportState>(ReportState.Idle)
    val stateFlow: StateFlow<ReportState> = _stateFlow.asStateFlow()

    private val _formFlow = MutableStateFlow(ReportFormState())
    val formFlow: StateFlow<ReportFormState> = _formFlow.asStateFlow()

    // флаг для просмотра обращения
    private val _viewOnly = MutableStateFlow(false)
    val viewOnly: StateFlow<Boolean> = _viewOnly.asStateFlow()

    private val editingDraftId = MutableStateFlow<Long?>(null)

    // флаг для сохранения обращения в черновик
    private val _draftSaved = MutableStateFlow(false)
    val draftSaved = _draftSaved.asStateFlow()

    private val viewingReportId = MutableStateFlow<Long?>(null)
    val loadedReport = MutableStateFlow<Report?>(null)

    fun onViolationTypeChange(raw: String?) {
        _formFlow.update { it.copy(violationType = parseViolationType(raw)) }
    }

    fun onDescriptionChange(value: String) {
        _formFlow.update { it.copy(description = value) }
    }

    fun onGeoLocChange(value: GeoLoc?) {
        _formFlow.update { it.copy(geoLoc = value) }
    }

    fun onPhotoChange(uri: String?) {
        _formFlow.update { it.copy(photo = uri) }
    }

    fun loadReport(id: Long) = viewModelScope.launch {
        val report = repository.getReportById(id) ?: return@launch
        _viewOnly.value = true
        editingDraftId.value = null
        viewingReportId.value = id
        loadedReport.value = report
        onViolationTypeChange(report.violationType.name)
        onDescriptionChange(report.description)
        report.geoLoc?.let { geo -> onGeoLocChange(geo) }
        onPhotoChange(report.photo)
    }

    fun reset() {
        _viewOnly.value = false
        editingDraftId.value = null
        viewingReportId.value = null
        loadedReport.value = null
        _formFlow.value = ReportFormState()
        _stateFlow.value = ReportState.Idle
    }

    fun rejectReport(onSuccess: () -> Unit = {}) = viewModelScope.launch {
        val id = viewingReportId.value ?: return@launch
        _stateFlow.value = ReportState.Loading
        _stateFlow.value = if (repository.rejectReport(id)) {
            onSuccess()
            ReportState.Rejected
        } else {
            ReportState.Error("Не удалось отклонить обращение")
        }
    }

    fun createViolationCard(onSuccess: () -> Unit = {}) = viewModelScope.launch {
        val id = viewingReportId.value ?: return@launch
        val userId = repository.getCurrentUserId()
        if (userId == null) {
            _stateFlow.value = ReportState.Error("Пользователь не авторизован")
            return@launch
        }

        _stateFlow.value = ReportState.Loading
        _stateFlow.value = if (repository.createViolationCard(id, userId) != null) {
            onSuccess()
            ReportState.ViolationCardCreated
        } else {
            ReportState.Error("Не удалось создать карточку нарушения")
        }
    }

    fun saveDraft() = viewModelScope.launch {
        val form = _formFlow.value
        val newId = repository.upsertDraft(
            id = editingDraftId.value,
            violationType = form.violationType,
            description = form.description,
            geoLoc = form.geoLoc,
            photo = form.photo,
            reportPriority = form.violationType.toPriority()
        )
        if (editingDraftId.value == null && newId != null) {
            editingDraftId.value = newId
        }
        _stateFlow.value = ReportState.DraftSaved
        _draftSaved.value = true
    }

    fun loadDraft(id: Long) = viewModelScope.launch {
        val draft = repository.getDraftById(id) ?: return@launch
        editingDraftId.value = draft.id
        onViolationTypeChange(draft.violationType)
        onDescriptionChange(draft.description)
        val lat = draft.latitude
        val lng = draft.longitude
        if (lat != null && lng != null) onGeoLocChange(GeoLoc(lat, lng))
        onPhotoChange(draft.photo)
    }

    fun submit(onSuccess: () -> Unit = {}) {
        val form = _formFlow.value
        if (!form.canSubmit) return

        viewModelScope.launch {
            _stateFlow.value = ReportState.Loading
            val geo = form.geoLoc
            if (geo == null) {
                _stateFlow.value = ReportState.Error("Укажите местоположение")
                return@launch
            }

            val created = repository.createReport(
                violationType = form.violationType,
                description = form.description.trim(),
                geoLoc = geo,
                photo = form.photo,
                reportPriority = form.violationType.toPriority()
            )

            _stateFlow.value = if (created != null) {
                editingDraftId.value?.let { repository.deleteReportById(it) }
                editingDraftId.value = null
                onSuccess()
                ReportState.Success(created)
            } else {
                ReportState.Error("Не удалось создать обращение!")
            }
        }
    }

    fun resetDraftSaved() {
        _draftSaved.value = false
    }

    companion object {
        fun factory(context: Context) = viewModelFactory {
            initializer {
                ReportViewModel(ReportRepository(context, SessionManager(context)))
            }
        }
    }
}

private fun parseViolationType(raw: String?): ViolationType =
    runCatching {
        ViolationType.valueOf(raw ?: return ViolationType.NOT_SELECTED)
    }.getOrDefault(ViolationType.NOT_SELECTED)

private fun parseReportPriority(raw: String?): ReportPriority =
    runCatching {
        ReportPriority.valueOf(raw ?: return ReportPriority.NOT_SELECTED)
    }.getOrDefault(ReportPriority.NOT_SELECTED)