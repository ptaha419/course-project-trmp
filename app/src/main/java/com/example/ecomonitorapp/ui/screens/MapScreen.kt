package com.example.ecomonitorapp.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ecomonitorapp.data.repositories.ReportRepository
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(
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
            controller.setZoom(12.0)
            controller.setCenter(GeoPoint(56.136, 40.394)) // центр по умолчанию
            overlays.add(CopyrightOverlay(context))
        }
    }

    // жизненный цикл: onResume/onPause/onDetach — то, что в Activity делали руками
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
            val marker = Marker(map).apply {
                position = GeoPoint(point.latitude, point.longitude)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = point.violationType.displayName
                snippet = buildString {
                    append("Широта: %.5f, Долгота: %.5f".format(point.latitude, point.longitude))
                    point.cardStatus?.let { append("\nСтатус: ${it.displayName}") }
                }
                setOnMarkerClickListener { m, _ ->
                    if (m.isInfoWindowShown) m.closeInfoWindow() else m.showInfoWindow()
                    point.cardId?.let { clickCallback(it) }
                    true
                }
            }
            map.overlays.add(marker)
        }
        map.invalidate()
    }

    AndroidView(
        factory = { map },
        modifier = modifier.fillMaxSize()
    )
}