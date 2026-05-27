package com.example.skycastproject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache_table")
data class WeatherCacheEntity(
    @PrimaryKey val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val temperature: Double,
    val apparentTemperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val weatherCode: Int,
    val uvIndex: Double,
    val hourlyTimes: String,
    val hourlyTemps: String,
    val hourlyCodes: String,
    val dailyTimes: String,
    val dailyHighs: String,
    val dailyLows: String,
    val dailyCodes: String,
    val lastUpdatedEpochMillis: Long
)