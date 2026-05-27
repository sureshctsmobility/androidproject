package com.example.skycastproject.domain.model

data class BriefingForecast(
    val temperature: Double,
    val apparentTemperature: Double,
    val relativeHumidity: Int,
    val windSpeed: Double,
    val weatherCode: Int,
    val uvIndex: Double
)