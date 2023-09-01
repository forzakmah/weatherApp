package com.bkcoding.weather.data.repository

import com.bkcoding.core.network.httpclient.NetworkResult
import com.bkcoding.core.network.model.CityNetwork
import com.bkcoding.core.network.model.WeatherInfoNetwork
import com.bkcoding.core.network.weatherApi.WeatherAppApi
import com.bkcoding.weather.data.model.WeatherInfoModel
import com.bkcoding.weather.data.model.asExternalModel
import com.bkcoding.weather.db.dao.WeatherInfoDao
import com.bkcoding.weather.db.entity.WeatherInfoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface IWeatherRepository {
    suspend fun searchCity(
        query: String,
        limit: Int = 5,
    ): NetworkResult<List<CityNetwork>>

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

    /**
     * Search cities from the network using the passed query
     * @param query [String] city name to search
     * @param limit [Int] number of items to return in the response
     * @return [NetworkResult]<[List]<[CityNetwork]>>
     */
    override suspend fun searchCity(
        query: String,
        limit: Int
    ): NetworkResult<List<CityNetwork>> {
        return api.searchCity(query = query, limit = limit)
    }

    /**
     * Return the network result of weather information of the searched city
     * @param query [String] city name
     * @param lat [Double] latitude of the city
     * @param lon [Double] longitude of the city
     * @return [NetworkResult]<[WeatherInfoNetwork]>
     */
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

    /**
     * Save city weather information in the local database
     * @param weatherInfoEntity [WeatherInfoModel] weather entity to save
     */
    override suspend fun saveWeatherInfo(
        weatherInfoEntity: WeatherInfoEntity
    ) {
        weatherInfoDao.insert(weatherInfoEntity)
    }

    /**
     * Return the list of the cities saved inside the database as flow
     * @return [Flow]<[List]<[WeatherInfoModel]>>
     */
    override fun fetchWeathersInfo(): Flow<List<WeatherInfoModel>> {
        return weatherInfoDao.getAllWeathers().map { flow ->
            flow.map { weather ->
                weather.asExternalModel()
            }
        }
    }

    /**
     * Return the city by id
     * @param id [Long]
     * @return [Flow]<[WeatherInfoModel]>
     */
    override fun fetchWeatherInfo(
        id: Long
    ): Flow<WeatherInfoModel> {
        return weatherInfoDao.weatherById(id = id).map { it.asExternalModel() }
    }
}