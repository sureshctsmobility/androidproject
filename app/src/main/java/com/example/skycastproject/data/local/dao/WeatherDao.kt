package com.example.skycastproject.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.skycastproject.data.local.entity.WeatherCacheEntity

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_cache_table WHERE cityName = :cityName LIMIT 1")
    suspend fun getWeatherCacheByCity(cityName: String): WeatherCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherCache(cache: WeatherCacheEntity)

    @Query("DELETE FROM weather_cache_table WHERE cityName = :cityName")
    suspend fun clearWeatherCacheForCity(cityName: String)

    // Gets the single most recently updated record, regardless of city name
    @Query("SELECT * FROM weather_cache_table ORDER BY lastUpdatedEpochMillis DESC LIMIT 1")
    suspend fun getAbsoluteLatestCache(): WeatherCacheEntity?

    // Gets the latest update that has a specific city name (not the generic "My Location")
    @Query("SELECT * FROM weather_cache_table WHERE cityName != 'My Location' ORDER BY lastUpdatedEpochMillis DESC LIMIT 1")
    suspend fun getLatestSpecificCityCache(): WeatherCacheEntity?
}
