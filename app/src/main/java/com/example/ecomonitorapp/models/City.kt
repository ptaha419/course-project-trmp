package com.example.ecomonitorapp.models

import android.content.Context
import com.google.gson.Gson

data class City(
    val name: String,
    val subject: String?,
    val population: Long,
    val coords: CityCoords? = null,
)

data class CityCoords(
    val lat: Double,
    val lon: Double,
)

object CityRepository {
    @Volatile
    private var cached: List<City>? = null

    fun load(context: Context): List<City> =
        cached ?: synchronized(this) {
            cached ?: run {
                val json = context.assets.open("russian-cities.json")
                    .bufferedReader().use { it.readText() }
                val raw = Gson().fromJson(json, Array<City>::class.java).toList()
                // крупные города в начале списка, чтобы они были первыми в меню
                raw.sortedByDescending { it.population }.also { cached = it }
            }
        }

    fun findByName(context: Context, name: String): City? =
        load(context).firstOrNull { it.name.equals(name.trim(), ignoreCase = true) }
}