package com.example.ecomonitorapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecomonitorapp.data.repositories.UserRepository
import com.example.ecomonitorapp.models.User
import com.example.ecomonitorapp.models.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

// класс с готовыми состояниями аутентификации,
// которые изменяются в течение работы приложения
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

// инициализация ViewModel и StateFlow
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserRepository(application.applicationContext)

    // создание объекта StateFlow с нач. значением Idle
    private val _stateFlow = MutableStateFlow<AuthState> (AuthState.Idle)
    val stateFlow = _stateFlow.asStateFlow()

    fun register(email: String,
                 firstName: String,
                 middleName: String?,
                 lastName: String,
                 phoneNumber: String,
                 passport: String,
                 password: String) {
        viewModelScope.launch {
            // меняем состояние, идёт процесс регистрации
            _stateFlow.value = AuthState.Loading

            val passwordHash = hashPassword(password)

            val success = repository.registerUser(email, firstName, middleName, lastName,
                phoneNumber, passport, passwordHash)

            if(success) {
                // нужно изменить состояние на успешное
                _stateFlow.value = AuthState.Success(
                    User(
                        id = null,
                        email = email,
                        firstName = firstName,
                        middleName = middleName,
                        lastName = lastName,
                        phoneNumber = phoneNumber,
                        passport = passport,
                        role = UserRole.APPLICANT
                    )
                )
            } else {
                _stateFlow.value = AuthState.Error("Не удалось зарегистрировать пользователя в системе!")
            }

        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _stateFlow.value = AuthState.Loading

            val storedUser = repository.loginUser(email)

            // пользователь существует
            if(storedUser != null) {
                val inputHash = hashPassword(password)
                if(inputHash == storedUser.passwordHash) {
                    val domainUser = User(
                        id = storedUser.id,
                        email = storedUser.email,
                        firstName = storedUser.firstName,
                        middleName = storedUser.middleName,
                        lastName = storedUser.lastName,
                        phoneNumber = storedUser.phoneNumber,
                        passport = storedUser.passport,
                        role = parseRole(storedUser.role)
                    )
                    _stateFlow.value = AuthState.Success(domainUser)
                } else {
                _stateFlow.value = AuthState.Error("Неверный пароль!")
                }
            } else {
                // иначе ошибка - пользователь не найден
                _stateFlow.value = AuthState.Error("Пользователь не найден!")
                }
        }
    }
}

// функция для хэширования пароля методом SHA-256
private fun hashPassword(password: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

// парсинг типа данных роли
private fun parseRole(roleString: String) =
    UserRole.values().find { it.name == roleString } ?: UserRole.APPLICANT