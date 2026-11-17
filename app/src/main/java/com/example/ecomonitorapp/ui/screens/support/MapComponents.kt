package com.example.ecomonitorapp.ui.screens.support

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ecomonitorapp.data.managers.GeoLoc
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import com.example.ecomonitorapp.R
import com.example.ecomonitorapp.models.CityRepository

@Composable
fun LocationSourceDialog(
    onAuto: () -> Unit,
    onMap: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Указать местоположение") },
        text = { Text("Как определить точку нарушения?") },
        confirmButton = {
            TextButton(onClick = onAuto) { Text("Текущее местоположение") }
        },
        dismissButton = {
            TextButton(onClick = onMap) { Text("Выбрать на карте") }
        }
    )
}

@Composable
fun MapPickerDialog(
    userCity: String,
    initialPoint: GeoLoc?,
    onConfirm: (GeoLoc) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var picked by remember { mutableStateOf(initialPoint) }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(
                XYTileSource(
                    "OpenStreetMap",
                    0, 50, 256, ".png",
                    arrayOf(
                        "https://a.tile.openstreetmap.fr/hot/",
                        "https://b.tile.openstreetmap.fr/hot/",
                        "https://c.tile.openstreetmap.fr/hot/"
                    ),
                    "© OpenStreetMap contributors"
                )
            )
            setMultiTouchControls(true)
            controller.setZoom(12.0)
            // центр по умолчанию — пока не нашли город
            controller.setCenter(GeoPoint(56.136, 40.394))
            overlays.add(CopyrightOverlay(context))
        }
    }

    val marker = remember(mapView) {
        Marker(mapView).apply {
            position = GeoPoint(0.0, 0.0) // безопасное значение вместо null
            isEnabled = false // маркер скрыт, пока точку не выбрали
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            isDraggable = true
            setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
                override fun onMarkerDrag(m: Marker?) {}
                override fun onMarkerDragStart(m: Marker?) {}
                override fun onMarkerDragEnd(m: Marker?) {
                    m?.position?.let { picked = GeoLoc(it.latitude, it.longitude) }
                }
            })
            icon = ContextCompat.getDrawable(context, R.drawable.ic_marker_pin)?.apply {
                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            }
        }.also { mapView.overlays.add(it) }
    }

    remember(mapView, marker) {
        mapView.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let {
                    marker.position = it
                    marker.isEnabled = true
                    picked = GeoLoc(it.latitude, it.longitude)
                    mapView.invalidate()
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean = false
        }))
    }

    // центр выбранного города
    val cityCenter = remember(userCity) {
        CityRepository.findByName(context, userCity)
            ?.coords
            ?.let { GeoPoint(it.lat, it.lon) }
    }

    // если город найден и точка ещё не выбрана, то центрируемся на городе
    LaunchedEffect(Unit) {
        initialPoint?.let { geo ->
            val p = GeoPoint(geo.latitude, geo.longitude)
            marker.position = p
            marker.isEnabled = true
            mapView.controller.setCenter(p)
            mapView.controller.animateTo(p)
            mapView.invalidate()
        } ?: run {
            // точки нет — центрируемся на городе
            cityCenter?.let {
                mapView.controller.setCenter(it)
                mapView.controller.animateTo(it)
                mapView.invalidate()
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Выберите точку нарушения",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Закрыть")
                }
            }

            AndroidView(
                factory = { mapView },
                modifier = Modifier.weight(1f).fillMaxWidth()
            )

            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = picked?.let { "\nШирота: %.5f, Долгота: %.5f".format(it.latitude, it.longitude) }
                        ?: "Коснитесь карты, чтобы поставить точку",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) { Text("Отмена") }

                    Button(
                        onClick = { picked?.let(onConfirm) },
                        enabled = picked != null,
                        modifier = Modifier.weight(1f)
                    ) { Text("Сохранить точку") }
                }
            }
        }
    }
}