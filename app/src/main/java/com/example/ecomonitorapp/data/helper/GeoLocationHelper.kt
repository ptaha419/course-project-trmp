package com.example.ecomonitorapp.data.helper

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient

import android.widget.Toast
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import kotlinx.coroutines.launch


// Класс для сохранения координат для геотега
//data class GeoLoc(val latitude: Double, val longitude: Double)
//
//class GeoLocationHelper {
//    private var curLat: Double? = null // широта
//    private var curLong: Double? = null // долгота
//
//    // клиент для получения местоположения устройства
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//    private var geoLoc: GeoLoc? = null // геотег
//
//    // лаунчер для запроса разрешений
//    private val activityResultLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
//            var allGranted = true
//            var anyDenied = false
//
//            for ((perm, isGranted) in permissions) {
//                if (!isGranted) {
//                    anyDenied = true
//                } else {
//                    // все разрешения даны
//                }
//            }
//
//            if (anyDenied) {
//                // проверяем, можем ли мы ещё раз запросить
//                val shouldShowRationale = REQUIRED_PERMISSIONS.any { perm ->
//                    shouldShowRequestPermissionRationale(perm)
//                }
//
//                if (shouldShowRationale) {
//                    Toast.makeText(
//                        baseContext,
//                        "Пожалуйста, предоставьте разрешения на использование камеры и геолокации.",
//                        Toast.LENGTH_LONG
//                    ).show()
//                } else {
//                    Toast.makeText(
//                        baseContext,
//                        "Разрешения отклонены. Включите их в настройках приложения.",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            } else {
//                getPhotoGeoLocation()
//            }
//        }
//
//    @SuppressLint("MissingPermission")
//    private fun getPhotoGeoLocation() {
//        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//            location?.let { loc ->
//                curLat = loc.latitude
//                curLong = loc.longitude
//
//                Log.d("Geo", "Lat: ${loc.latitude}, Lon: ${loc.longitude}")
//                geoLoc = GeoLoc(loc.latitude, loc.longitude)
//
//                if (geoLoc != null) {
//                    Log.d("Geo", "Геолокация устройства получена")
//                }
//            } ?: run {
//                Log.w("Geo", "Данные о геолокации отсутствуют")
//            }
//        }.addOnFailureListener { e ->
//            Log.e("Geo", "Не удалось получить данные о геолокации", e)
//        }
//    }
//
//    // Запрос необходимых разрешений
//    private fun requestPermissions() {
//        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
//    }
//
//    // Проверка наличия разрешений
//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(
//            baseContext, it) == PackageManager.PERMISSION_GRANTED
//    }
//
//    companion object {
//        private const val TAG = "EcoMonitorApp"
//        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
//        private val REQUIRED_PERMISSIONS =
//            mutableListOf (
//                Manifest.permission.CAMERA,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ).apply {
//                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
//                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                }
//            }.toTypedArray()
//    }
//}