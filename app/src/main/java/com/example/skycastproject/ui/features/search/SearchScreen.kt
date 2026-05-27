package com.example.skycastproject.ui.features.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.example.skycastproject.ui.theme.glassBackground
import com.example.skycastproject.ui.theme.glassBorder

@Composable
fun SearchScreen(query: String, onQueryChange: (String) -> Unit, onBack: () -> Unit, isLoading: Boolean, searchResult: WeatherPreview?, onAddCity: () -> Unit) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.glassBackground).border(1.dp, MaterialTheme.glassBorder, RoundedCornerShape(14.dp))) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground)
                }
                Spacer(Modifier.width(16.dp))
                Text("Add Location", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            }
            Spacer(Modifier.height(24.dp))
            TextField(
                value = query, onValueChange = onQueryChange, modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.glassBorder, RoundedCornerShape(20.dp)),
                placeholder = { Text("Enter city name...", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = { if (query.isNotEmpty()) { IconButton(onClick = { onQueryChange("") }) { Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.onBackground) } } },
                colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.glassBackground, unfocusedContainerColor = MaterialTheme.glassBackground, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = MaterialTheme.colorScheme.onBackground, unfocusedTextColor = MaterialTheme.colorScheme.onBackground),
                shape = RoundedCornerShape(20.dp), singleLine = true
            )
            Spacer(Modifier.height(24.dp))
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
            }
            searchResult?.let { result ->
                val weatherIcon = when (result.condition) {
                    "Sunny", "Mainly Clear" -> Icons.Default.WbSunny
                    "Rainy", "Rain Showers", "Drizzle" -> Icons.Default.Umbrella
                    "Thunderstorm" -> Icons.Default.Thunderstorm
                    else -> Icons.Default.Cloud
                }

                val iconTint = if (result.condition == "Sunny" || result.condition == "Mainly Clear") {
                    Color(0xFFFFD700)
                } else {
                    MaterialTheme.colorScheme.onBackground
                }
                Column {
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(MaterialTheme.glassBackground).border(1.dp, MaterialTheme.glassBorder, RoundedCornerShape(28.dp)).padding(24.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = weatherIcon, null, modifier = Modifier.size(56.dp), tint = iconTint)
                            Spacer(Modifier.width(20.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(result.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                Text("${result.country} · ${result.lat}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onAddCity, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                        Text("+ Add to Tracked Cities", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}
