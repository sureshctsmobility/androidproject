package com.example.skycastproject.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.skycastproject.data.local.dao.TrackedCityDao
import com.example.skycastproject.data.local.dao.WeatherDao
import com.example.skycastproject.data.local.entity.TrackedCityEntity
import com.example.skycastproject.data.local.entity.WeatherCacheEntity

@Database(
    entities = [WeatherCacheEntity::class, TrackedCityEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SkyCastDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun trackedCityDao(): TrackedCityDao
}