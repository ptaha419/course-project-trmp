package com.example.ecomonitorapp.ui.screens.violationCards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Embedded
import androidx.room.Relation
import com.example.ecomonitorapp.data.entities.ReportEntity
import com.example.ecomonitorapp.data.entities.ViolationCardEntity
import com.example.ecomonitorapp.data.managers.SessionManager
import com.example.ecomonitorapp.data.repositories.ReportRepository
import com.example.ecomonitorapp.data.repositories.ViolationCardRepository
import com.example.ecomonitorapp.models.ReportPriority
import com.example.ecomonitorapp.models.ViolationStatus
import com.example.ecomonitorapp.models.ViolationType
import com.example.ecomonitorapp.ui.screens.support.ReportFilterPanel
import com.example.ecomonitorapp.ui.screens.support.ViolationFilterPanel
import kotlin.collections.emptyList

data class CardWithReportRow(
    @Embedded val card: ViolationCardEntity,
    @Relation(parentColumn = "report", entityColumn = "id")
    val report: ReportEntity
)

@Composable
fun AllViolationCardsScreen(
    onOpenCard: (Long) -> Unit
) {
    val context = LocalContext.current
    val repository = remember {
        ReportRepository(context = context, session = SessionManager(context))
    }

    val cards by repository.observeAllCards()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    var selectedPriority by remember { mutableStateOf<ReportPriority?>(null) }
    var selectedViolationType by remember { mutableStateOf<ViolationType?>(null) }
    var selectedViolationStatus by remember { mutableStateOf<ViolationStatus?>(null) }

    val filteredCards = remember(cards, selectedPriority,
        selectedViolationType, selectedViolationStatus) {
        val priority = selectedPriority
        val type = selectedViolationType
        val status = selectedViolationStatus
        cards.filter { row ->
            row.card.id != null &&
                    (priority == null || row.report.reportPriority == priority.name) &&
                    (type == null || row.report.violationType == type.name) &&
                    (selectedViolationStatus == null || row.card.status == status?.name)
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ViolationFilterPanel(
                selectedPriority = selectedPriority,
                onPrioritySelected = { selectedPriority = it },
                selectedViolationType = selectedViolationType,
                onViolationTypeSelected = { selectedViolationType = it },
                selectedViolationStatus = selectedViolationStatus,
                onViolationStatusSelected = { selectedViolationStatus = it }
            )
        }

        items(filteredCards, key = { it.card.id!! }) { row ->
            val card = row.card
            val report = row.report

            val (statusText, statusColor) = when (ViolationStatus.valueOf(card.status)) {
                ViolationStatus.LIQUIDATED ->
                    ViolationStatus.LIQUIDATED.displayName to MaterialTheme.colorScheme.tertiary
                else ->
                    ViolationStatus.valueOf(card.status).displayName to MaterialTheme.colorScheme.onBackground
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { card.id?.let(onOpenCard) }
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "Карточка №${card.id}",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Обращение №${report.id}: ${
                            ViolationType.valueOf(report.violationType).displayName
                        }",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Статус: $statusText",
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor
                    )
                }
            }
        }
    }
}