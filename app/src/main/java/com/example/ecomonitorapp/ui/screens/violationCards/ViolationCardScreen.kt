package com.example.ecomonitorapp.ui.screens.violationCards

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ecomonitorapp.models.ViolationCard
import com.example.ecomonitorapp.models.ViolationStatus
import com.example.ecomonitorapp.ui.viewmodels.violationCards.CardWithReport
import com.example.ecomonitorapp.ui.viewmodels.violationCards.ViolationCardState
import com.example.ecomonitorapp.ui.viewmodels.violationCards.ViolationCardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.format
import java.io.IOException
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViolationCardScreen(
    vm: ViolationCardViewModel,
    cardId: Long,
    onBack: () -> Unit,
) {
    LaunchedEffect(cardId) { vm.loadCard(cardId) }
    DisposableEffect(Unit) { onDispose { vm.reset() } }

    val state by vm.stateFlow.collectAsStateWithLifecycle()
    val isInspector by vm.isInspector.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Назад") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            when (val s = state) {
                is ViolationCardState.Loading -> CircularProgressIndicator()
                is ViolationCardState.Error -> Text("Карточка не найдена")
                is ViolationCardState.Success -> CardContent(
                    s.data,
                    Modifier.fillMaxSize(),
                    isInspector = isInspector,
                    onStatusChange = { newStatus -> vm.updateStatus(cardId, newStatus) },
                    onSendToInspector = { vm.sendToInspector(cardId) }
                )
                ViolationCardState.Idle -> Unit
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardContent(
    data: CardWithReport,
    modifier: Modifier = Modifier,
    isInspector: Boolean,
    onSendToInspector: () -> Unit = {},
    onStatusChange: (ViolationStatus) -> Unit = {}
    ) {
    val card = data.card
    val report = data.report
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val bitmap = remember(report.photo) { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(report.photo) {
        if (report.photo != null) {
            withContext(Dispatchers.IO) {
                try {
                    bitmap.value = if (report.photo.startsWith("content://")) {
                        // на всякий случай: старые записи с URI пикера
                        context.contentResolver.openInputStream(Uri.parse(report.photo))
                            ?.use { BitmapFactory.decodeStream(it) }
                    } else {
                        BitmapFactory.decodeFile(report.photo)
                    }
                } catch (e: SecurityException) {
                    bitmap.value = null
                } catch (e: IOException) {
                    bitmap.value = null
                }
            }
        }
    }

    val statusText = card.status.displayName
    val statusColor = statusColorFor(card.status)

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Карточка №${card.id}", style = MaterialTheme.typography.titleLarge)

        val canChangeStatus = isInspector && card.status != ViolationStatus.LIQUIDATED

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Статус карточки:")

            Box {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.15f),
                    contentColor = statusColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .then(
                            if (canChangeStatus) {
                                Modifier.clickable { expanded = true }
                            } else Modifier
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(statusText, style = MaterialTheme.typography.labelLarge)
                        if (canChangeStatus) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Изменить статус",
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .rotate(if (expanded) 180f else 0f)
                            )
                        }
                    }
                }

                DropdownMenu(
                    expanded = expanded && canChangeStatus,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf(
                        ViolationStatus.INSPECTOR_APPOINTED,
                        ViolationStatus.LIQUIDATION_PROCESS,
                        ViolationStatus.LIQUIDATED
                    ).forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.displayName) },
                            enabled = status != card.status,
                            onClick = {
                                expanded = false
                                onStatusChange(status)
                            }
                        )
                    }
                }
            }
        }

        // кнопка для модератора и только пока карточка на рассмотрении
        if (!isInspector && card.status == ViolationStatus.UNDER_CONSIDERATION) {
            Button(
                onClick = onSendToInspector,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Направить инспектору")
            }
        }

        HorizontalDivider()

        // данные обращения
        Text("Обращение №${report.id}", style = MaterialTheme.typography.titleMedium)
        Text("Тип нарушения: ${report.violationType.displayName}")
        Text("Приоритет: ${report.reportPriority.displayName}")
        Text("Описание: ${report.description}")

        report.geoLoc?.let {
            Text("Местоположение: ${it.latitude}, ${it.longitude}")
        }

        val img = bitmap.value
        if (report.photo != null) {
            if (img != null) {
                Image(
                    bitmap = img.asImageBitmap(),
                    contentDescription = "Фото нарушения",
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text("Фото недоступно", style = MaterialTheme.typography.bodySmall)
            }
        }

        val verifierName = data.verifiedBy?.let {
            listOfNotNull(it.lastName, it.firstName, it.middleName)
                .joinToString(" ")
                .ifBlank { "неизвестен" }
        } ?: "неизвестен"

        Text("Проверил: $verifierName", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun statusColorFor(status: ViolationStatus): Color = when (status) {
    ViolationStatus.UNDER_CONSIDERATION -> MaterialTheme.colorScheme.secondary
    ViolationStatus.SENT_TO_INSPECTOR -> MaterialTheme.colorScheme.primary
    ViolationStatus.INSPECTOR_APPOINTED -> MaterialTheme.colorScheme.primary
    ViolationStatus.LIQUIDATION_PROCESS -> MaterialTheme.colorScheme.error
    ViolationStatus.LIQUIDATED -> MaterialTheme.colorScheme.tertiary
}