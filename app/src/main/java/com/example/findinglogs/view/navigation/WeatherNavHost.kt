package com.example.findinglogs.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.findinglogs.view.screens.CitiesScreen
import com.example.findinglogs.view.screens.WeatherDetailScreen
import com.example.findinglogs.view.screens.WeatherListScreen
import com.example.findinglogs.viewmodel.MainViewModel

@Composable
fun WeatherNavHost(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val weatherList by viewModel.weatherList.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val cities by viewModel.cities.observeAsState(emptyList())
    val searchResults by viewModel.searchResults.observeAsState(emptyList())
    val isSearching by viewModel.isSearching.observeAsState(false)
    val forecastItems by viewModel.forecastItems.observeAsState(emptyList())

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            WeatherListScreen(
                weathers = weatherList,
                isLoading = isLoading,
                onRefresh = { viewModel.refresh() },
                onCardClick = { index -> navController.navigate("detail/$index") },
                onCitiesClick = { navController.navigate("cities") }
            )
        }
        composable("detail/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val weather = weatherList.getOrNull(index)

            LaunchedEffect(index) {
                viewModel.loadForecast(index)
            }

            if (weather != null) {
                WeatherDetailScreen(
                    weather = weather,
                    forecastItems = forecastItems,
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable("cities") {
            CitiesScreen(
                cities = cities,
                searchResults = searchResults,
                isSearching = isSearching,
                onSearch = { viewModel.searchCity(it) },
                onAddCity = { viewModel.addCity(it) },
                onRemoveCity = { viewModel.removeCity(it) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}