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

data class RegisterFieldErrors(
    val email: String? = null,
    val phone: String? = null,
    val password: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val passport: String? = null,
    val city: String? = null
)

// инициализация ViewModel и StateFlow
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserRepository(application.applicationContext)
    private val _stateFlow = MutableStateFlow<AuthState>(AuthState.Idle)
    val stateFlow = _stateFlow.asStateFlow()

    private val _fieldErrors = MutableStateFlow(RegisterFieldErrors())
    val fieldErrors = _fieldErrors.asStateFlow()

    fun register(
        email: String,
        firstName: String,
        middleName: String?,
        lastName: String,
        phoneNumber: String,
        passport: String,
        city: String,
        password: String
    ) {
        viewModelScope.launch {
            if (!validateFields(email, firstName, lastName, phoneNumber, passport, city, password)) return@launch

            val normalizedEmail = email.trim().lowercase()
            val normalizedPhone = phoneNumber.filter { it.isDigit() || it == '+' }

            _stateFlow.value = AuthState.Loading

            val emailTaken = repository.isEmailTaken(normalizedEmail)
            val phoneTaken = repository.isPhoneTaken(normalizedPhone)

            if (emailTaken || phoneTaken) {
                _fieldErrors.value = _fieldErrors.value.copy(
                    email = if (emailTaken) "Аккаунт с такой почтой уже зарегистрирован" else null,
                    phone = if (phoneTaken) "Этот номер телефона уже используется" else null
                )
                _stateFlow.value = AuthState.Idle
                return@launch
            }

            val passwordHash = hashPassword(password)
            val registeredUser = repository.registerUser(
                normalizedEmail, firstName, middleName, lastName,
                normalizedPhone, passport, city, passwordHash
            )

            if (registeredUser != null) {
                val domainUser = User(
                    id = registeredUser.id!!,
                    email = registeredUser.email,
                    firstName = registeredUser.firstName,
                    middleName = registeredUser.middleName,
                    lastName = registeredUser.lastName,
                    phoneNumber = registeredUser.phoneNumber,
                    passport = registeredUser.passport,
                    city = registeredUser.city,
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
                        city = storedUser.city,
                        role = parseRole(storedUser.role)
                    )
                    repository.saveCurrentUserId(storedUser.id!!)
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

    fun validateFields(
        email: String, firstName: String, lastName: String,
        phoneNumber: String, passport: String, city: String, password: String
    ): Boolean {
        val errors = RegisterFieldErrors(
            email = when {
                email.isBlank() -> "Введите email"
                !email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) -> "Некорректный email"
                else -> null
            },
            phone = when {
                phoneNumber.isBlank() -> "Введите номер телефона"
                !Regex("^\\+?[78]?\\d{10}$").matches(phoneNumber.filter { it.isDigit() || it == '+' }) -> "Некорректный номер"
                else -> null
            },
            password = when {
                password.length < 6 -> "Минимум 6 символов"
                else -> null
            },
            firstName = if (firstName.isBlank()) "Введите имя" else null,
            lastName = if (lastName.isBlank()) "Введите фамилию" else null,
            passport = if (passport.isBlank()) "Введите данные паспорта" else null,
            city = if (city.isBlank()) "Введите город" else null
        )
        _fieldErrors.value = errors
        return errors.email == null && errors.phone == null &&
                errors.password == null && errors.firstName == null &&
                errors.lastName == null && errors.passport == null && errors.city == null
    }

    fun clearError(field: String) {
        val current = _fieldErrors.value
        _fieldErrors.value = when (field) {
            "email" -> current.copy(email = null)
            "phone" -> current.copy(phone = null)
            "passport" -> current.copy(passport = null)
            else -> current
        }
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