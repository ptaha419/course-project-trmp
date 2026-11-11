package com.example.ecomonitorapp.ui.screens.support

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ecomonitorapp.models.ReportPriority
import com.example.ecomonitorapp.models.ReportStatus
import com.example.ecomonitorapp.models.ViolationStatus
import com.example.ecomonitorapp.models.ViolationType

@Composable
fun ReportFilterPanel(
    selectedPriority: ReportPriority?,
    onPrioritySelected: (ReportPriority?) -> Unit,
    selectedViolationType: ViolationType?,
    onViolationTypeSelected: (ViolationType?) -> Unit,
    selectedReportStatus: ReportStatus?,
    onReportStatusSelected: (ReportStatus?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Приоритет", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedPriority == null,
                onClick = { onPrioritySelected(null) },
                label = { Text("Все") }
            )
            ReportPriority.entries.forEach { priority ->
                FilterChip(
                    selected = selectedPriority == priority,
                    onClick = { onPrioritySelected(priority) },
                    label = { Text(priority.displayName) }
                )
            }
        }

        Text("Тип нарушения", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedViolationType == null,
                onClick = { onViolationTypeSelected(null) },
                label = { Text("Все") }
            )
            ViolationType.entries.forEach { type ->
                FilterChip(
                    selected = selectedViolationType == type,
                    onClick = { onViolationTypeSelected(type) },
                    label = { Text(type.displayName) }
                )
            }
        }

        Text("Статус обращения", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedReportStatus == null,
                onClick = { onReportStatusSelected(null) },
                label = { Text("Все") }
            )
            ReportStatus.entries.forEach { status ->
                FilterChip(
                    selected = selectedReportStatus == status,
                    onClick = { onReportStatusSelected(status) },
                    label = { Text(status.displayName) }
                )
            }
        }
    }
}

@Composable
fun ViolationFilterPanel(
    selectedPriority: ReportPriority?,
    onPrioritySelected: (ReportPriority?) -> Unit,
    selectedViolationType: ViolationType?,
    onViolationTypeSelected: (ViolationType?) -> Unit,
    selectedViolationStatus: ViolationStatus?,
    onViolationStatusSelected: (ViolationStatus?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Приоритет", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedPriority == null,
                onClick = { onPrioritySelected(null) },
                label = { Text("Все") }
            )
            ReportPriority.entries.forEach { priority ->
                FilterChip(
                    selected = selectedPriority == priority,
                    onClick = { onPrioritySelected(priority) },
                    label = { Text(priority.displayName) }
                )
            }
        }

        Text("Тип нарушения", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedViolationType == null,
                onClick = { onViolationTypeSelected(null) },
                label = { Text("Все") }
            )
            ViolationType.entries.forEach { type ->
                FilterChip(
                    selected = selectedViolationType == type,
                    onClick = { onViolationTypeSelected(type) },
                    label = { Text(type.displayName) }
                )
            }
        }
        Text("Статус нарушения", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedViolationStatus == null,
                onClick = { onViolationStatusSelected(null) },
                label = { Text("Все") }
            )
            ViolationStatus.entries.forEach { status ->
                FilterChip(
                    selected = selectedViolationStatus == status,
                    onClick = { onViolationStatusSelected(status) },
                    label = { Text(status.displayName) }
                )
            }
        }
    }
}