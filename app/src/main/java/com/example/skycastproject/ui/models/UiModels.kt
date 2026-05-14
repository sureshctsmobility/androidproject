package com.example.skycastproject.ui.models

import androidx.compose.ui.graphics.Color

data class HomeUiState(
    val cityName: String = "Chennai",
    val date: String = "Tuesday, May 5",
    val currentTemp: Int = 32,
    val condition: String = "Sunny",
    val feelsLike: Int = 36,
    val high: Int = 34,
    val low: Int = 27,
    val humidity: String = "68%",
    val wind: String = "12 km/h",
    val rain: String = "2%",
    val uv: String = "8",
    val hourlyForecast: List<HourlyForecastItem> = listOf(
        HourlyForecastItem("Now", 32, "Sunny"),
        HourlyForecastItem("11am", 33, "Sunny"),
        HourlyForecastItem("12pm", 34, "Cloudy"),
        HourlyForecastItem("1pm", 34, "Cloudy"),
        HourlyForecastItem("2pm", 33, "Partly Cloudy"),
        HourlyForecastItem("3pm", 31, "Partly Cloudy")
    )
)

data class HourlyForecastItem(
    val time: String,
    val temp: Int,
    val condition: String
)

data class ForecastDay(
    val day: String,
    val highTemp: Int,
    val lowTemp: Int,
    val condition: String,
    val precipitation: String
)

data class WeatherPreview(
    val name: String,
    val country: String,
    val lat: String,
    val lon: String,
    val temp: Int,
    val feelsLike: Int,
    val condition: String
)

data class CityUiModel(
    val id: String,
    val name: String,
    val time: String,
    val country: String,
    val temp: Int,
    val condition: String,
    val high: Int,
    val low: Int,
    val gradient: List<Color>
)
