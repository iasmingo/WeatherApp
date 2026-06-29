package com.example.findinglogs.model.repo.remote.api

import com.example.findinglogs.model.model.Weather

interface WeatherCallback {
    fun onSuccess(weather: Weather)
    fun onFailure(msg: String)
}