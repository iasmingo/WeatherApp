package com.example.findinglogs.model.repo.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object ConnectionManager {
    private const val OPEN_WEATHER_DOMAIN = "https://api.openweathermap.org/data/2.5/"
    private const val GEO_DOMAIN = "https://api.openweathermap.org/geo/1.0/"

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            })
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.SECONDS)
            .build()
    }

    val weatherConnection: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(OPEN_WEATHER_DOMAIN)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }

    val geoConnection: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(GEO_DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }
}