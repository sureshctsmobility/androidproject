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

    @Query("SELECT * FROM weather_cache_table ORDER BY lastUpdatedEpochMillis DESC LIMIT 1")
    suspend fun getAbsoluteLatestCache(): WeatherCacheEntity?

    /**
     * Retrieves the latest weather cache entry that has a specific, named city.
     * This avoids using generic entries like "My Location" when a more precise one is available.
     */
    @Query("SELECT * FROM weather_cache_table WHERE cityName NOT IN ('My Location', 'Current Location', 'Current', 'Cache Mapping Container') ORDER BY lastUpdatedEpochMillis DESC LIMIT 1")
    suspend fun getLatestNamedCityCache(): WeatherCacheEntity?
}
