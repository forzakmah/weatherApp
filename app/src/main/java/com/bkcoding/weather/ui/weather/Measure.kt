package com.bkcoding.weather.ui.weather

enum class Measure(
    val symbol: String
) {
    PERCENT(
        symbol = "%"
    ),
    CELSIUS(
        symbol = "°"
    ),
    HECTOPASCAL(
        symbol = " hPa"
    ),
    KILOMETER(
        symbol = " km"
    ),

    /**
     * in some cases we can found some fields without unit
     * like the UV index
     */
    NONE(symbol = "")
}