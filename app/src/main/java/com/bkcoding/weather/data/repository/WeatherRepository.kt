package com.bkcoding.weather.data.repository

import com.bkcoding.core.network.httpclient.NetworkResult
import com.bkcoding.core.network.weatherApi.WeatherAppApi
import com.bkcoding.weather.data.model.City
import com.bkcoding.weather.data.model.asExternalModel
import com.bkcoding.weather.db.dao.CityDao
import com.bkcoding.weather.db.entity.CityEntity
import com.bkcoding.weather.utils.suspendRunCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface IWeatherRepository {
    suspend fun searchCity(
        query: String,
        limit: Int = 5,
    ): Result<List<City>>

    suspend fun addCity(entity: CityEntity)
    fun fetchCities(): Flow<List<City>>
}

class WeatherRepository(
    private val cityDao: CityDao,
    private val api: WeatherAppApi
) : IWeatherRepository {
    override suspend fun searchCity(
        query: String,
        limit: Int
    ): Result<List<City>> {
        return suspendRunCatching {
            when (
                val response = api.searchCity(query = query, limit = limit)
            ) {
                is NetworkResult.Success -> response.data.map { it.asExternalModel() }
                else -> emptyList()
            }
        }
    }

    override suspend fun addCity(entity: CityEntity) {
        cityDao.insertAll(entity)
    }

    override fun fetchCities(): Flow<List<City>> {
        return cityDao.getAllCities().map { flow ->
            flow.map { city ->
                city.asExternalModel()
            }
        }
    }
}