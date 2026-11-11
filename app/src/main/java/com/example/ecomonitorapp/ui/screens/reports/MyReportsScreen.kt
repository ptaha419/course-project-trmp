package com.example.ecomonitorapp.ui.screens.reports

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ecomonitorapp.data.managers.SessionManager
import com.example.ecomonitorapp.data.repositories.ReportRepository
import com.example.ecomonitorapp.models.ReportPriority
import com.example.ecomonitorapp.models.ReportStatus
import com.example.ecomonitorapp.models.ViolationType
import com.example.ecomonitorapp.ui.screens.support.ReportFilterPanel

@Composable
fun MyReportsScreen(
    onOpenReport: (Long) -> Unit,
    onOpenDraft: (Long) -> Unit,
) {
    val context = LocalContext.current

    val repository = remember {
        ReportRepository(
            context = context,
            session = SessionManager(context)
        )
    }

    val reports by repository.observeMyReports()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    // выбранные фильтры
    // null = показывать все
    var selectedPriority by remember { mutableStateOf<ReportPriority?>(null) }
    var selectedViolationType by remember { mutableStateOf<ViolationType?>(null) }
    var selectedReportStatus by remember { mutableStateOf<ReportStatus?>(null) }

    val filteredReports = remember(reports, selectedPriority,
        selectedViolationType, selectedReportStatus) {
        val priority = selectedPriority
        val type = selectedViolationType
        val status = selectedReportStatus
        reports.filter { report ->
            report.id != null &&
                    (priority == null || report.reportPriority == priority.name) &&
                    (type == null || report.violationType == type.name) &&
                    (status == null || report.status == status.name)
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ReportFilterPanel(
                selectedPriority = selectedPriority,
                onPrioritySelected = { selectedPriority = it },
                selectedViolationType = selectedViolationType,
                onViolationTypeSelected = { selectedViolationType = it },
                selectedReportStatus = selectedReportStatus,
                onReportStatusSelected = { selectedReportStatus = it }
            )
        }

        items(filteredReports, key = { it.id!! }) { report ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        report.id?.let { id ->
                            if (report.isDraft) onOpenDraft(id) else onOpenReport(id)
                        }
                    }
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Обращение №${report.id}",
                            style = MaterialTheme.typography.titleSmall
                        )
                        if (report.isDraft) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = Color.Transparent,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Text(
                                    text = "Черновик",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = ReportPriority.valueOf(report.reportPriority).displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = ViolationType.valueOf(report.violationType).displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = report.description.ifEmpty { "Описание не заполнено" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}