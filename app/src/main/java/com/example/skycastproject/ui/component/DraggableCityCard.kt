package com.example.skycastproject.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycastproject.ui.models.CityUiModel

@Composable
fun DraggableCityCard(
    city: CityUiModel,
    modifier: Modifier = Modifier,
    isNight: Boolean = isSystemInDarkTheme()
) {
    val contentColor = if (isNight) Color.White else Color.Black
    val glassBg = if (isNight) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f)
    val glassBorder = if (isNight) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(glassBg)
            .border(1.dp, glassBorder, RoundedCornerShape(28.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Left Side: City Info
        Column(
            modifier = Modifier.align(Alignment.TopStart),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = city.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = "${city.time} · ${city.country}",
                fontSize = 12.sp,
                color = contentColor.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = city.condition,
                fontSize = 14.sp,
                color = contentColor.copy(alpha = 0.8f)
            )
        }

        // Right Side: Temperature Info
        Column(
            modifier = Modifier.align(Alignment.TopEnd),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${city.temp}°",
                fontSize = 44.sp,
                fontWeight = FontWeight.Light,
                color = contentColor
            )
            Text(
                text = "H:${city.high}° L:${city.low}°",
                fontSize = 12.sp,
                color = contentColor.copy(alpha = 0.5f)
            )
        }
    }
}
