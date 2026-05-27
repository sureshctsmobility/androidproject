package com.example.skycastproject.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycastproject.ui.component.WeatherStatsRow
import com.example.skycastproject.ui.models.HomeStateWrapper
import com.example.skycastproject.ui.theme.glassBackground
import com.example.skycastproject.ui.theme.glassBorder

@Composable
fun HomeScreen(uiState: HomeStateWrapper, onRequestPermission: () -> Unit, onRefresh: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
        when (uiState) {
            is HomeStateWrapper.Loading -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(16.dp))
                    Text("Fetching Location Coordinates...", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
                }
            }
            is HomeStateWrapper.PermissionDenied -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.Red, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Location access is required to render localized data.", color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onRequestPermission) { Text("Grant Access") }
                }
            }
            is HomeStateWrapper.Error -> Text(uiState.explanatoryMessage, color = Color.Red, textAlign = TextAlign.Center)
            is HomeStateWrapper.Success -> {
                val stats = uiState.stats
                val scrollState = rememberScrollState()

                // Robust Icon mapping using numeric weatherCode from API
                val weatherIcon = when (stats.weatherCode) {
                    0, 1 -> Icons.Default.WbSunny // Clear, Mainly Clear
                    2, 3, 45, 48 -> Icons.Default.Cloud // Partly Cloudy, Overcast, Fog
                    51, 53, 55, 61, 63, 65, 80, 81, 82 -> Icons.Default.Umbrella // Rain
                    95, 96, 99 -> Icons.Default.Thunderstorm // Storm
                    else -> Icons.Default.WbSunny
                }

                // Map condition to tint
                val iconTint = if (stats.weatherCode <= 1) Color(0xFFFFD700) else MaterialTheme.colorScheme.onBackground

                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {}, modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.glassBackground).border(1.dp, MaterialTheme.glassBorder, RoundedCornerShape(14.dp))) {
                            Icon(Icons.Default.GridView, null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.weight(1f))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, null, tint = Color.Red, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(stats.cityName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = onRefresh, modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.glassBackground).border(1.dp, MaterialTheme.glassBorder, RoundedCornerShape(14.dp))) {
                            Icon(Icons.Default.Refresh, null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(40.dp)).background(MaterialTheme.glassBackground).border(1.dp, MaterialTheme.glassBorder, RoundedCornerShape(40.dp)), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = weatherIcon, null, tint = iconTint, modifier = Modifier.size(100.dp))
                            Text("${stats.currentTemp}°", fontSize = 70.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                            Text(stats.condition, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
                            Text(stats.date, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), fontSize = 13.sp)
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    WeatherStatsRow(stats.humidity, stats.wind, stats.rain, stats.uv)
                    Spacer(Modifier.height(24.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(stats.hourlyForecast) { item ->
                            val itemIcon = when (item.weatherCode) {
                                0, 1 -> Icons.Default.WbSunny
                                2, 3, 45, 48 -> Icons.Default.Cloud
                                51, 53, 55, 61, 63, 65, 80, 81, 82 -> Icons.Default.Umbrella
                                95, 96, 99 -> Icons.Default.Thunderstorm
                                else -> Icons.Default.WbSunny
                            }
                            val itemTint = if (item.weatherCode <= 1) Color(0xFFFFD700) else MaterialTheme.colorScheme.onBackground

                            Box(
                                modifier = Modifier
                                    .width(72.dp) // Wider for labels like "12 pm"
                                    .height(130.dp) // Increased height to prevent cutting
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(MaterialTheme.glassBackground)
                                    .border(1.dp, MaterialTheme.glassBorder, RoundedCornerShape(22.dp))
                                    .padding(vertical = 12.dp), 
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(item.time, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontSize = 11.sp)
                                    Spacer(Modifier.height(8.dp))
                                    Text("${item.temp}°", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Spacer(Modifier.height(8.dp))
                                    Icon(imageVector = itemIcon, null, tint = itemTint, modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
