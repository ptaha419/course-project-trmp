package com.example.ecomonitorapp.ui.viewmodels.violationCards

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ecomonitorapp.data.managers.SessionManager
import com.example.ecomonitorapp.data.repositories.ReportRepository
import com.example.ecomonitorapp.data.repositories.ViolationCardRepository
import com.example.ecomonitorapp.models.ViolationCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ViolationCardState {
    data object Idle : ViolationCardState
    data object Loading : ViolationCardState
    data class Success(val violationCard: ViolationCard) : ViolationCardState
    data object Error : ViolationCardState
}

class ViolationCardViewModel(
    private val repository: ReportRepository,
) : ViewModel() {
    private val _stateFlow = MutableStateFlow<ViolationCardState>(ViolationCardState.Idle)
    val stateFlow: StateFlow<ViolationCardState> = _stateFlow.asStateFlow()

    fun loadCard(id: Long) = viewModelScope.launch {
        _stateFlow.value = ViolationCardState.Loading
        val card = repository.getViolationCardById(id)
        _stateFlow.value = if (card != null) ViolationCardState.Success(card) else ViolationCardState.Error
    }

    fun reset() {
        _stateFlow.value = ViolationCardState.Idle
    }

    companion object {
        fun factory(context: Context) = viewModelFactory {
            initializer {
                ViolationCardViewModel(ReportRepository(context, SessionManager(context)))
            }
        }
    }
}