package com.example.ecomonitorapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
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
    onOpenListOfViolationCards: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HomeCard(
            title = "Новое обращение",
            description = "Сообщить об экологическом нарушении",
            icon = Icons.Filled.Add,
            onClick = onCreateReport,
            color = MaterialTheme.colorScheme.primaryContainer
        )

        HomeCard(
            title = "Мои обращения",
            description = "Просмотреть мои отправленные обращения",
            icon = Icons.AutoMirrored.Filled.List,
            onClick = onOpenMyReports,
            color = MaterialTheme.colorScheme.secondaryContainer
        )

        if (userRole == UserRole.MODERATOR) {
            HomeCard(
                title = "Обращения",
                description = "Просмотреть все поступившие обращения",
                icon = Icons.Filled.Layers,
                onClick = onOpenListOfReports,
                color = MaterialTheme.colorScheme.secondaryContainer
            )
        }

        if(userRole == UserRole.MODERATOR || userRole == UserRole.INSPECTOR) {
            HomeCard(
                title = "Карточки нарушений",
                description = "Просмотреть созданные карточки нарушений",
                icon = Icons.Filled.Folder,
                onClick = onOpenListOfViolationCards,
                color = MaterialTheme.colorScheme.errorContainer
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
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = LocalContentColor.current.copy(alpha = 0.4f)
            )
        }
    }
}