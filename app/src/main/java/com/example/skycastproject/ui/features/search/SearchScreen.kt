package com.example.skycastproject.ui.features.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycastproject.ui.models.WeatherPreview

@Composable
fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean = false,
    searchResult: WeatherPreview? = null,
    onAddCity: () -> Unit = {},
    recentSearches: List<String> = listOf("Singapore", "Bangalore"),
    onRecentSearchClick: (String) -> Unit = {},
    isNight: Boolean = isSystemInDarkTheme()
) {
    val backgroundColor = if (isNight) Color.Black else Color.White
    val contentColor = if (isNight) Color.White else Color.Black
    val glassBg = if (isNight) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f)
    val glassBorder = if (isNight) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.1f)

    Scaffold(
        containerColor = backgroundColor,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // Header with Glass Button
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                        tint = contentColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "Add City",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }

            Spacer(Modifier.height(24.dp))

            // Glassmorphic Search Bar
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, glassBorder, RoundedCornerShape(20.dp)),
                placeholder = { Text("tokyo", color = contentColor.copy(alpha = 0.3f)) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF4A90E2)) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Default.Close, null, tint = contentColor.copy(alpha = 0.5f))
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = glassBg,
                    unfocusedContainerColor = glassBg,
                    disabledContainerColor = glassBg,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = contentColor,
                    unfocusedTextColor = contentColor
                ),
                shape = RoundedCornerShape(20.dp),
                singleLine = true
            )

            Spacer(Modifier.height(24.dp))

            // Loading state
            if (isLoading) {
                Surface(
                    color = if (isNight) Color(0xFFFFF9C4).copy(alpha = 0.15f) else Color(0xFFFFF9C4),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    border = androidx.compose.foundation.BorderStroke(1.dp, glassBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFFFBC02D),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = "Fetching weather data from API...",
                            color = if (isNight) Color(0xFFFFF9C4) else Color(0xFF827717),
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // Search Result Card
            searchResult?.let { result ->
                SearchResultCard(result, onAddCity, isNight)
                Spacer(Modifier.height(32.dp))
            }

            // Recent searches header
            Text(
                text = "RECENT SEARCHES",
                color = contentColor.copy(alpha = 0.4f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recentSearches) { city ->
                    RecentSearchItem(city, onRecentSearchClick, isNight)
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(result: WeatherPreview, onAddCity: () -> Unit, isNight: Boolean) {
    val contentColor = if (isNight) Color.White else Color.Black
    val glassBg = if (isNight) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f)
    val glassBorder = if (isNight) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.1f)

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(glassBg)
                .border(1.dp, glassBorder, RoundedCornerShape(28.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SEARCH RESULT · LIVE",
                    color = contentColor.copy(alpha = 0.4f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Cloud,
                        null,
                        modifier = Modifier.size(56.dp),
                        tint = contentColor
                    )

                    Spacer(Modifier.width(20.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(result.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = contentColor)
                        Text("${result.country} · ${result.lat}", fontSize = 12.sp, color = contentColor.copy(alpha = 0.6f))
                        Text(
                            text = "${result.condition} · Feels like ${result.feelsLike}°",
                            fontSize = 14.sp,
                            color = Color(0xFF4A90E2),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = "${result.temp}°",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Light,
                        color = contentColor
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onAddCity,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("+ Add to My Cities", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun RecentSearchItem(city: String, onClick: (String) -> Unit, isNight: Boolean) {
    val contentColor = if (isNight) Color.White else Color.Black
    val glassBg = if (isNight) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f)
    val glassBorder = if (isNight) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(glassBg)
            .border(1.dp, glassBorder, RoundedCornerShape(18.dp))
            .clickable { onClick(city) }
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Schedule, null, tint = contentColor.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(16.dp))
            Text(text = city, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = contentColor.copy(alpha = 0.7f), modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = contentColor.copy(alpha = 0.3f))
        }
    }
}
