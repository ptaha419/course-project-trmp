package com.example.ecomonitorapp.ui.viewmodels.account

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ecomonitorapp.data.mappers.toEntity
import com.example.ecomonitorapp.data.mappers.toUser
import com.example.ecomonitorapp.data.repositories.UserRepository
import com.example.ecomonitorapp.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AccountState {
    data object Loading : AccountState
    data class Loaded(val user: User) : AccountState
    data object NotFound : AccountState
}

class AccountViewModel(private val repository: UserRepository) : ViewModel() {
    private val _stateFlow = MutableStateFlow<AccountState>(AccountState.Loading)
    val stateFlow: StateFlow<AccountState> = _stateFlow.asStateFlow()

    fun loadUser(id: Long) {
        viewModelScope.launch {
            _stateFlow.value = AccountState.Loading
            val found = repository.getUserById(id)?.toUser()
            _stateFlow.value = if (found != null) AccountState.Loaded(found)
            else AccountState.NotFound
        }
    }

    fun saveUser(user: User, onDone: () -> Unit) {
        viewModelScope.launch {
            val entity = repository.getUserById(user.id)
            if (entity != null) {
                repository.updateUser(user.toEntity(existing = entity))
                _stateFlow.value = AccountState.Loaded(user)
            }
            onDone()
        }
    }

    companion object {
        fun factory(context: Context) = viewModelFactory {
            initializer {
                AccountViewModel(UserRepository(context))
            }
        }
    }
}