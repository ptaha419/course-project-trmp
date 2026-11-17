package com.example.ecomonitorapp.ui.screens

import com.example.ecomonitorapp.R
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ecomonitorapp.data.repositories.ReportRepository
import com.example.ecomonitorapp.models.CityRepository
import com.example.ecomonitorapp.models.MapPoint
import com.example.ecomonitorapp.models.ReportPriority
import com.example.ecomonitorapp.models.ViolationStatus
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(
    userCity: String,
    repository: ReportRepository,
    onCardClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val points by repository.observeMapPoints()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    // инициализация карты
    val map = remember {
        MapView(context).apply {
            setTileSource(
                XYTileSource(
                    "OpenStreetMap",
                    0, 19, 256, ".png",
                    arrayOf(
                        "https://a.tile.openstreetmap.fr/hot/",
                        "https://b.tile.openstreetmap.fr/hot/",
                        "https://c.tile.openstreetmap.fr/hot/"
                    ),
                    "© OpenStreetMap contributors"
                )
            )
            setMultiTouchControls(true)
            controller.setZoom(15.0)
            controller.setCenter(GeoPoint(56.136, 40.394)) // центр по умолчанию
            overlays.add(CopyrightOverlay(context))
        }
    }

    // центр выбранного города
    val cityCenter = remember(userCity) {
        CityRepository.findByName(context, userCity)
            ?.coords
            ?.let { GeoPoint(it.lat, it.lon) }
    }

    // при изменении города перемещаем центр
    LaunchedEffect(cityCenter) {
        cityCenter?.let { map.controller.animateTo(it) }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> map.onResume()
                Lifecycle.Event.ON_PAUSE -> map.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            map.onDetach()
        }
    }

    // перестраиваем маркеры при изменении данных
    val clickCallback by rememberUpdatedState(onCardClick)
    LaunchedEffect(points) {
        map.overlays.removeAll { it is Marker }
        points.forEach { point ->
            val drawable = ContextCompat.getDrawable(context, R.drawable.ic_marker_pin)!!
                .mutate() // без mutate() tint покрасит ОБЩИЙ drawable и все маркеры станут одного цвета
            drawable.setTint(markerColor(point).toArgb())

            val marker = Marker(map).apply {
                position = GeoPoint(point.latitude, point.longitude)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = drawable
                title = point.violationType.displayName
                snippet = buildString {
                    append("Приоритет: ${point.reportPriority.displayName}")
                    append("\nШирота: %.5f, Долгота: %.5f".format(point.latitude, point.longitude))
                    point.cardStatus?.let { append("\nСтатус: ${it.displayName}") }
                }
                setOnMarkerClickListener { m, _ ->
                    if (!m.isInfoWindowShown) {
                        m.showInfoWindow()
                    } else {
                        m.closeInfoWindow()
                        val cardId = point.cardId
                        if (cardId != null) clickCallback(cardId)
                        else Toast.makeText(
                            context,
                            "К этому обращению карточка ещё не создана",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    true
                }
            }
            map.overlays.add(marker)
        }
    }

    AndroidView(
        factory = { map },
        modifier = modifier.fillMaxSize()
    )
}

private fun markerColor(point: MapPoint): Color = when {
    point.cardId == null -> Color(0xFFFFFFFF)                       // просто обращение
    point.cardStatus == ViolationStatus.LIQUIDATED -> Color(0xFF616161) // ликвидировано
    point.reportPriority == ReportPriority.HIGH -> Color(0xFFD32F2F)      // красный
    point.reportPriority == ReportPriority.MEDIUM -> Color(0xFFF57C00)    // оранжевый
    else -> Color(0xFF388E3C)                                       // зелёный
}