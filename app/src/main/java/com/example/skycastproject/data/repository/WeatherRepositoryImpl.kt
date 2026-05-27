package com.example.skycastproject.data.repository

import com.example.skycastproject.core.common.Resource
import com.example.skycastproject.data.local.dao.WeatherDao
import com.example.skycastproject.data.local.entity.WeatherCacheEntity
import com.example.skycastproject.data.remote.api.WeatherApi
import com.example.skycastproject.domain.model.DomainDailyItem
import com.example.skycastproject.domain.model.DomainHourlyItem
import com.example.skycastproject.domain.model.WeatherDomainModel
import com.example.skycastproject.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherDao: WeatherDao,
    private val weatherApi: WeatherApi
) : WeatherRepository {

    private val cacheTtlTimeout = TimeUnit.MINUTES.toMillis(10)

    override fun fetchReactiveForecast(cityName: String, lat: Double, lon: Double): Flow<Resource<WeatherDomainModel>> = flow {
        emit(Resource.Loading)
        val localCache = weatherDao.getWeatherCacheByCity(cityName)
        val isFresh = localCache != null && (System.currentTimeMillis() - localCache.lastUpdatedEpochMillis < cacheTtlTimeout)

        if (isFresh) {
            // Standardize: Update timestamp to mark this as the most recently viewed city
            val freshCache = localCache!!.copy(lastUpdatedEpochMillis = System.currentTimeMillis())
            weatherDao.insertWeatherCache(freshCache)
            // Ensure generic reference is also updated for the Widget
            weatherDao.insertWeatherCache(freshCache.copy(cityName = "My Location"))
            
            emit(Resource.Success(freshCache.toDomainRepresentation()))
        } else {
            try {
                val networkResponse = weatherApi.getFullForecast(lat, lon)
                val newCache = WeatherCacheEntity(
                    cityName = cityName, latitude = lat, longitude = lon,
                    temperature = networkResponse.current.temperature2m,
                    apparentTemperature = networkResponse.current.apparentTemperature,
                    humidity = networkResponse.current.relativeHumidity2m,
                    windSpeed = networkResponse.current.windSpeed10m,
                    weatherCode = networkResponse.current.weatherCode,
                    uvIndex = networkResponse.daily.uvIndexMax.firstOrNull() ?: 0.0,
                    hourlyTimes = networkResponse.hourly.time.joinToString(","),
                    hourlyTemps = networkResponse.hourly.temperatures.joinToString(","),
                    hourlyCodes = networkResponse.hourly.weatherCodes.joinToString(","),
                    dailyTimes = networkResponse.daily.time.joinToString(","),
                    dailyHighs = networkResponse.daily.temperaturesMax.joinToString(","),
                    dailyLows = networkResponse.daily.temperaturesMin.joinToString(","),
                    dailyCodes = networkResponse.daily.weatherCodes.joinToString(","),
                    lastUpdatedEpochMillis = System.currentTimeMillis()
                )
                weatherDao.insertWeatherCache(newCache)
                weatherDao.insertWeatherCache(newCache.copy(cityName = "My Location"))
                
                emit(Resource.Success(newCache.toDomainRepresentation()))
            } catch (e: Exception) {
                if (localCache != null) emit(Resource.Success(localCache.toDomainRepresentation()))
                else emit(Resource.Error(e))
            }
        }
    }

    override suspend fun fetchDirectStaticForecast(lat: Double, lon: Double): WeatherDomainModel {
        val dto = weatherApi.getFullForecast(lat, lon)
        val entity = WeatherCacheEntity(
            cityName = "Cache Mapping Container", latitude = lat, longitude = lon,
            temperature = dto.current.temperature2m, apparentTemperature = dto.current.apparentTemperature,
            humidity = dto.current.relativeHumidity2m, windSpeed = dto.current.windSpeed10m,
            weatherCode = dto.current.weatherCode, uvIndex = dto.daily.uvIndexMax.firstOrNull() ?: 0.0,
            hourlyTimes = dto.hourly.time.joinToString(","), hourlyTemps = dto.hourly.temperatures.joinToString(","),
            hourlyCodes = dto.hourly.weatherCodes.joinToString(","), dailyTimes = dto.daily.time.joinToString(","),
            dailyHighs = dto.daily.temperaturesMax.joinToString(","), dailyLows = dto.daily.temperaturesMin.joinToString(","),
            dailyCodes = dto.daily.weatherCodes.joinToString(","), lastUpdatedEpochMillis = System.currentTimeMillis()
        )
        return entity.toDomainRepresentation()
    }
}

private fun WeatherCacheEntity.toDomainRepresentation(): WeatherDomainModel {
    val rawTimes = hourlyTimes.split(",")
    val rawTemps = hourlyTemps.split(",")
    val rawCodes = hourlyCodes.split(",")
    
    val now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0)
    
    var startIndex = 0
    for (i in rawTimes.indices) {
        try {
            val timeStr = rawTimes[i].replace(" ", "T")
            val parsed = LocalDateTime.parse(timeStr)
            if (!parsed.isBefore(now)) {
                startIndex = i
                break
            }
        } catch (e: Exception) {}
    }

    val mappedHourly = mutableListOf<DomainHourlyItem>()
    for (offset in 0 until 24) {
        val targetIndex = startIndex + offset
        if (targetIndex < rawTimes.size && targetIndex < rawTemps.size) {
            val timeStr = rawTimes[targetIndex].replace(" ", "T")
            val formattedLabel = try {
                val parsed = LocalDateTime.parse(timeStr)
                parsed.format(DateTimeFormatter.ofPattern("h a")).lowercase()
            } catch(e: Exception) { "" }
            
            mappedHourly.add(
                DomainHourlyItem(
                    time = if (offset == 0) "Now" else formattedLabel,
                    temp = rawTemps[targetIndex].toDoubleOrNull()?.toInt() ?: 0, 
                    code = if (targetIndex < rawCodes.size) rawCodes[targetIndex].toIntOrNull() ?: 0 else 0
                )
            )
        }
    }

    val dDays = dailyTimes.split(",")
    val dHighs = dailyHighs.split(",")
    val dLows = dailyLows.split(",")
    val dCodes = dailyCodes.split(",")
    val mappedDaily = dDays.mapIndexedNotNull { i, dayStr ->
        if (i >= 5 || i >= dHighs.size) return@mapIndexedNotNull null
        val label = try {
            val parsed = LocalDateTime.parse(dayStr + "T00:00:00")
            parsed.format(DateTimeFormatter.ofPattern("E"))
        } catch(e: Exception) { "Day" }
        DomainDailyItem(
            dayName = label, 
            maxTemp = dHighs[i].toDoubleOrNull()?.toInt() ?: 0, 
            minTemp = dLows[i].toDoubleOrNull()?.toInt() ?: 0, 
            code = if (i < dCodes.size) dCodes[i].toIntOrNull() ?: 0 else 0
        )
    }

    return WeatherDomainModel(
        cityName = cityName, currentTemp = temperature.toInt(), apparentTemp = apparentTemperature.toInt(),
        humidity = "$humidity%", windSpeed = "$windSpeed km/h", weatherCode = weatherCode, uvIndex = uvIndex,
        hourlyItems = mappedHourly, dailyItems = mappedDaily,
        rawHourlyTimes = hourlyTimes, rawHourlyTemps = hourlyTemps, rawHourlyCodes = hourlyCodes,
        rawDailyTimes = dailyTimes, rawDailyHighs = dailyHighs, rawDailyLows = dailyLows, rawDailyCodes = dailyCodes
    )
}
