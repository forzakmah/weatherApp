package com.bkcoding.weather.data

import android.content.Context
import com.bkcoding.core.network.weatherApi.WeatherApiImpl
import com.bkcoding.core.network.weatherApi.WeatherAppApi
import com.bkcoding.weather.WeatherApplication
import com.bkcoding.weather.data.repository.WeatherRepository
import com.bkcoding.weather.db.dao.CityDao
import com.bkcoding.weather.ui.cities.CityViewModel
import com.bkcoding.weather.ui.weather.WeatherViewModel


/**
 * Dependency Injection container at the application level.
 */
interface WeatherContainer {
    val cityDao: CityDao
    val weatherApi: WeatherAppApi
    val weatherRepository: WeatherRepository
    val cityViewModel: CityViewModel
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class WeatherContainerImpl(private val applicationContext: Context) : WeatherContainer {

    override val cityDao: CityDao by lazy {
        (applicationContext.applicationContext as WeatherApplication).database.cityDao()
    }

    override val weatherApi: WeatherAppApi by lazy { WeatherApiImpl() }

    override val weatherRepository: WeatherRepository by lazy {
        WeatherRepository(
            cityDao = cityDao,
            api = weatherApi
        )
    }

    override val cityViewModel: CityViewModel by lazy {
        CityViewModel(
            weatherRepository = weatherRepository
        )
    }
}