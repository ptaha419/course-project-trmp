package com.example.ecomonitorapp.ui.screens.violationCards

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                is ViolationCardState.Success -> CardContent(s.data, Modifier.fillMaxSize())
                ViolationCardState.Idle -> Unit
            }
        }
    }
}

@Composable
private fun CardContent(data: CardWithReport, modifier: Modifier = Modifier) {
    val card = data.card
    val report = data.report

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

    val (statusText, statusColor) = when (card.status) {
        ViolationStatus.LIQUIDATED -> card.status.displayName to MaterialTheme.colorScheme.tertiary
        else -> card.status.displayName to MaterialTheme.colorScheme.onBackground
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Карточка №${card.id}", style = MaterialTheme.typography.titleLarge)

        Row {
            Text("Статус карточки: ")
            Text(text = statusText, color = statusColor)
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