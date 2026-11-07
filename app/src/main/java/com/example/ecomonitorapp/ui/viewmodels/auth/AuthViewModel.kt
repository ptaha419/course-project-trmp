package com.example.ecomonitorapp.ui.viewmodels.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecomonitorapp.data.repositories.UserRepository
import com.example.ecomonitorapp.models.User
import com.example.ecomonitorapp.models.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

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
    private val _stateFlow = MutableStateFlow<AuthState>(AuthState.Idle)
    val stateFlow = _stateFlow.asStateFlow()

    fun register(email: String, firstName: String, middleName: String?, lastName: String,
                 phoneNumber: String, passport: String, password: String) {
        viewModelScope.launch {
            _stateFlow.value = AuthState.Loading
            val passwordHash = hashPassword(password)

            // получаем существующего пользователя
            val registeredUser = repository.registerUser(
                email, firstName, middleName, lastName, phoneNumber, passport, passwordHash
            )

            if (registeredUser != null) {
                val domainUser = User(
                    id = registeredUser.id!!, // используем реальный id, а не null
                    email = registeredUser.email,
                    firstName = registeredUser.firstName,
                    middleName = registeredUser.middleName,
                    lastName = registeredUser.lastName,
                    phoneNumber = registeredUser.phoneNumber,
                    passport = registeredUser.passport,
                    role = parseRole(registeredUser.role)
                )
                _stateFlow.value = AuthState.Success(domainUser)
            } else {
                _stateFlow.value = AuthState.Error("Не удалось зарегистрировать пользователя!")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _stateFlow.value = AuthState.Loading
            val storedUser = repository.loginUser(email)

            if (storedUser != null && storedUser.passwordHash != null) {
                val inputHash = hashPassword(password)
                if (inputHash == storedUser.passwordHash) {
                    val domainUser = User(
                        id = storedUser.id!!,
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
                _stateFlow.value = AuthState.Error("Пользователь не найден или ошибка данных!")
            }
        }
    }

    fun reset() {
        _stateFlow.value = AuthState.Idle
    }
}

// функция для хэширования пароля методом SHA-256
private fun hashPassword(password: String): String {
    return try {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        bytes.joinToString("") { "%02x".format(it) }
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
        ""
    }
}

// парсинг типа данных роли
private fun parseRole(roleString: String) =
    UserRole.values().find { it.name == roleString } ?: UserRole.APPLICANT