package com.example.findinglogs.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findinglogs.model.model.ForecastItem
import com.example.findinglogs.model.model.Weather
import com.example.findinglogs.model.util.Utils
import com.example.findinglogs.view.theme.AppColors
import com.example.findinglogs.view.theme.getCardColor
import com.example.findinglogs.view.theme.getWeatherEmoji

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailScreen(
    weather: Weather,
    forecastItems: List<ForecastItem>,
    onBack: () -> Unit
) {
    val detail = weather.weather.firstOrNull()
    val iconCode = detail?.icon ?: ""
    val backgroundColor = getCardColor(iconCode)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(weather.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, backgroundColor)
                    )
                )
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = getWeatherEmoji(iconCode), fontSize = 72.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = Utils.getCelsiusFromKelvin(weather.main.temp),
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )

            if (detail != null) {
                Text(
                    text = detail.description.replaceFirstChar { it.uppercase() },
                    fontSize = 18.sp,
                    color = AppColors.TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Feels like ${Utils.getCelsiusFromKelvin(weather.main.feels_like)}",
                fontSize = 14.sp,
                color = AppColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (forecastItems.isNotEmpty()) {
                Text(
                    text = "Next hours",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(forecastItems.take(8)) { item ->
                        ForecastHourCard(item)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailCard("High", Utils.getCelsiusFromKelvin(weather.main.temp_max), Icons.Default.KeyboardArrowUp, Color(0xFFE53935), Modifier.weight(1f))
                DetailCard("Low", Utils.getCelsiusFromKelvin(weather.main.temp_min), Icons.Default.KeyboardArrowDown, Color(0xFF1E88E5), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailCard("Humidity", "${weather.main.humidity.toInt()}%", Icons.Default.WaterDrop, Color(0xFF42A5F5), Modifier.weight(1f))
                DetailCard("Pressure", "${weather.main.pressure.toInt()} hPa", Icons.Default.Speed, Color(0xFF66BB6A), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DetailCard(label: String, value: String, icon: ImageVector, iconTint: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = iconTint, modifier = Modifier.size(26.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ForecastHourCard(item: ForecastItem) {
    val iconCode = item.weather.firstOrNull()?.icon ?: ""
    val hour = item.dtTxt.substringAfter(" ").substring(0, 5)

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.width(72.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = hour, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = getWeatherEmoji(iconCode), fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = Utils.getCelsiusFromKelvin(item.main.temp),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}