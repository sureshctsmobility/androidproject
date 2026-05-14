package com.example.skycastproject.ui.features.saved_cities

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycastproject.ui.component.DraggableCityCard
import com.example.skycastproject.ui.models.CityUiModel

@Composable
fun SavedCitiesScreen(
    cities: List<CityUiModel>,
    onCityClick: (String) -> Unit,
    onAddCityClick: () -> Unit,
    isNight: Boolean = isSystemInDarkTheme()
) {
    val backgroundColor = if (isNight) Color.Black else Color.White
    val contentColor = if (isNight) Color.White else Color.Black
    val glassBg = if (isNight) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f)
    val glassBorder = if (isNight) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f)

    Scaffold(
        containerColor = backgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCityClick,
                containerColor = if (isNight) Color.White else Color.Black,
                contentColor = if (isNight) Color.Black else Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add City")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Title and Sync Info
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "My Cities",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = "${cities.size} cities · Synced 2 min ago",
                    fontSize = 14.sp,
                    color = contentColor.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Glassmorphic Search Bar (Static UI)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(glassBg)
                    .border(1.dp, glassBorder, RoundedCornerShape(20.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxHeight()
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = contentColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Search for a city or airport",
                        color = contentColor.copy(alpha = 0.4f),
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Cities List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(cities) { city ->
                    DraggableCityCard(
                        city = city,
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .border(1.dp, glassBorder, RoundedCornerShape(24.dp))
                            .clickable { onCityClick(city.name) }
                    )
                }
            }
        }
    }
}
