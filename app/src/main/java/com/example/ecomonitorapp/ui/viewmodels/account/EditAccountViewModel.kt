package com.example.ecomonitorapp.ui.viewmodels.account

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ecomonitorapp.data.mapper.toEntity
import com.example.ecomonitorapp.data.mapper.toUser
import com.example.ecomonitorapp.data.repositories.UserRepository
import com.example.ecomonitorapp.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditAccountState(
    val original: User? = null,
    val firstName: String = "",
    val middleName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
) {
    val hasChanges: Boolean
        get() = original != null && (original.firstName != firstName ||
                original.middleName != middleName.trim().ifBlank { null } ||
                original.lastName != lastName || original.email != email ||
                original.phoneNumber != phoneNumber)


    val isFirstNameValid: Boolean get() = firstName.trim().length >= 2
    val isMiddleNameValid: Boolean get() = middleName.isBlank() || middleName.trim().length >= 2
    val isLastNameValid: Boolean get() = lastName.trim().length >= 2

    val isEmailValid: Boolean get() = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val canSave: Boolean get() = isFirstNameValid && isMiddleNameValid && isLastNameValid &&
            isEmailValid && hasChanges && !isSaving
}

class EditAccountViewModel(private val repository: UserRepository) : ViewModel() {
    private val _stateFlow = MutableStateFlow(EditAccountState())
    val stateFlow: StateFlow<EditAccountState> = _stateFlow.asStateFlow()

    fun loadUser(id: Int) {
        viewModelScope.launch {
            val user = repository.getUserById(id)?.toUser()
            _stateFlow.value = if (user != null) EditAccountState(
                original = user,
                firstName = user.firstName,
                middleName = user.middleName.orEmpty(),
                lastName = user.lastName,
                email = user.email,
                phoneNumber = user.phoneNumber,
            ) else _stateFlow.value.copy(error = "Профиль не найден")
        }
    }

    fun onFirstNameChange(value: String) { _stateFlow.update { it.copy(firstName = value, error = null) } }
    fun onMiddleNameChange(value: String) { _stateFlow.update { it.copy(middleName = value, error = null) } }
    fun onLastNameChange(value: String) { _stateFlow.update { it.copy(lastName = value, error = null) } }
    fun onEmailChange(value: String) { _stateFlow.update { it.copy(email = value, error = null) } }
    fun onPhoneChange(value: String) { _stateFlow.update { it.copy(phoneNumber = value, error = null) } }

    fun save(onSuccess: () -> Unit) {
        val state = _stateFlow.value
        val user = state.original ?: return
        if (!state.canSave) return

        viewModelScope.launch {
            _stateFlow.update { it.copy(isSaving = true, error = null) }
            try {
                val updated = user.copy(
                    firstName = state.firstName.trim(),
                    middleName = state.middleName.trim().ifBlank { null },
                    lastName = state.lastName.trim(),
                    email = state.email.trim(),
                    phoneNumber = state.phoneNumber.trim())
                val entity = repository.getUserById(user.id)
                if (entity != null) {
                    repository.updateUser(updated.toEntity(existing = entity))
                    _stateFlow.update { it.copy(isSaving = false, original = updated) }
                    onSuccess()
                } else {
                    _stateFlow.update { it.copy(isSaving = false, error = "Аккаунт не найден") }
                }
            } catch (e: Exception) {
                _stateFlow.update { it.copy(isSaving = false, error = "Не удалось сохранить: ${e.message}") }
            }
        }
    }

    fun reset() { // откат к original при выходе без сохранения
        _stateFlow.update { state ->
            state.original?.let {
                state.copy(
                    firstName = it.firstName,
                    middleName = it.middleName.orEmpty(),
                    lastName = it.lastName,
                    email = it.email,
                    phoneNumber = it.phoneNumber)
            } ?: state
        }
    }

    companion object {
        fun factory(context: Context) = viewModelFactory {
            initializer { EditAccountViewModel(UserRepository(context)) }
        }
    }
}