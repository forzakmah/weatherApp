package com.bkcoding.weather.data.repository

import com.bkcoding.core.network.httpclient.NetworkResult
import com.bkcoding.core.network.model.WeatherInfoNetwork
import com.bkcoding.core.network.weatherApi.WeatherAppApi
import com.bkcoding.weather.data.model.City
import com.bkcoding.weather.data.model.WeatherInfoModel
import com.bkcoding.weather.data.model.asExternalModel
import com.bkcoding.weather.db.dao.WeatherInfoDao
import com.bkcoding.weather.db.entity.WeatherInfoEntity
import com.bkcoding.weather.utils.suspendRunCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface IWeatherRepository {
    suspend fun searchCity(
        query: String,
        limit: Int = 5,
    ): Result<List<City>>

    suspend fun weatherByCity(
        query: String,
        lat: Double,
        lon: Double
    ): NetworkResult<WeatherInfoNetwork>

    suspend fun saveWeatherInfo(
        weatherInfoEntity: WeatherInfoEntity
    )

    fun fetchWeathersInfo(): Flow<List<WeatherInfoModel>>

    fun fetchWeatherInfo(id: Long): Flow<WeatherInfoModel>
}

class WeatherRepository(
    private val weatherInfoDao: WeatherInfoDao,
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

    override suspend fun weatherByCity(
        query: String,
        lat: Double,
        lon: Double
    ): NetworkResult<WeatherInfoNetwork> {
        return api.fetchWeather(
            query = query,
            lat = lat,
            lon = lon
        )
    }

    override suspend fun saveWeatherInfo(
        weatherInfoEntity: WeatherInfoEntity
    ) {
        weatherInfoDao.insert(weatherInfoEntity)
    }

    override fun fetchWeathersInfo(): Flow<List<WeatherInfoModel>> {
        return weatherInfoDao.getAllWeathers().map { flow ->
            flow.map { weather ->
                weather.asExternalModel()
            }
        }
    }

    override fun fetchWeatherInfo(id: Long): Flow<WeatherInfoModel> {
        return weatherInfoDao.weatherById(id = id).map { it.asExternalModel() }
    }
}