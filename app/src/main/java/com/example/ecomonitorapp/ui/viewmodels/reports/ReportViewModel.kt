package com.example.ecomonitorapp.ui.viewmodels.reports

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ecomonitorapp.data.managers.GeoLoc
import com.example.ecomonitorapp.data.managers.SessionManager
import com.example.ecomonitorapp.data.repositories.ReportRepository
import com.example.ecomonitorapp.models.Report
import com.example.ecomonitorapp.models.ReportPriority
import com.example.ecomonitorapp.models.ViolationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ReportState {
    data object Idle : ReportState
    data object Loading : ReportState
    data class Success(val report: Report) : ReportState
    data class Error(val message: String) : ReportState
}

data class ReportForm(
    val violationType: ViolationType = ViolationType.NOT_SELECTED,
    val description: String = "",
    val geoLoc: GeoLoc? = null,
    val photo: String? = null,
    val reportPriority: ReportPriority = ReportPriority.NOT_SELECTED,
) {
    val isDescriptionValid: Boolean
        get() = description.trim().length >= 10

    val canSubmit: Boolean
        get() = violationType != ViolationType.NOT_SELECTED &&
                reportPriority != ReportPriority.NOT_SELECTED &&
                isDescriptionValid &&
                geoLoc != null
}

class ReportViewModel(
    private val repository: ReportRepository,
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<ReportState>(ReportState.Idle)
    val stateFlow: StateFlow<ReportState> = _stateFlow.asStateFlow()

    private val _formFlow = MutableStateFlow(ReportForm())
    val formFlow: StateFlow<ReportForm> = _formFlow.asStateFlow()

    fun onViolationTypeChange(raw: String?) {
        _formFlow.update { it.copy(violationType = parseViolationType(raw)) }
    }

    fun onReportPriorityChange(raw: String?) {
        _formFlow.update { it.copy(reportPriority = parseReportPriority(raw)) }
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

    fun submit(onSuccess: () -> Unit = {}) {
        val form = _formFlow.value
        if (!form.canSubmit) return

        viewModelScope.launch {
            _stateFlow.value = ReportState.Loading

            val created = repository.createReport(
                violationType = form.violationType,
                description = form.description.trim(),
                geoLoc = form.geoLoc ?: return@launch,
                photo = form.photo,
                reportPriority = form.reportPriority
            )

            _stateFlow.value = if (created != null) {
                onSuccess()
                ReportState.Success(created)
            } else {
                ReportState.Error("Не удалось создать обращение!")
            }
        }
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