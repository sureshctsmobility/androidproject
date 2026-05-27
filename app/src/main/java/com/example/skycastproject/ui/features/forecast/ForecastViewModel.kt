package com.example.skycastproject.ui.features.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycastproject.core.common.Resource
import com.example.skycastproject.domain.usecase.GetWeatherUseCase
import com.example.skycastproject.ui.models.ForecastDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    private val _forecastDays = MutableStateFlow<List<ForecastDay>>(emptyList())
    val forecastDays = _forecastDays.asStateFlow()

    private val _chartData = MutableStateFlow<List<Int>>(emptyList())
    val chartData = _chartData.asStateFlow()

    fun loadForecastData(cityName: String) {
        viewModelScope.launch {
            val lat = if (cityName == "Tokyo") 35.6895 else 13.0827
            val lon = if (cityName == "Tokyo") 139.6917 else 80.2707

            getWeatherUseCase(cityName, lat, lon).collect { res ->
                if (res is Resource.Success) {
                    _forecastDays.value = res.data.dailyItems.map {
                        ForecastDay(it.dayName, it.maxTemp, it.minTemp, if (it.code == 0) "Sunny" else "Cloudy", "0%")
                    }
                    _chartData.value = res.data.dailyItems.map { it.maxTemp }
                }
            }
        }
    }
}