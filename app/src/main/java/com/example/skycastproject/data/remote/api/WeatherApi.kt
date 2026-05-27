package com.example.skycastproject.data.remote.api

import com.example.skycastproject.data.remote.dto.WeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getFullForecast(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") currentFields: String = "temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m",
        @Query("hourly") hourlyFields: String = "temperature_2m,weather_code",
        @Query("daily") dailyFields: String = "weather_code,temperature_2m_max,temperature_2m_min,uv_index_max",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponseDto
}