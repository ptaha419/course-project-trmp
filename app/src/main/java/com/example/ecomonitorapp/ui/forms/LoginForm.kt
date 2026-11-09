package com.example.ecomonitorapp.ui.forms

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun LoginForm(
    onLogin: (email: String, password: String) -> Unit,
    onToRegister: () -> Unit
    ) {
    val context = LocalContext.current
    // создание и сохранение объектов MutableState для отслеживания изменений их значений
    val prefs = remember { context.getSharedPreferences("auth", Context.MODE_PRIVATE) }

    var email by remember { mutableStateOf(prefs.getString("saved_login", "") ?: "") }
    var password by remember { mutableStateOf(prefs.getString("saved_password", "") ?: "") }

    // переменная для "запоминания" пользователя при следующем входе
    var rememberMe by remember { mutableStateOf(prefs.getBoolean("remember", false)) }

    var errMessage by remember {mutableStateOf<String?>(null)}

    // проверка данных перед отправкой
    fun validateAndSubmit() {
        errMessage = null
        if (email.isBlank() || !email.contains("@")) {
            errMessage = "Введите корректный email"
            return
        }

        if (password.length < 8) {
            errMessage = "Пароль должен быть не короче 8 символов"
            return
        }

        if (rememberMe) {
            prefs.edit()
                .putBoolean("remember", true)
                .putString("saved_login", email)
                .putString("saved_password", password)
                .apply()
        } else {
            prefs.edit().clear().apply() // или удалить только эти ключи
        }

        // если все проверки пройдены, то передаём данные в ViewModel
        onLogin(email, password)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Вход в систему", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            singleLine = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { rememberMe = !rememberMe },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
            Text("Запомнить меня")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { validateAndSubmit() },
            enabled = true,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Войти")
        }

        Spacer(modifier = Modifier.height(16.dp))  // отступ от кнопки
        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Новый пользователь?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Зарегистрироваться",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onToRegister() }
            )
        }

        errMessage?.let { msg ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}