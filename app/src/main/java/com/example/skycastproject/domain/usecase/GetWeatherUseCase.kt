package com.example.skycastproject.domain.usecase

import com.example.skycastproject.core.common.Resource
import com.example.skycastproject.domain.model.WeatherDomainModel
import com.example.skycastproject.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(cityName: String, lat: Double, lon: Double): Flow<Resource<WeatherDomainModel>> {
        return repository.fetchReactiveForecast(cityName, lat, lon)
    }
}