package com.example.skycastproject.ui.features.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycastproject.ui.models.HomeUiState

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onRefresh: () -> Unit = {},
    onNavigateToForecast: () -> Unit = {},
    isNight: Boolean = isSystemInDarkTheme()
) {
    val backgroundColor = if (isNight) Color.Black else Color.White
    val contentColor = if (isNight) Color.White else Color.Black
    val glassBg = if (isNight) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f)
    val glassBorder = if (isNight) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.1f)
    
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,

            ) {
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(glassBg)
                        .border(1.dp, glassBorder, RoundedCornerShape(14.dp))
                ) {
                    Icon(Icons.Default.GridView, null, tint = contentColor, modifier = Modifier.size(20.dp))
                }
                
                Spacer(Modifier.weight(1f))
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = Color.Red, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = uiState.cityName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    }
                    Text(
                        text = "Updating",
                        color = contentColor.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }
                
                Spacer(Modifier.weight(1f))

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(glassBg)
                        .border(1.dp, glassBorder, RoundedCornerShape(14.dp))
                ) {
                    Icon(Icons.Default.MoreVert, null, tint = contentColor, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(32.dp))

            // Main Weather Glass Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(glassBg)
                    .border(1.dp, glassBorder, RoundedCornerShape(40.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (uiState.condition == "Sunny") Icons.Default.WbSunny else Icons.Default.Thunderstorm,
                        contentDescription = null,
                        tint = if (uiState.condition == "Sunny") Color(0xFFFFD700) else contentColor,
                        modifier = Modifier.size(140.dp)
                    )
                    Text(
                        text = "${uiState.currentTemp}°",
                        fontSize = 90.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    Text(
                        text = uiState.condition,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = contentColor
                    )
                    Text(
                        text = uiState.date,
                        color = contentColor.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Air Quality Glass Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(glassBg)
                    .border(1.dp, glassBorder, RoundedCornerShape(32.dp))
            ) {
                Column(modifier = Modifier.padding(24.dp),
                    ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudQueue, null, tint = contentColor, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("Air Quality", color = contentColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            DetailItem("Real Feel", "${uiState.feelsLike}°", contentColor)
                            Spacer(Modifier.height(16.dp))
                            DetailItem("Chance of Rain", uiState.rain, contentColor)
                        }
                        Column {
                            DetailItem("Wind", uiState.wind, contentColor)
                            Spacer(Modifier.height(16.dp))
                            DetailItem("UV Index", uiState.uv, contentColor)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Hourly Glass Scroll - Fixed height ensures content is not cut
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(uiState.hourlyForecast) { item ->
                    val isNow = item.time == "Now"
                    Box(
                        modifier = Modifier
                            .width(68.dp)
                            .height(120.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (isNow) contentColor else glassBg)
                            .border(1.dp, glassBorder, RoundedCornerShape(22.dp))
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                item.time, 
                                color = if (isNow) backgroundColor else contentColor.copy(alpha = 0.6f), 
                                fontSize = 12.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "${item.temp}°", 
                                color = if (isNow) backgroundColor else contentColor, 
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Icon(
                                imageVector = if (item.condition == "Sunny") Icons.Default.WbSunny else Icons.Default.Cloud,
                                null,
                                tint = if (isNow) backgroundColor else (if (item.condition == "Sunny") Color(0xFFFFD700) else contentColor),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, contentColor: Color) {
    Column {
        Text(text = label, fontSize = 12.sp, color = contentColor.copy(alpha = 0.5f))
        Text(text = value, fontWeight = FontWeight.Bold, color = contentColor, fontSize = 16.sp)
    }
}
