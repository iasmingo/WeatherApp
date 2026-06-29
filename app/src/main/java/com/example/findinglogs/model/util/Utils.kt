package com.example.findinglogs.model.util

import kotlin.math.roundToInt

object Utils {
    fun getCelsiusFromKelvin(temp: Float): String {
        val celsius = (temp - 273.15f).roundToInt()
        return "${celsius}ºC"
    }
}