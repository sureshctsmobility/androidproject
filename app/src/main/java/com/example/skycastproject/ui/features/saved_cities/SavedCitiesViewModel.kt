package com.example.skycastproject.ui.features.saved_cities

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycastproject.data.local.dao.TrackedCityDao
import com.example.skycastproject.domain.repository.WeatherRepository
import com.example.skycastproject.ui.models.CityUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedCitiesViewModel @Inject constructor(
    private val trackedCityDao: TrackedCityDao,
    private val repository: WeatherRepository
) : ViewModel() {

    private val _citiesState = MutableStateFlow<List<CityUiModel>>(emptyList())
    val citiesState = _citiesState.asStateFlow()

    init {
        viewModelScope.launch {
            trackedCityDao.getAllTrackedCitiesFlow().collect { entities ->
                val initialList = entities.mapIndexed { i, e ->
                    CityUiModel(e.cityName + i, e.cityName, "Tracking", e.country, 20, "Loading", 22, 18, listOf(Color(0xFF4FACFE)), e.latitude, e.longitude)
                }
                _citiesState.value = initialList
                resolveLiveMetrics(initialList)
            }
        }
    }

    private fun resolveLiveMetrics(list: List<CityUiModel>) {
        viewModelScope.launch {
            val updated = list.map { model ->
                try {
                    val res = repository.fetchDirectStaticForecast(model.lat, model.lon)
                    model.copy(temp = res.currentTemp, condition = if (res.weatherCode == 0) "Sunny" else "Cloudy", high = res.dailyItems.firstOrNull()?.maxTemp ?: 25, low = res.dailyItems.firstOrNull()?.minTemp ?: 15)
                } catch(e: Exception) { model }
            }
            _citiesState.value = updated
        }
    }
}