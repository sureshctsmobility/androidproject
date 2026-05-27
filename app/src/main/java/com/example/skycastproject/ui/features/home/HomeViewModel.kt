package com.example.skycastproject.ui.features.home

import android.content.Context
import android.location.Geocoder
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycastproject.core.common.Resource
import com.example.skycastproject.core.location.LocationTracker
import com.example.skycastproject.data.local.SkyCastDatabase
import com.example.skycastproject.data.local.entity.WeatherCacheEntity
import com.example.skycastproject.domain.usecase.GetWeatherUseCase
import com.example.skycastproject.ui.models.HomeStateWrapper
import com.example.skycastproject.ui.models.HomeUiState
import com.example.skycastproject.ui.models.HourlyForecastItem
import com.example.skycastproject.ui.widget.SkyCastWidget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val locationTracker: LocationTracker,
    private val database: SkyCastDatabase
) : ViewModel() {

    private val _state = MutableStateFlow<HomeStateWrapper>(HomeStateWrapper.Loading)
    val state = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun loadDeviceLocationWeather(context: Context) {
        viewModelScope.launch {
            _isRefreshing.value = true
            _state.value = HomeStateWrapper.Loading
            val location = locationTracker.getCurrentLocationCoordinates()
            if (location != null) {
                val actualCityName = getCityName(context, location.latitude, location.longitude)
                getWeatherUseCase(actualCityName, location.latitude, location.longitude).collect { res ->
                    when (res) {
                        is Resource.Loading -> _state.value = HomeStateWrapper.Loading
                        is Resource.Error -> _state.value = HomeStateWrapper.Error(res.throwable.localizedMessage ?: "Sync Error")
                        is Resource.Success -> {
                            val data = res.data
                            _state.value = HomeStateWrapper.Success(
                                stats = HomeUiState(
                                    cityName = data.cityName,
                                    date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                                    currentTemp = data.currentTemp,
                                    condition = mapWmoCodeToCondition(data.weatherCode),
                                    weatherCode = data.weatherCode,
                                    feelsLike = data.apparentTemp,
                                    high = data.dailyItems.firstOrNull()?.maxTemp ?: data.currentTemp,
                                    low = data.dailyItems.firstOrNull()?.minTemp ?: data.currentTemp,
                                    humidity = data.humidity, wind = data.windSpeed, uv = data.uvIndex.toInt().toString(),
                                    hourlyForecast = data.hourlyItems.map { HourlyForecastItem(it.time, it.temp, mapWmoCodeToCondition(it.code), it.code) }
                                )
                            )

                            // Synchronize the "My Location" cache entry for the Widget
                            val entity = WeatherCacheEntity(
                                cityName = data.cityName,
                                latitude = location.latitude,
                                longitude = location.longitude,
                                temperature = data.currentTemp.toDouble(),
                                apparentTemperature = data.apparentTemp.toDouble(),
                                humidity = data.humidity.filter { it.isDigit() }.toIntOrNull() ?: 0,
                                windSpeed = data.windSpeed.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0,
                                weatherCode = data.weatherCode,
                                uvIndex = data.uvIndex,
                                hourlyTimes = data.rawHourlyTimes,
                                hourlyTemps = data.rawHourlyTemps,
                                hourlyCodes = data.rawHourlyCodes,
                                dailyTimes = data.rawDailyTimes,
                                dailyHighs = data.rawDailyHighs,
                                dailyLows = data.rawDailyLows,
                                dailyCodes = data.rawDailyCodes,
                                lastUpdatedEpochMillis = System.currentTimeMillis()
                            )
                            database.weatherDao().insertWeatherCache(entity)
                            database.weatherDao().insertWeatherCache(entity.copy(cityName = "My Location"))

                            // Aggressively trigger Widget Refresh
                            SkyCastWidget().updateAll(context)
                        }
                    }
                }
            } else {
                _state.value = HomeStateWrapper.PermissionDenied
            }

            _isRefreshing.value = false
        }
    }

    private fun mapWmoCodeToCondition(code: Int): String {
        return when (code) {
            0 -> "Sunny"
            1 -> "Mainly Clear"
            2 -> "Partly Cloudy"
            3 -> "Overcast"
            45, 48 -> "Foggy"
            51, 53, 55 -> "Drizzle"
            61, 63, 65 -> "Rainy"
            66, 67 -> "Freezing Rain"
            71, 73, 75 -> "Snowy"
            77 -> "Snow Grains"
            80, 81, 82 -> "Rain Showers"
            85, 86 -> "Snow Showers"
            95, 96, 99 -> "Thunderstorm"
            else -> "Sunny"
        }
    }

    private fun getCityName(context: Context, lat: Double, lon: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            val address = addresses?.firstOrNull()
            address?.subLocality ?: address?.locality ?: address?.subAdminArea ?: "My Location"
        } catch (e: Exception) {
            "My Location"
        }
    }
}
