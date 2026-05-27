package com.example.skycastproject.ui.features.saved_cities

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycastproject.ui.models.CityUiModel
import com.example.skycastproject.ui.theme.glassBackground
import com.example.skycastproject.ui.theme.glassBorder

@Composable
fun SavedCitiesScreen(cities: List<CityUiModel>, onCityClick: (String) -> Unit, onAddCityClick: () -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCityClick, containerColor = MaterialTheme.colorScheme.primary, shape = CircleShape) {
                Icon(Icons.Default.Add, "Add", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(24.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("My Cities", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text("${cities.size} Tracking Entries", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
            }
            Spacer(Modifier.height(24.dp))
            Box(modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(20.dp)).background(MaterialTheme.glassBackground).border(1.dp, MaterialTheme.glassBorder, RoundedCornerShape(20.dp)).clickable { onAddCityClick() }) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp).fillMaxHeight()) {
                    Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    Spacer(Modifier.width(12.dp))
                    Text("Search location parameters...", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f), fontSize = 16.sp)
                }
            }
            Spacer(Modifier.height(24.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
                items(cities) { city ->
                    Box(modifier = Modifier.fillMaxWidth().height(110.dp).clip(RoundedCornerShape(28.dp)).background(MaterialTheme.glassBackground).border(1.dp, MaterialTheme.glassBorder, RoundedCornerShape(28.dp)).clickable { onCityClick(city.name) }.padding(horizontal = 20.dp, vertical = 16.dp)) {
                        Column(modifier = Modifier.align(Alignment.TopStart)) {
                            Text(city.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                            Text("${city.time} · ${city.country}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                            Spacer(Modifier.weight(1f))
                            Text(city.condition, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
                        }
                        Column(modifier = Modifier.align(Alignment.TopEnd), horizontalAlignment = Alignment.End) {
                            Text("${city.temp}°", fontSize = 44.sp, fontWeight = FontWeight.Light, color = MaterialTheme.colorScheme.onBackground)
                            Text("H:${city.high}° L:${city.low}°", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}