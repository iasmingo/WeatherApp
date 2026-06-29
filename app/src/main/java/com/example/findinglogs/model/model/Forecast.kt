package com.example.findinglogs.model.model

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    val list: List<ForecastItem> = emptyList()
)

data class ForecastItem(
    val main: WeatherInfo = WeatherInfo(),
    val weather: List<WeatherDetail> = emptyList(),
    @SerializedName("dt_txt") val dtTxt: String = ""
)