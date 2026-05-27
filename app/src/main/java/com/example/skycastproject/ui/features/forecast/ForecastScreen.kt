package com.example.skycastproject.ui.features.forecast

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WbSunny
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycastproject.ui.component.CustomForecastChart
import com.example.skycastproject.ui.models.ForecastDay
import com.example.skycastproject.ui.theme.glassBackground
import com.example.skycastproject.ui.theme.glassBorder

@Composable
fun ForecastScreen(cityName: String, forecastDays: List<ForecastDay>, chartData: List<Int>, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(48.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.glassBackground).border(1.dp, MaterialTheme.glassBorder, RoundedCornerShape(14.dp))) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground)
                }
                Text(text = "Forecast", color = MaterialTheme.colorScheme.onBackground, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = {}) { Icon(Icons.Default.Refresh, "Refresh", tint = MaterialTheme.colorScheme.onBackground) }
            }
            Spacer(Modifier.height(24.dp))
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = cityName, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(text = "Next 5 days · Open-Meteo", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), fontSize = 14.sp)
            }
            Spacer(Modifier.height(24.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
                if (chartData.isNotEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(MaterialTheme.glassBackground).border(1.dp, MaterialTheme.glassBorder, RoundedCornerShape(24.dp))) {
                            CustomForecastChart(temps = chartData, days = forecastDays.map { it.day }, modifier = Modifier.padding(vertical = 16.dp))
                        }
                    }
                }
                items(forecastDays) { day ->
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(MaterialTheme.glassBackground).border(1.dp, MaterialTheme.glassBorder, RoundedCornerShape(24.dp))) {
                        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = if (day.condition == "Sunny") Icons.Default.WbSunny else Icons.Default.Cloud, null, tint = if (day.condition == "Sunny") Color(0xFFFFD700) else MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(44.dp))
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = day.day, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(text = day.condition, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontSize = 13.sp)
                            }
                            Text(text = "${day.highTemp}°", color = MaterialTheme.colorScheme.onBackground, fontSize = 36.sp, fontWeight = FontWeight.Light)
                        }
                    }
                }
            }
        }
    }
}