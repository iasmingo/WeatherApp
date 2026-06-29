package com.example.findinglogs.model.model

data class Weather(
    val main: WeatherInfo = WeatherInfo(),
    val name: String = "",
    val weather: List<WeatherDetail> = emptyList()
)

data class WeatherInfo(
    val temp: Float = 0f,
    val feels_like: Float = 0f,
    val temp_min: Float = 0f,
    val temp_max: Float = 0f,
    val pressure: Float = 0f,
    val humidity: Float = 0f
)

data class WeatherDetail(
    val main: String = "",
    val description: String = "",
    val icon: String = ""
)