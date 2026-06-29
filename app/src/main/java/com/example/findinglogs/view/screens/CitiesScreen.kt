package com.example.findinglogs.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findinglogs.model.model.City
import com.example.findinglogs.model.model.GeoResult
import com.example.findinglogs.view.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitiesScreen(
    cities: List<City>,
    onSearch: (String) -> Unit,
    searchResults: List<GeoResult>,
    isSearching: Boolean,
    onAddCity: (GeoResult) -> Unit,
    onRemoveCity: (City) -> Unit,
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Manage Cities", fontWeight = FontWeight.SemiBold, fontSize = 22.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppColors.TopBarContent)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.TopBar,
                    titleContentColor = AppColors.TopBarContent
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search city...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = ""; onSearch("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { if (query.length >= 3) onSearch(query) },
                modifier = Modifier.fillMaxWidth(),
                enabled = query.length >= 3,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.TopBar)
            ) {
                Text("Search")
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isSearching) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            } else if (searchResults.isNotEmpty()) {
                Text("Results", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                searchResults.forEach { result ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable { onAddCity(result) },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(result.name, fontWeight = FontWeight.Medium)
                                val location = listOfNotNull(
                                    result.state.takeIf { it.isNotEmpty() },
                                    result.country.takeIf { it.isNotEmpty() }
                                ).joinToString(", ")
                                if (location.isNotEmpty()) {
                                    Text(location, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color(0xFF4CAF50))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text("Your cities", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))

            if (cities.isEmpty()) {
                Text("No cities added", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    itemsIndexed(cities) { index, city ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${index + 1}.",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(text = city.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                                IconButton(onClick = { onRemoveCity(city) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFFE53935))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}