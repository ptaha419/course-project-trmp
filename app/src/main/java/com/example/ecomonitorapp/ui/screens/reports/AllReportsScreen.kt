package com.example.ecomonitorapp.ui.screens.reports

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ecomonitorapp.data.managers.SessionManager
import com.example.ecomonitorapp.data.repositories.ReportRepository
import com.example.ecomonitorapp.models.ViolationType
import kotlin.text.ifEmpty

@Composable
fun AllReportsScreen(
    onOpenReport: (Long) -> Unit
) {
    val context = LocalContext.current

    val repository = remember {
        ReportRepository(
            context = context,
            session = SessionManager(context)
        )
    }

    val reports by repository.observeAll()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items( reports.filter {
            it.id != null && !it.isDraft },
            key = { it.id!! } ) { report ->
            Card( modifier = Modifier.fillMaxWidth() ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ViolationType.valueOf(report.violationType).displayName,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
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