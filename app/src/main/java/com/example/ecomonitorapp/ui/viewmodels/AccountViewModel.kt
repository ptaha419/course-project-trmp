package com.example.ecomonitorapp.ui.viewmodels

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
import kotlinx.coroutines.launch

class AccountViewModel(private val repository: UserRepository) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    fun loadUser(id: Int) {
        viewModelScope.launch {
            _user.value = repository.getUserById(id)?.toUser()
        }
    }

    fun saveUser(user: User, onDone: () -> Unit) {
        viewModelScope.launch {
            val entity = repository.getUserById(user.id)
            if (entity != null) {
                repository.updateUser(user.toEntity(existing = entity))
                _user.value = user
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