package com.example.findinglogs.model.model

data class GeoResult(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String = "",
    val state: String = ""
)