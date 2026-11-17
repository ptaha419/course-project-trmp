package com.example.ecomonitorapp.ui.forms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ecomonitorapp.models.CityRepository
import com.example.ecomonitorapp.ui.screens.support.CityDropdown
import com.example.ecomonitorapp.ui.viewmodels.auth.AuthViewModel

@Composable
fun RegisterForm(
    vm: AuthViewModel,
    onRegister: (
        email: String,
        firstName: String,
        middleName: String?,
        lastName: String,
        phoneNumber: String,
        passport: String,
        city: String,
        password: String) -> Unit,
    onToLogin: () -> Unit
) {
    val context = LocalContext.current
    val cities = remember { CityRepository.load(context) }

    // создание и сохранение объектов MutableState для отслеживания изменений их значений
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var passport by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var errMessage by remember {mutableStateOf<String?>(null)}

    val errors by vm.fieldErrors.collectAsStateWithLifecycle()

    // проверка данных перед отправкой
    fun validateAndSubmit() {
        errMessage = null
        if (email.isBlank() || !email.contains("@")) {
            errMessage = "Введите корректный email"
            return
        }

        if (firstName.isBlank()) {
            errMessage = "Имя обязательно"
            return
        }

        val middleName = if (middleName.isBlank()) null else middleName

        if (lastName.isBlank()) {
            errMessage = "Фамилия обязательна"
            return
        }

        if (phoneNumber.isBlank() || phoneNumber.length < 10) {
            errMessage = "Некорректный номер телефона"
            return
        }

        if (passport.isBlank()) {
            errMessage = "Серия и номер паспорта обязательны"
            return
        }

        if (city.isBlank()) {
            errMessage = "Город обязателен"
            return
        }
        if (cities.none { it.name.equals(city.trim(), ignoreCase = true) }) {
            errMessage = "Выберите город из списка"
            return
        }

        if (password.length < 8) {
            errMessage = "Пароль должен быть не короче 8 символов"
            return
        }

        // если все проверки пройдены, то передаём данные в ViewModel
        onRegister(
            email,
            firstName,
            middleName,
            lastName,
            phoneNumber,
            passport,
            city,
            password
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Регистрация", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; vm.clearError("email") },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = errors.email != null,
            supportingText = { errors.email?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = middleName,
            onValueChange = { middleName = it },
            label = { Text("Отчество (необязательно)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Фамилия") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it; vm.clearError("phone") },
            label = { Text("Телефон") },
            modifier = Modifier.fillMaxWidth(),
            isError = errors.phone != null,
            supportingText = { errors.phone?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        OutlinedTextField(
            value = passport,
            onValueChange = { passport = it; vm.clearError("passport") },
            label = { Text("Паспорт (серия и номер)") },
            modifier = Modifier.fillMaxWidth(),
            isError = errors.passport != null,
            supportingText = { errors.passport?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        CityDropdown(
            value = city,
            onValueChange = { city = it },
            cities = cities,
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                validateAndSubmit()
            },
            enabled = true,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Зарегистрироваться")
        }

        Spacer(modifier = Modifier.height(16.dp))  // отступ от кнопки
        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Уже есть аккаунт?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Войти",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onToLogin() }
            )
        }

        errMessage?.let { mes ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mes,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}