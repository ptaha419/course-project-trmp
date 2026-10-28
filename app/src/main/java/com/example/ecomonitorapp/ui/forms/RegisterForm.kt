package com.example.ecomonitorapp.ui.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun RegisterForm(
    onRegister: (
        email: String,
        firstName: String,
        middleName: String?,
        lastName: String,
        phoneNumber: String,
        passport: String,
        password: String) -> Unit
) {
    // создание и сохранение объектов MutableState для отслеживания изменений их значений
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var passport by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var errMessage by remember {mutableStateOf<String?>(null)}

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
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
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
            onValueChange = { phoneNumber = it },
            label = { Text("Телефон") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        OutlinedTextField(
            value = passport,
            onValueChange = { passport = it },
            label = { Text("Паспорт (серия и номер)") },
            modifier = Modifier.fillMaxWidth()
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