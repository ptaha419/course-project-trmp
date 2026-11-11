package com.example.ecomonitorapp.ui.screens.violationCards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ecomonitorapp.models.ViolationCard
import com.example.ecomonitorapp.models.ViolationStatus
import com.example.ecomonitorapp.ui.viewmodels.violationCards.ViolationCardState
import com.example.ecomonitorapp.ui.viewmodels.violationCards.ViolationCardViewModel
import kotlinx.datetime.format
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
                is ViolationCardState.Success -> CardContent(s.violationCard, Modifier.fillMaxSize())
                ViolationCardState.Idle -> Unit
            }
        }
    }
}

@Composable
private fun CardContent(card: ViolationCard, modifier: Modifier = Modifier) {
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
            Text("Статус: ")
            Text(text = statusText, color = statusColor)
        }

        Text(
            "Проверил: пользователь №${card.verifiedBy}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}