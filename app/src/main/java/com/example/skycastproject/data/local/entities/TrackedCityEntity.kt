package com.example.skycastproject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracked_cities_table")
data class TrackedCityEntity(
    @PrimaryKey val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val addedAtEpochMillis: Long
)