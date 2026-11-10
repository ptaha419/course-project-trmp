package com.example.ecomonitorapp.ui.screens.reports

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ecomonitorapp.data.managers.GeoLocationManager
import com.example.ecomonitorapp.data.mappers.toPriority
import com.example.ecomonitorapp.models.ViolationType
import com.example.ecomonitorapp.ui.viewmodels.reports.ReportState
import com.example.ecomonitorapp.ui.viewmodels.reports.ReportViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    vm: ReportViewModel,
    openReportId: Long? = null,
    openDraftId: Long? = null,
    initialViolationType: String? = null,
    onBack: () -> Unit,
) {
    LaunchedEffect(initialViolationType) {
        vm.onViolationTypeChange(initialViolationType)
    }

    LaunchedEffect(openReportId, openDraftId) {
        when {
            openReportId != null -> vm.loadReport(openReportId)
            openDraftId != null -> vm.loadDraft(openDraftId)
        }
    }

    // сброс состояния при выходе
    DisposableEffect(Unit) {
        onDispose { vm.reset() }
    }

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

private fun copyPhotoToInternal(context: Context, uri: Uri): String? =
    try {
        val dir = File(context.filesDir, "report_photos").apply { mkdirs() }
        val file = File(dir, "${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        } ?: return null
        file.absolutePath
    } catch (e: SecurityException) {
        null
    } catch (e: IOException) {
        null
    }

@Composable
private fun ReportForm(
    vm: ReportViewModel,
    modifier: Modifier = Modifier,
) {
    val form by vm.formFlow.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val path = copyPhotoToInternal(context, it)
            if (path != null) {
                vm.onPhotoChange(path)
            } else {
                Toast.makeText(context, "Не удалось сохранить фото", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val draftSaved by vm.draftSaved.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val viewOnly by vm.viewOnly.collectAsStateWithLifecycle()

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
                    enabled = !viewOnly,  // только просмотр, если открыт не черновик
                    label = { Text(type.displayName) }
                )
            }
        }

        OutlinedTextField(
            value = form.description,
            onValueChange = vm::onDescriptionChange,
            readOnly = viewOnly,
            enabled = !viewOnly,
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
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { grants ->
            if (grants.values.all { it }) {
                GeoLocationManager.getCurrentLocation(context) { geo ->
                    vm.onGeoLocChange(geo)
                }
            } else {
                Toast.makeText(context,
                    "Разрешите доступ к местоположению в настройках",
                    Toast.LENGTH_LONG).show()
            }
        }

        if (!viewOnly) {
            OutlinedButton(
                onClick = {
                    if (GeoLocationManager.hasPermissions(context)) {
                        GeoLocationManager.getCurrentLocation(context) { geo ->
                            vm.onGeoLocChange(geo)
                        }
                    } else {
                        permissionLauncher.launch(GeoLocationManager.requiredPermissions)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (form.geoLoc != null)
                        "Местоположение: ${form.geoLoc!!.latitude}, ${form.geoLoc!!.longitude}"
                    else
                        "Определить местоположение"
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
        } else {
            if (form.geoLoc != null) {
                Text(
                    text = "Местоположение: ${form.geoLoc!!.latitude}, ${form.geoLoc!!.longitude}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (form.photo != null) {
                val bitmap = remember(form.photo) { mutableStateOf<Bitmap?>(null) }
                LaunchedEffect(form.photo) {
                    withContext(Dispatchers.IO) {
                        try {
                            val photo = form.photo ?: return@withContext
                            bitmap.value = if (photo.startsWith("content://")) {
                                // старые записи, где сохранился URI пикера
                                context.contentResolver.openInputStream(Uri.parse(photo))
                                    ?.use { BitmapFactory.decodeStream(it) }
                            } else {
                                BitmapFactory.decodeFile(photo)
                            }
                        } catch (e: SecurityException) {
                            bitmap.value = null   // устаревший URI пикера
                        } catch (e: IOException) {
                            bitmap.value = null
                        }
                    }
                }
                val img = bitmap.value
                if (img != null) {
                    Image(
                        bitmap = img.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text("Фото недоступно", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (!viewOnly) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = vm::saveDraft,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (draftSaved) "Сохранено" else "В черновик")
                    if (draftSaved) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                    }
                }

                // сбрасываем состояние через пару секунд
                LaunchedEffect(draftSaved) {
                    if (draftSaved) {
                        kotlinx.coroutines.delay(2000)
                        vm.resetDraftSaved()   // _draftSaved.value = false
                    }
                }

                Button(
                    onClick = vm::submit,
                    enabled = form.canSubmit,
                    modifier = Modifier.weight(1f)
                ) { Text("Отправить") }
            }
        }
    }
}