package com.example.findinglogs.model.repo.remote.api

import com.example.findinglogs.model.model.ForecastResponse
import com.example.findinglogs.model.model.GeoResult
import com.example.findinglogs.model.model.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather")
    fun getWeather(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appid") appid: String
    ): Call<Weather>

    @GET("forecast")
    fun getForecast(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appid") appid: String
    ): Call<ForecastResponse>
}

interface GeoService {
    @GET("direct")
    fun searchCity(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") appid: String
    ): Call<List<GeoResult>>
}