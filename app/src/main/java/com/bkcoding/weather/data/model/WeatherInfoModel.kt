package com.bkcoding.weather.data.model

import com.bkcoding.core.network.model.WeatherInfoNetwork
import com.bkcoding.weather.db.entity.WeatherInfoEntity
import java.util.Date

data class WeatherInfoModel(
    val id: Long,
    val lat: Double,
    val lon: Double,
    val cityName: String,
    val country: String,
    val temp: Double,
    val tempMax: Double,
    val tempMin: Double,
    val description: String,
    val icon: String,
    val feelsLike: Double,
    val humidity: Int,
    val visibility: Int,
    val pressure: Int,
    val wind: Double,
    val clouds: Int,
    val sunrise: Long,
    val sunset: Long,
    val createdAt: Date
)

fun WeatherInfoNetwork.asEntity(): WeatherInfoEntity {
    return WeatherInfoEntity(
        id = this.id,
        lat = this.location.lat,
        lon = this.location.lon,
        cityName = this.name,
        country = this.sys.country,
        temp = this.main.temp,
        tempMax = this.main.tempMax,
        tempMin = this.main.tempMin,
        description = this.weather.first().description,
        icon = this.weather.first().icon,
        feelsLike = this.main.feelsLike,
        humidity = this.main.humidity,
        visibility = this.visibility,
        pressure = this.main.pressure,
        wind = this.wind.deg,
        clouds = this.clouds.all,
        sunrise = this.sys.sunrise,
        sunset = this.sys.sunset,
        createdAt = Date()
    )
}

fun WeatherInfoEntity.asExternalModel(): WeatherInfoModel {
    return WeatherInfoModel(
        id = id,
        lat = lat,
        lon = lon,
        cityName = cityName,
        country = country,
        temp = temp,
        tempMax = tempMax,
        tempMin = tempMin,
        description = description,
        icon = icon,
        feelsLike = feelsLike,
        humidity = humidity,
        visibility = visibility,
        pressure = pressure,
        wind = wind,
        clouds = clouds,
        sunrise = sunrise,
        sunset = sunset,
        createdAt = createdAt
    )
}