package com.example.ecomonitorapp.ui.screens.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ecomonitorapp.models.User
import com.example.ecomonitorapp.models.UserRole
import com.example.ecomonitorapp.ui.viewmodels.account.AccountState
import com.example.ecomonitorapp.ui.viewmodels.account.AccountViewModel

@Composable
fun AccountScreen(
    vm: AccountViewModel,
    userId: Int,
    onEditClick: () -> Unit
) {
    LaunchedEffect(userId) {
        vm.loadUser(userId)
    }

    val state by vm.stateFlow.collectAsStateWithLifecycle()

    when (val s = state) {
        AccountState.Loading -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }

        is AccountState.Loaded -> AccountContent(user = s.user, onEditClick = onEditClick)

        AccountState.NotFound -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Профиль не найден")
        }
    }
}

@Composable
private fun AccountContent(user: User, onEditClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Личный кабинет",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        InfoRow("Фамилия", user.lastName)
        InfoRow("Имя", user.firstName)
        user.middleName?.takeIf { it.isNotBlank() }?.let {
            InfoRow("Отчество", it)
        }
        InfoRow("Email", user.email)
        InfoRow("Телефон", user.phoneNumber)
        InfoRow("Паспорт", user.passport)
        InfoRow("Роль", when (user.role) {
            UserRole.APPLICANT -> "Заявитель"
            UserRole.MODERATOR -> "Модератор"
            UserRole.INSPECTOR -> "Инспектор"
        })

        Button(
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Изменить данные")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}