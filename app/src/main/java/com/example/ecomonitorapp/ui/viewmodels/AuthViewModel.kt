package com.example.ecomonitorapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecomonitorapp.data.repositories.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserRepository(application.applicationContext)

    fun register(email: String,
                 firstName: String,
                 middleName: String?,
                 lastName: String,
                 phoneNumber: String,
                 passport: String,
                 passwordHash: String?) {
        viewModelScope.launch {
            val success = repository.registerUser(email, firstName, middleName, lastName,
                phoneNumber, passport, passwordHash)
            // нужно обновить состояние для UI
        }
    }

    fun login(email: String) {
        viewModelScope.launch {
            val user = repository.loginUser(email)
            if(user != null) {
                // успешный вход
            } else {
                // иначе ошибка - пользователь не найден
            }
        }
    }
}