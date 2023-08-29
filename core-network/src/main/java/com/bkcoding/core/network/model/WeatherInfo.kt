package com.bkcoding.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherInfoLocation(
    val lon: Double,
    val lat: Double
)

@Serializable
data class WeatherBaseInfoNetwork(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@Serializable
data class WeatherMainInfoNetwork(
    val temp: Double,
    @SerialName("feels_like")
    val feelsLike: Double,
    @SerialName("temp_min")
    val tempMin: Double,
    @SerialName("temp_max")
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int
)

@Serializable
data class WeatherWindInfoNetwork(
    val speed: Double,
    val deg: Double
)

@Serializable
data class WeatherCloudInfoNetwork(
    val all: Int
)


@Serializable
data class WeatherCountryInfoNetwork(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)


@Serializable
data class WeatherInfoNetwork(
    @SerialName("coord")
    val location: WeatherInfoLocation,
    val weather: List<WeatherBaseInfoNetwork>,
    val base: String,
    val main: WeatherMainInfoNetwork,
    val visibility: Int,
    val wind: WeatherWindInfoNetwork,
    val clouds: WeatherCloudInfoNetwork,
    val dt: Long,
    val sys: WeatherCountryInfoNetwork,
    val timezone: Int,
    val id: Long,
    val name: String,
    val cod: Int
)
