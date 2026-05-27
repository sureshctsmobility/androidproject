package com.example.skycastproject.ui.features.forecast

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycastproject.core.common.Resource
import com.example.skycastproject.core.location.LocationTracker
import com.example.skycastproject.domain.usecase.GetWeatherUseCase
import com.example.skycastproject.ui.models.ForecastDay
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val locationTracker: LocationTracker,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _forecastDays = MutableStateFlow<List<ForecastDay>>(emptyList())
    val forecastDays = _forecastDays.asStateFlow()

    private val _chartData = MutableStateFlow<List<Int>>(emptyList())
    val chartData = _chartData.asStateFlow()

    fun loadForecastData(cityName: String) {
        viewModelScope.launch {
            val isGeneric = cityName == "Current" || cityName == "My Location" || cityName == "Current Location"
            val coords = if (isGeneric) locationTracker.getCurrentLocationCoordinates() else null

            // Geocode to get a real name if we are looking at "Current"
            val resolvedName = if (isGeneric && coords != null) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(coords.latitude, coords.longitude, 1)
                    val address = addresses?.firstOrNull()
                    address?.subLocality ?: address?.locality ?: address?.subAdminArea ?: cityName
                } catch (e: Exception) { cityName }
            } else cityName

            val lat = coords?.latitude ?: if (cityName == "Tokyo") 35.6895 else 13.0827
            val lon = coords?.longitude ?: if (cityName == "Tokyo") 139.6917 else 80.2707

            getWeatherUseCase(resolvedName, lat, lon).collect { res ->
                if (res is Resource.Success) {
                    _forecastDays.value = res.data.dailyItems.map {
                        ForecastDay(it.dayName, it.maxTemp, it.minTemp, mapWmoToCondition(it.code), "0%")
                    }
                    _chartData.value = res.data.dailyItems.map { it.maxTemp }
                }
            }
        }
    }

    private fun mapWmoToCondition(code: Int): String {
        return when (code) {
            0 -> "Sunny"
            1, 2, 3 -> "Cloudy"
            45, 48 -> "Foggy"
            51, 53, 55, 61, 63, 65, 80, 81, 82 -> "Rainy"
            95, 96, 99 -> "Thunderstorm"
            else -> "Sunny"
        }
    }
}
