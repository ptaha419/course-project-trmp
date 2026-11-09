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
    modifier: Modifier = Modifier,
    userRole: UserRole?,
    onOpenAccount: () -> Unit,
    onOpenMap: () -> Unit,
    onCreateReport: () -> Unit,
    onOpenListOfReports: () -> Unit,
    onOpenMyReports: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Карточки
//        HomeCard(
//            title = "Личный кабинет",
//            description = "Ваши данные, история заявок, настройки",
//            icon = Icons.Filled.Person,
//            onClick = onOpenAccount,
//            color = MaterialTheme.colorScheme.surfaceVariant
//        )
        HomeCard(
            title = "Новое обращение",
            description = "Сообщить об экологическом нарушении",
            icon = Icons.Filled.Add,
            onClick = onCreateReport,
            color = MaterialTheme.colorScheme.primaryContainer
        )
        if (userRole == UserRole.MODERATOR) {
            HomeCard(
                title = "Список обращений",
                description = "Просмотреть все поступившие обращения",
                icon = Icons.AutoMirrored.Filled.List,
                onClick = onOpenListOfReports,
                color = MaterialTheme.colorScheme.secondaryContainer
            )
        }

        if (userRole == UserRole.APPLICANT) {
            HomeCard(
                title = "Список моих обращений",
                description = "Просмотреть мои отправленные обращения",
                icon = Icons.AutoMirrored.Filled.List,
                onClick = onOpenMyReports,
                color = MaterialTheme.colorScheme.secondaryContainer
            )
        }

        HomeCard(
            title = "Карта города",
            description = "Состояние экологии, точки мониторинга",
            icon = Icons.Filled.Map,
            onClick = onOpenMap,
            color = MaterialTheme.colorScheme.tertiaryContainer
        )
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