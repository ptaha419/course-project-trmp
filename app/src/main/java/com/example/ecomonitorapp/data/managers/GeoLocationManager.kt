package com.example.ecomonitorapp.data.managers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

// Класс для сохранения координат для геотега
data class GeoLoc(val latitude: Double, val longitude: Double)

object GeoLocationManager {
    val requiredPermissions = arrayOf (
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)

    fun hasPermissions(context: Context) = requiredPermissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        context: Context,
        onResult: (GeoLoc) -> Unit)
    {
        val client = LocationServices.getFusedLocationProviderClient(context)
        val cts = CancellationTokenSource()

        client.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cts.token)
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    onResult(GeoLoc(loc.latitude, loc.longitude))
                } else {
                    client.lastLocation
                        .addOnSuccessListener { cached ->
                            onResult(cached.let { GeoLoc(it.latitude, it.longitude) }) }
                        .addOnFailureListener { onResult(GeoLoc(0.0, 0.0)) }
                }
            }
            .addOnFailureListener { onResult(GeoLoc(0.0, 0.0)) }
    }
}