package com.example.ecomonitorapp.ui.screens.reports

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ecomonitorapp.data.managers.GeoLocationManager
import com.example.ecomonitorapp.data.mappers.toPriority
import com.example.ecomonitorapp.models.ReportPriority
import com.example.ecomonitorapp.models.ViolationType
import com.example.ecomonitorapp.ui.viewmodels.reports.ReportForm
import com.example.ecomonitorapp.ui.viewmodels.reports.ReportState
import com.example.ecomonitorapp.ui.viewmodels.reports.ReportViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    vm: ReportViewModel,
    initialViolationType: String? = null,
    onBack: () -> Unit,
) {
    LaunchedEffect(initialViolationType) { vm.onViolationTypeChange(initialViolationType) }

    val state by vm.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state) {
        when (state) {
            is ReportState.Error ->
                snackbarHostState.showSnackbar((state as ReportState.Error).message)
            is ReportState.Success -> onBack()
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новое обращение") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            when (state) {
                is ReportState.Loading -> CircularProgressIndicator()
                else -> ReportForm(vm = vm, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun ReportForm(
    vm: ReportViewModel,
    modifier: Modifier = Modifier,
) {
    val form by vm.formFlow.collectAsStateWithLifecycle()

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { vm.onPhotoChange(it.toString()) }
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (form.violationType != ViolationType.NOT_SELECTED) {
            FilterChip(
                selected = true,
                onClick = { },
                label = { Text(form.violationType.toPriority().displayName) }
            )
        }
        Text(
            text = "Приоритет обращения определяется автоматически по выбранному типу нарушения",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text("Тип нарушения", style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ViolationType.entries.filter { it != ViolationType.NOT_SELECTED }.forEach { type ->
                FilterChip(
                    selected = form.violationType == type,
                    onClick = { vm.onViolationTypeChange(type.name) },
                    label = { Text(type.displayName) }
                )
            }
        }

        OutlinedTextField(
            value = form.description,
            onValueChange = vm::onDescriptionChange,
            label = { Text("Описание") },
            isError = form.description.isNotEmpty() && !form.isDescriptionValid,
            supportingText = {
                if (form.description.isNotEmpty() && !form.isDescriptionValid) {
                    Text("Минимум 10 символов")
                }
            },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        OutlinedButton(
            onClick = {
                scope.launch {
                    vm.onGeoLocChange(GeoLocationManager.getCurrentLocation(context))
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (form.geoLoc != null) {
                    "Местоположение: ${form.geoLoc!!.latitude}, ${form.geoLoc!!.longitude}"
                } else {
                    "Определить местоположение"
                }
            )
        }

        OutlinedButton(
            onClick = {
                photoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (form.photo != null) "Фото выбрано ✓" else "Прикрепить фото")
        }

        Button(
            onClick = vm::submit,
            enabled = form.canSubmit, // проверка отправки
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Отправить")
        }
    }
}