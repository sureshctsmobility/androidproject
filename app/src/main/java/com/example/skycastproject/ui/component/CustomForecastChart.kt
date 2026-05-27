package com.example.skycastproject.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomForecastChart(temps: List<Int>, days: List<String>, modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    if (temps.isEmpty() || days.isEmpty()) return

    // FIX: Read Composable theme colors here, completely outside the Canvas DrawScope
    val themeBackgroundColor = MaterialTheme.colorScheme.background

    Column(modifier = modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "TEMPERATURE TREND (°C)", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (temps.size < 2) return@Canvas
                val spacing = size.width / (temps.size - 1)
                val maxTemp = (temps.maxOrNull() ?: 1) + 2
                val minTemp = (temps.minOrNull() ?: 0) - 2
                val range = (maxTemp - minTemp).coerceAtLeast(1)

                val bottomOffset = with(density) { 30.dp.toPx() }
                val topPadding = with(density) { 20.dp.toPx() }
                val usableHeight = size.height - bottomOffset - topPadding

                val points = temps.mapIndexed { index, temp ->
                    Offset(
                        x = index * spacing,
                        y = (size.height - bottomOffset) - ((temp - minTemp).toFloat() / range * usableHeight)
                    )
                }

                val strokePath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                }
                drawPath(path = strokePath, color = Color(0xFFFFD700), style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                points.forEach { p ->
                    // FIX: Reference the safe, locally cached variable inside DrawScope loops
                    drawCircle(color = themeBackgroundColor, radius = 5.dp.toPx(), center = p)
                    drawCircle(color = Color(0xFFFFD700), radius = 3.dp.toPx(), center = p)
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            days.take(temps.size).forEach { d ->
                Text(text = d, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }
    }
}