package com.example.skycastproject.data.remote.api

import com.example.skycastproject.data.remote.dto.GeocodingResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("v1/search")
    suspend fun searchCities(
        @Query("name") cityName: String,
        @Query("count") maxResults: Int = 10,
        @Query("format") format: String = "json",
        @Query("language") language: String = "en"
    ): GeocodingResponseDto
}