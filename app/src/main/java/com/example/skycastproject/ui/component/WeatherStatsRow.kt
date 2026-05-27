//package com.example.skycastproject.ui.component
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//
//
//@Composable
//fun WeatherStatsRow(
//    humidity: String,
//    windSpeed: String,
//    rainChance: String,
//    uvIndex: String,
//    modifier: Modifier = Modifier
//) {
//    Surface(
//        color = Color.White.copy(alpha = 0.2f),
//        shape = RoundedCornerShape(24.dp),
//        modifier = modifier.fillMaxWidth()
//    ) {
//        Row(
//            modifier = Modifier.padding(20.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            StatItem(label = "HUMIDITY", value = humidity)
//            StatItem(label = "WIND", value = windSpeed)
//            StatItem(label = "RAIN", value = rainChance)
//            StatItem(label = "UV INDEX", value = uvIndex)
//        }
//    }
//}
//
//@Composable
//private fun StatItem(label: String, value: String) {
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        Text(text = value, fontWeight = FontWeight.Bold, color = Color.White)
//        Text(text = label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
//    }
//}

package com.example.skycastproject.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycastproject.ui.theme.glassBackground
import com.example.skycastproject.ui.theme.glassBorder

@Composable
fun WeatherStatsRow(humidity: String, windSpeed: String, rainChance: String, uvIndex: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.glassBackground)
            .border(1.dp, MaterialTheme.glassBorder, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StatItem(label = "HUMIDITY", value = humidity)
            StatItem(label = "WIND", value = windSpeed)
            StatItem(label = "RAIN", value = rainChance)
            StatItem(label = "UV INDEX", value = uvIndex)
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
    }
}