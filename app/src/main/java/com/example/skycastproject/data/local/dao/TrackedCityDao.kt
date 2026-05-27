package com.example.skycastproject.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.skycastproject.data.local.entity.TrackedCityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackedCityDao {
    @Query("SELECT * FROM tracked_cities_table ORDER BY addedAtEpochMillis DESC")
    fun getAllTrackedCitiesFlow(): Flow<List<TrackedCityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackedCity(city: TrackedCityEntity)

    @Query("DELETE FROM tracked_cities_table WHERE cityName = :cityName")
    suspend fun removeTrackedCity(cityName: String)
}