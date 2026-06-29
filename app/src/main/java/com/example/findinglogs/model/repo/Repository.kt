package com.example.findinglogs.model.repo

import android.content.Context
import android.content.SharedPreferences
import com.example.findinglogs.model.model.City
import com.example.findinglogs.model.model.ForecastResponse
import com.example.findinglogs.model.model.GeoResult
import com.example.findinglogs.model.repo.remote.WeatherManager
import com.example.findinglogs.model.repo.remote.api.WeatherCallback
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Repository(context: Context) {
    private val weatherManager = WeatherManager()
    private val prefs: SharedPreferences =
        context.getSharedPreferences("weather_cities", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun retrieveForecast(city: City, callback: WeatherCallback) {
        weatherManager.retrieveForecast(city.lat, city.lon, callback)
    }

    fun retrieveHourlyForecast(city: City, onResult: (ForecastResponse) -> Unit, onError: (String) -> Unit) {
        weatherManager.retrieveHourlyForecast(city.lat, city.lon, onResult, onError)
    }

    fun searchCity(query: String, onResult: (List<GeoResult>) -> Unit, onError: (String) -> Unit) {
        weatherManager.searchCity(query, onResult, onError)
    }

    fun getCities(): List<City> {
        val json = prefs.getString("cities", null) ?: return defaultCities()
        val type = object : TypeToken<List<City>>() {}.type
        val cities: List<City> = gson.fromJson(json, type)
        return cities.ifEmpty { defaultCities() }
    }

    fun addCity(city: City) {
        val cities = getCities().toMutableList()
        if (cities.none { it.lat == city.lat && it.lon == city.lon }) {
            cities.add(city)
            saveCities(cities)
        }
    }

    fun removeCity(city: City) {
        val cities = getCities().toMutableList()
        cities.removeAll { it.lat == city.lat && it.lon == city.lon }
        saveCities(cities)
    }

    private fun saveCities(cities: List<City>) {
        prefs.edit().putString("cities", gson.toJson(cities)).apply()
    }

    private fun defaultCities(): List<City> = listOf(
        City("Recife", -8.05428, -34.8813),
        City("Petrolina", -9.39416, -40.5096),
        City("Caruaru", -8.284547, -35.969863)
    )
}