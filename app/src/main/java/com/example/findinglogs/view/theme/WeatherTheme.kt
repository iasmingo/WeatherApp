package com.example.findinglogs.view.theme

import androidx.compose.ui.graphics.Color

object AppColors {
    val TopBar = Color(0xFF4A90D9)
    val TopBarContent = Color.White
    val TextPrimary = Color(0xFF3A3A3A)
    val TextSecondary = Color(0xFF666666)
}

fun getWeatherEmoji(iconCode: String): String {
    return when {
        iconCode.startsWith("01d") -> "☀️"
        iconCode.startsWith("01n") -> "🌙"
        iconCode.startsWith("02d") -> "⛅"
        iconCode.startsWith("02n") -> "☁️"
        iconCode.startsWith("03") -> "☁️"
        iconCode.startsWith("04") -> "☁️"
        iconCode.startsWith("09") -> "🌧️"
        iconCode.startsWith("10d") -> "🌦️"
        iconCode.startsWith("10n") -> "🌧️"
        iconCode.startsWith("11") -> "⛈️"
        iconCode.startsWith("13") -> "❄️"
        iconCode.startsWith("50") -> "🌫️"
        else -> "🌡️"
    }
}

fun getCardColor(iconCode: String): Color {
    return when {
        iconCode.startsWith("01") -> Color(0xFF90CAF9)
        iconCode.startsWith("02") -> Color(0xFF80DEEA)
        iconCode.startsWith("03") || iconCode.startsWith("04") -> Color(0xFFB0BEC5)
        iconCode.startsWith("09") || iconCode.startsWith("10") -> Color(0xFF9FA8DA)
        iconCode.startsWith("11") -> Color(0xFFB39DDB)
        iconCode.startsWith("13") -> Color(0xFFB3E5FC)
        iconCode.startsWith("50") -> Color(0xFFD7CCC8)
        else -> Color(0xFF90CAF9)
    }
}
