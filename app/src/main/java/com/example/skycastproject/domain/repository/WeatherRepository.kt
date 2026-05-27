package com.example.skycastproject.domain.repository

import com.example.skycastproject.core.common.Resource
import com.example.skycastproject.domain.model.WeatherDomainModel
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun fetchReactiveForecast(cityName: String, lat: Double, lon: Double): Flow<Resource<WeatherDomainModel>>
    suspend fun fetchDirectStaticForecast(lat: Double, lon: Double): WeatherDomainModel
}