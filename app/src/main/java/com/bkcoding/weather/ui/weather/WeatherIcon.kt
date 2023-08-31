package com.bkcoding.weather.ui.weather

import androidx.annotation.DrawableRes
import com.bkcoding.weather.R

enum class WeatherIcon(
    @DrawableRes val drawable: Int,
    val value: String,
) {
    CLEAR_SKY_DAY(
        drawable = R.drawable.clear_day,
        value = "01d"
    ),
    CLEAR_SKY_NIGHT(
        drawable = R.drawable.clear_night,
        value = "01n"
    ),
    FEW_CLOUDS_DAY(
        drawable = R.drawable.few_clouds_day,
        value = "02d"
    ),
    FEW_CLOUDS_NIGHT(
        drawable = R.drawable.few_clouds_night,
        value = "02n"
    ),
    SCATTERED_CLOUDS_DAY(
        drawable = R.drawable.scattered_clouds,
        value = "03d"
    ),
    SCATTERED_CLOUDS_NIGHT(
        drawable = R.drawable.scattered_clouds,
        value = "03n"
    ),
    BROKEN_CLOUDS_DAY(
        drawable = R.drawable.broken_clouds,
        value = "04d"
    ),
    BROKEN_CLOUDS_NIGHT(
        drawable = R.drawable.broken_clouds,
        value = "04n"
    ),
    SHOWER_RAIN_DAY(
        drawable = R.drawable.shower_rain,
        value = "09d"
    ),
    SHOWER_RAIN_NIGHT(
        drawable = R.drawable.shower_rain,
        value = "09n"
    ),
    RAIN_DAY(
        drawable = R.drawable.rain_day,
        value = "10d"
    ),
    RAIN_NIGHT(
        drawable = R.drawable.rain_night,
        value = "10n"
    ),
    THUNDERSTORM_DAY(
        drawable = R.drawable.thunderstorm,
        value = "11d"
    ),
    THUNDERSTORM_NIGHT(
        drawable = R.drawable.thunderstorm,
        value = "11n"
    ),
    SNOW_DAY(
        drawable = R.drawable.snow,
        value = "13d"
    ),
    SNOW_NIGHT(
        drawable = R.drawable.snow,
        value = "13n"
    ),
    MIST_DAY(
        drawable = R.drawable.mist,
        value = "50d"
    ),
    MIST_NIGHT(
        drawable = R.drawable.mist,
        value = "50n"
    ),
    UNKNOWN(
        drawable = R.drawable.scattered_clouds,
        value = "00"
    );

    companion object {
        fun find(key: String): WeatherIcon {
            return WeatherIcon.values().find { it.value == key } ?: UNKNOWN
        }
    }
}