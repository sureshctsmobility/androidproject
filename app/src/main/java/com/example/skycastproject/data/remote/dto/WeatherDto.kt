package com.example.skycastproject.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("current") val current: CurrentSpecsDto,
    @SerialName("hourly") val hourly: HourlySpecsDto,
    @SerialName("daily") val daily: DailySpecsDto
)

@Serializable
data class CurrentSpecsDto(
    @SerialName("time") val time: String,
    @SerialName("temperature_2m") val temperature2m: Double,
    @SerialName("relative_humidity_2m") val relativeHumidity2m: Int,
    @SerialName("apparent_temperature") val apparentTemperature: Double,
    @SerialName("weather_code") val weatherCode: Int,
    @SerialName("wind_speed_10m") val windSpeed10m: Double,
    @SerialName("precipitation") val precipitation: Double = 0.0
)

@Serializable
data class HourlySpecsDto(
    @SerialName("time") val time: List<String>,
    @SerialName("temperature_2m") val temperatures: List<Double>,
    @SerialName("weather_code") val weatherCodes: List<Int>
)

@Serializable
data class DailySpecsDto(
    @SerialName("time") val time: List<String>,
    @SerialName("weather_code") val weatherCodes: List<Int>,
    @SerialName("temperature_2m_max") val temperaturesMax: List<Double>,
    @SerialName("temperature_2m_min") val temperaturesMin: List<Double>,
    @SerialName("uv_index_max") val uvIndexMax: List<Double>,
    @SerialName("precipitation_probability_max") val precipitationProbabilityMax: List<Int>? = null
)
