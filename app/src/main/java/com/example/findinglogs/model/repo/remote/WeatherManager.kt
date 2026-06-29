package com.example.findinglogs.model.repo.remote

import com.example.findinglogs.BuildConfig
import com.example.findinglogs.model.model.ForecastResponse
import com.example.findinglogs.model.model.GeoResult
import com.example.findinglogs.model.model.Weather
import com.example.findinglogs.model.repo.remote.api.GeoService
import com.example.findinglogs.model.repo.remote.api.WeatherCallback
import com.example.findinglogs.model.repo.remote.api.WeatherService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.net.ssl.HttpsURLConnection

class WeatherManager {

    fun retrieveForecast(lat: Double, lon: Double, callback: WeatherCallback) {
        val apiKey = BuildConfig.WEATHER_API_KEY

        ConnectionManager.weatherConnection
            .create(WeatherService::class.java)
            .getWeather(lat.toString(), lon.toString(), apiKey)
            .enqueue(object : Callback<Weather> {
                override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                    if (response.isSuccessful && response.code() == HttpsURLConnection.HTTP_OK) {
                        response.body()?.let { callback.onSuccess(it) }
                            ?: callback.onFailure("Empty response body")
                    } else {
                        callback.onFailure("HTTP ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Weather>, t: Throwable) {
                    callback.onFailure(t.message ?: "Unknown error")
                }
            })
    }

    fun retrieveHourlyForecast(lat: Double, lon: Double, onResult: (ForecastResponse) -> Unit, onError: (String) -> Unit) {
        val apiKey = BuildConfig.WEATHER_API_KEY

        ConnectionManager.weatherConnection
            .create(WeatherService::class.java)
            .getForecast(lat.toString(), lon.toString(), apiKey)
            .enqueue(object : Callback<ForecastResponse> {
                override fun onResponse(call: Call<ForecastResponse>, response: Response<ForecastResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { onResult(it) }
                            ?: onError("Empty response body")
                    } else {
                        onError("HTTP ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                    onError(t.message ?: "Unknown error")
                }
            })
    }

    fun searchCity(query: String, onResult: (List<GeoResult>) -> Unit, onError: (String) -> Unit) {
        val apiKey = BuildConfig.WEATHER_API_KEY

        ConnectionManager.geoConnection
            .create(GeoService::class.java)
            .searchCity(query, appid = apiKey)
            .enqueue(object : Callback<List<GeoResult>> {
                override fun onResponse(call: Call<List<GeoResult>>, response: Response<List<GeoResult>>) {
                    if (response.isSuccessful) {
                        onResult(response.body() ?: emptyList())
                    } else {
                        onError("HTTP ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<GeoResult>>, t: Throwable) {
                    onError(t.message ?: "Unknown error")
                }
            })
    }
}