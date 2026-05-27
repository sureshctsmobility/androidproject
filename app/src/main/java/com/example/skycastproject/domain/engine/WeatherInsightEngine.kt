package com.example.skycastproject.domain.engine

import com.example.skycastproject.domain.model.BriefingForecast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherInsightEngine @Inject constructor() {

    fun generateDailyGuidance(forecast: BriefingForecast): String {
        val clothingAdvice = when {
            forecast.temperature < 15.0 -> "It's cold (${forecast.temperature.toInt()}°C). We recommend layering with a heavy jacket."
            forecast.temperature in 15.0..25.0 -> "Pleasant ${forecast.temperature.toInt()}°C conditions. Single-layer clothing is optimal today."
            else -> "Warm ambient temperatures (${forecast.temperature.toInt()}°C). Opt for light clothing."
        }
        val uvProtection = if (forecast.uvIndex >= 5.0) " High UV exposure; apply sunscreen (SPF 30+)." else ""
        val weatherWarning = if (forecast.weatherCode in listOf(61, 63, 65, 80, 81, 82)) " Rain forecasted; bring an umbrella." else ""
        return "$clothingAdvice$uvProtection$weatherWarning"
    }
}