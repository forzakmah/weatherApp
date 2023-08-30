package com.bkcoding.weather

import android.content.Context
import com.bkcoding.core.network.weatherApi.WeatherApiImpl
import com.bkcoding.core.network.weatherApi.WeatherAppApi
import com.bkcoding.weather.data.repository.WeatherRepository
import com.bkcoding.weather.db.dao.WeatherInfoDao
import com.bkcoding.weather.ui.cities.CityViewModel


/**
 * Dependency Injection container at the application level.
 */
interface WeatherContainer {
    val weatherInfoDao: WeatherInfoDao
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

    override val weatherInfoDao: WeatherInfoDao by lazy {
        (applicationContext.applicationContext as WeatherApplication).database.weatherDao()
    }

    override val weatherApi: WeatherAppApi by lazy { WeatherApiImpl() }

    override val weatherRepository: WeatherRepository by lazy {
        WeatherRepository(
            weatherInfoDao = weatherInfoDao,
            api = weatherApi
        )
    }

    override val cityViewModel: CityViewModel by lazy {
        CityViewModel(
            weatherRepository = weatherRepository
        )
    }
}