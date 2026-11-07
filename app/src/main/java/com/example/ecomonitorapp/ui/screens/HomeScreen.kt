package com.example.ecomonitorapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ecomonitorapp.models.UserRole

@Composable
fun HomeScreen(
    userRole: UserRole?,
    onOpenAccount: () -> Unit,
    onOpenMap: () -> Unit,
    onOpenAppeal: () -> Unit,
    onOpenListOfAppeals: () -> Unit,
    onOpenMyAppeals: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Главное меню",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Карточки
        HomeCard(
            title = "Личный кабинет",
            description = "Ваши данные, история заявок, настройки",
            icon = Icons.Filled.Person,
            onClick = onOpenAccount,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
        HomeCard(
            title = "Карта города",
            description = "Состояние экологии, точки мониторинга",
            icon = Icons.Filled.Map,
            onClick = onOpenMap,
            color = MaterialTheme.colorScheme.tertiaryContainer
        )
        HomeCard(
            title = "Новая заявка",
            description = "Сообщить об экологическом нарушении",
            icon = Icons.Filled.Add,
            onClick = onOpenAppeal,
            color = MaterialTheme.colorScheme.primaryContainer
        )
        if (userRole == UserRole.MODERATOR) {
            HomeCard(
                title = "Список заявок",
                description = "Просмотреть все поступившие заявки",
                icon = Icons.AutoMirrored.Filled.List,
                onClick = onOpenListOfAppeals,
                color = MaterialTheme.colorScheme.secondaryContainer
            )
        }

        if (userRole == UserRole.APPLICANT) {
            HomeCard(
                title = "Список моих заявок",
                description = "Просмотреть мои отправленные заявки",
                icon = Icons.AutoMirrored.Filled.List,
                onClick = onOpenMyAppeals,
                color = MaterialTheme.colorScheme.secondaryContainer
            )
        }
    }
}

@Composable
private fun HomeCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.Center) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}