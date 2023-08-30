package com.bkcoding.weather.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "weather_info")
data class WeatherInfoEntity(
    @PrimaryKey
    val id: Long,
    val lat: Double,
    val lon: Double,
    @ColumnInfo("city_name")
    val cityName: String,
    val country: String,
    val temp: Double,
    @ColumnInfo(name = "temp_max")
    val tempMax: Double,
    @ColumnInfo(name = "temp_min")
    val tempMin: Double,
    val description: String,
    val icon: String,
    @ColumnInfo(name = "feels_like")
    val feelsLike: Double,
    val humidity: Int,
    val visibility: Int,
    val pressure: Int,
    val wind: Double,
    val clouds: Int,
    val sunrise: Long,
    val sunset: Long,
    @ColumnInfo(name = "created_at")
    val createdAt: Date
)