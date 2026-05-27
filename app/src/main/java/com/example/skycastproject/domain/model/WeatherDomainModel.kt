package com.example.skycastproject.domain.model

data class WeatherDomainModel(
    val cityName: String,
    val currentTemp: Int,
    val apparentTemp: Int,
    val humidity: String,
    val windSpeed: String,
    val weatherCode: Int,
    val uvIndex: Double,
    val hourlyItems: List<DomainHourlyItem>,
    val dailyItems: List<DomainDailyItem>,
    val rawHourlyTimes: String = "",
    val rawDailyTimes: String = "",
    val rawHourlyTemps: String = "",
    val rawHourlyCodes: String = "",
    val rawDailyHighs: String = "",
    val rawDailyLows: String = "",
    val rawDailyCodes: String = ""
)

data class DomainHourlyItem(val time: String, val temp: Int, val code: Int, val rawTime: String = "")
data class DomainDailyItem(val dayName: String, val maxTemp: Int, val minTemp: Int, val code: Int, val rawDate: String = "")