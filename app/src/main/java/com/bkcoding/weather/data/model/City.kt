package com.bkcoding.weather.data.model

import com.bkcoding.core.network.model.NetworkCity
import com.bkcoding.weather.db.entity.CityEntity
import java.util.Date

data class City(
    val name: String,
    val lat: Double,
    val lon: Double,
    val state: String,
    val country: String
)

fun City.asEntity(): CityEntity {
    return CityEntity(
        id = 0,
        name = name,
        lat = lat,
        lon = lon,
        state = state,
        country = country,
        createdAt = Date()
    )
}

fun CityEntity.asExternalModel(): City {
    return City(
        name = name,
        lat = lat,
        lon = lon,
        state = state,
        country = country
    )
}

fun NetworkCity.asExternalModel(): City {
    return City(
        name = name,
        lat = lat,
        lon = lon,
        state = state,
        country = country
    )
}