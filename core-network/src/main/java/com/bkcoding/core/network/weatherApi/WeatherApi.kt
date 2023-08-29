package com.bkcoding.core.network.weatherApi

import com.bkcoding.core.network.BuildConfig
import com.bkcoding.core.network.httpclient.HttpClient
import com.bkcoding.core.network.httpclient.NetworkResult
import com.bkcoding.core.network.model.NetworkCity
import com.bkcoding.core.network.model.WeatherInfoNetwork

interface WeatherAppApi {
    suspend fun fetchWeather(
        query: String
    ): NetworkResult<WeatherInfoNetwork>

    suspend fun searchCity(
        query: String,
        limit: Int = 5,
    ): NetworkResult<List<NetworkCity>>
}

class WeatherApiImpl : WeatherAppApi {
    private val httpClient = HttpClient.shared

    override suspend fun fetchWeather(query: String): NetworkResult<WeatherInfoNetwork> {
        return httpClient.get<WeatherInfoNetwork>(
            url = ApiHelper.weatherInfoEndpoint(
                query = query
            )
        )
    }

    override suspend fun searchCity(
        query: String,
        limit: Int
    ): NetworkResult<List<NetworkCity>> {
        return httpClient.get<List<NetworkCity>>(
            url = ApiHelper.searchCityEndpoint(
                query = query,
                limit = limit
            )
        )
    }
}

object ApiHelper {
    private const val baseUrl = BuildConfig.BASE_URL
    fun searchCityEndpoint(
        query: String,
        limit: Int
    ) = "${baseUrl}geo/1.0/direct?q=$query&limit=$limit"

    fun weatherInfoEndpoint(
        query: String,
        units: String = "metric"
    ) = "${baseUrl}data/2.5/weather?q=$query&units=$units"
}