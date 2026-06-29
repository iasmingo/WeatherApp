package com.example.findinglogs.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findinglogs.model.model.Weather
import com.example.findinglogs.model.util.Utils
import com.example.findinglogs.view.theme.AppColors
import com.example.findinglogs.view.theme.getCardColor
import com.example.findinglogs.view.theme.getWeatherEmoji

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherListScreen(
    weathers: List<Weather>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onCardClick: (Int) -> Unit,
    onCitiesClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Weather", fontWeight = FontWeight.SemiBold, fontSize = 22.sp)
                },
                actions = {
                    IconButton(onClick = onCitiesClick) {
                        Icon(Icons.Default.AddLocationAlt, contentDescription = "Manage cities", tint = AppColors.TopBarContent)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.TopBar,
                    titleContentColor = AppColors.TopBarContent
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            if (weathers.isEmpty() && isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(weathers) { index, weather ->
                        WeatherCard(weather = weather, onClick = { onCardClick(index) })
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherCard(weather: Weather, onClick: () -> Unit) {
    val iconCode = weather.weather.firstOrNull()?.icon ?: ""
    val description = weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: ""
    val cardColor = getCardColor(iconCode)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(cardColor, Color.White)
                    )
                )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getWeatherEmoji(iconCode),
                    fontSize = 40.sp,
                    modifier = Modifier.padding(end = 14.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = weather.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
                            Text(text = description, fontSize = 13.sp, color = AppColors.TextSecondary)
                        }
                        Text(
                            text = Utils.getCelsiusFromKelvin(weather.main.temp),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, modifier = Modifier.size(16.dp), tint = AppColors.TextSecondary)
                            Text(text = Utils.getCelsiusFromKelvin(weather.main.temp_max), fontSize = 13.sp, color = AppColors.TextSecondary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = AppColors.TextSecondary)
                            Text(text = Utils.getCelsiusFromKelvin(weather.main.temp_min), fontSize = 13.sp, color = AppColors.TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(14.dp), tint = AppColors.TextSecondary)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = "${weather.main.humidity.toInt()}%", fontSize = 13.sp, color = AppColors.TextSecondary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(14.dp), tint = AppColors.TextSecondary)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = "${weather.main.pressure.toInt()} hPa", fontSize = 13.sp, color = AppColors.TextSecondary)
                        }
                    }
                }
            }
        }
    }
}
