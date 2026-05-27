package com.example.skycastproject.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResponseDto(
    @SerialName("results") val results: List<RemoteCityMetadataDto>? = null
)

@Serializable
data class RemoteCityMetadataDto(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("country") val country: String,
    @SerialName("country_code") val countryCode: String,
    @SerialName("admin1") val admin1: String? = null
)