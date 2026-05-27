package com.example.skycastproject.ui.models

import androidx.compose.ui.graphics.Color

sealed interface HomeStateWrapper {
    object Loading : HomeStateWrapper
    object PermissionDenied : HomeStateWrapper
    data class Error(val explanatoryMessage: String) : HomeStateWrapper
    data class Success(val stats: HomeUiState) : HomeStateWrapper
}

data class HomeUiState(
    val cityName: String = "Current Location",
    val date: String = "",
    val currentTemp: Int = 20,
    val condition: String = "Cloudy",
    val weatherCode: Int = 0,
    val feelsLike: Int = 20,
    val high: Int = 22,
    val low: Int = 18,
    val humidity: String = "50%",
    val wind: String = "10 km/h",
    val rain: String = "0%",
    val uv: String = "5",
    val hourlyForecast: List<HourlyForecastItem> = emptyList()
)

data class HourlyForecastItem(val time: String, val temp: Int, val condition: String, val weatherCode: Int = 0)
data class ForecastDay(val day: String, val highTemp: Int, val lowTemp: Int, val condition: String, val precipitation: String)
data class WeatherPreview(val name: String, val country: String, val lat: String, val lon: String, val temp: Int, val feelsLike: Int, val condition: String)
data class CityUiModel(val id: String, val name: String, val time: String, val country: String, val temp: Int, val condition: String, val high: Int, val low: Int, val gradient: List<Color>, val lat: Double = 0.0, val lon: Double = 0.0)