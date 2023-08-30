package com.bkcoding.weather.data.model

import com.bkcoding.core.network.model.NetworkCity

data class City(
    val id: Long,
    val name: String,
    val lat: Double,
    val lon: Double,
    val state: String,
    val country: String
)

fun NetworkCity.asExternalModel(): City {
    return City(
        id = 0,
        name = name,
        lat = lat,
        lon = lon,
        state = state,
        country = country
    )
}