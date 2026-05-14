package com.example.skycastproject.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomForecastChart(
    temps: List<Int>,
    days: List<String>,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TEMP TREND (°C)",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (temps.size < 2) return@Canvas

                val spacing = size.width / (temps.size - 1)
                val maxTemp = (temps.maxOrNull() ?: 1) + 2
                val minTemp = (temps.minOrNull() ?: 0) - 2
                val range = (maxTemp - minTemp).coerceAtLeast(1)

                val bottomOffset = with(density) { 40.dp.toPx() }
                val topPadding = with(density) { 20.dp.toPx() }
                val usableHeight = size.height - bottomOffset - topPadding

                val points = temps.mapIndexed { index, temp ->
                    Offset(
                        x = index * spacing,
                        y = (size.height - bottomOffset) - ((temp - minTemp).toFloat() / range * usableHeight)
                    )
                }

                // Create fill path
                val fillPath = Path().apply {
                    moveTo(points.first().x, size.height - with(density) { 20.dp.toPx() })
                    lineTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        cubicTo(
                            points[i - 1].x + spacing / 2, points[i - 1].y,
                            points[i].x - spacing / 2, points[i].y,
                            points[i].x, points[i].y
                        )
                    }
                    lineTo(points.last().x, size.height - with(density) { 20.dp.toPx() })
                    close()
                }

                 //Create stroke path
                val strokePath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        cubicTo(
                            points[i - 1].x + spacing / 2, points[i - 1].y,
                            points[i].x - spacing / 2, points[i].y,
                            points[i].x, points[i].y
                        )
                    }
                }

                // Draw gradient fill
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFD700).copy(alpha = 0.3f),
                            Color.Transparent
                        ),
                        startY = points.minOf { it.y },
                        endY = size.height
                    )
                )



                // Draw curve
                drawPath(
                    path = strokePath,
                    color = Color(0xFFFFD700),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw points
                points.forEach { point ->
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = Color(0xFFFFD700),
                        radius = 2.dp.toPx(),
                        center = point
                    )
                }
            }
            
            // Temperature labels overlay
            Row(
                modifier = Modifier.fillMaxWidth().height(140.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                temps.forEachIndexed { index, temp ->
                    val maxTemp = (temps.maxOrNull() ?: 1) + 2
                    val minTemp = (temps.minOrNull() ?: 0) - 2
                    val range = (maxTemp - minTemp).coerceAtLeast(1)
                    
                    val yPosRatio = (temp - minTemp).toFloat() / range
                    // Matching the Canvas logic: (size.height - 40.dp) - (ratio * (size.height - 60.dp))
                    // Here we use weights for X and approximate Y in Dp
                    val yOffsetDp = 100 - (yPosRatio * 80) // Approximate mapping

                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        Text(
                            text = "${temp}°",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = yOffsetDp.dp)
                        )
                    }
                }
            }
        }

        // Days labels
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEach { day ->
                Text(
                    text = day,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}
