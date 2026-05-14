package com.example.skycastproject.ui.features.forecast

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import com.example.skycastproject.ui.component.CustomForecastChart
import com.example.skycastproject.ui.models.ForecastDay

@Composable
fun ForecastScreen(
    cityName: String,
    forecastDays: List<ForecastDay>,
    chartData: List<Int>,
    onBack: () -> Unit,
    isNight: Boolean = isSystemInDarkTheme()
) {
    val backgroundColor = if (isNight) Color.Black else Color.White
    val contentColor = if (isNight) Color.White else Color.Black
    val glassBg = if (isNight) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f)
    val glassBorder = if (isNight) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(48.dp))

            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(glassBg)
                        .border(1.dp, glassBorder, RoundedCornerShape(14.dp))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = contentColor.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = "Forecast",
                    color = contentColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { /* Refresh */ }) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = contentColor
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // City info
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = cityName,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = "Next 5 days · Open-Meteo",
                    color = contentColor.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            // Glass Tab Selector
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(glassBg)
                    .border(1.dp, glassBorder, RoundedCornerShape(18.dp))
                    .padding(4.dp)
            ) {
                Row {
                    Surface(
                        color = contentColor,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Day",
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = backgroundColor
                        )
                    }
                    Text(
                        text = "Month",
                        modifier = Modifier.weight(1f).padding(vertical = 10.dp),
                        textAlign = TextAlign.Center,
                        color = contentColor.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Forecast List (Horizontal items matching reference style)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    // Small Chart Section inside glass
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(glassBg)
                            .border(1.dp, glassBorder, RoundedCornerShape(24.dp))
                    ) {
                        CustomForecastChart(
                            temps = chartData,
                            days = listOf("Today", "Wed", "Thu", "Fri", "Sat"),
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
                
                item {
                    Text(
                        "Nearby Locations",
                        color = contentColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(forecastDays) { day ->
                    ForecastRow(day, glassBg, glassBorder, contentColor)
                }
            }
        }
    }
}

@Composable
fun ForecastRow(day: ForecastDay, bg: Color, border: Color, contentColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (day.condition) {
                    "Sunny" -> Icons.Default.WbSunny
                    "Cloudy" -> Icons.Default.Cloud
                    "Rainy" -> Icons.Default.Umbrella
                    else -> Icons.Default.WbCloudy
                },
                contentDescription = null,
                tint = if (day.condition == "Sunny") Color(0xFFFFD700) else contentColor,
                modifier = Modifier.size(44.dp)
            )
            
            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = day.day,
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = day.condition,
                    color = contentColor.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }

            Text(
                text = "${day.highTemp}°",
                color = contentColor,
                fontSize = 36.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}
