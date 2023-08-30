package com.bkcoding.weather.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bkcoding.core.network.httpclient.NetworkResult
import com.bkcoding.core.network.model.WeatherInfoNetwork
import com.bkcoding.weather.data.model.WeatherInfoModel
import com.bkcoding.weather.data.model.asEntity
import com.bkcoding.weather.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val weatherId: Long,
    private val cityName: String,
    private val latitude: Double,
    private val longitude: Double
) : ViewModel() {

    /**
     * Flow that contains Weather info saved locally
     */
    val weatherInfo: StateFlow<WeatherInfoModelState> =
        weatherRepository.fetchWeatherInfo(id = weatherId)
            .map { WeatherInfoModelState.Success(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = WeatherInfoModelState.Loading
            )

    init {
        /**
         * Fetch the latest weather info and metrics for the network
         */
        fetchWeather()
    }

    private fun fetchWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            when (
                val response = weatherRepository.weatherByCity(
                    query = cityName,
                    lat = latitude,
                    lon = longitude
                )
            ) {
                is NetworkResult.Success -> saveWeatherInfo(weatherInfoNetwork = response.data)
                else -> Unit
            }
        }
    }

    private fun saveWeatherInfo(weatherInfoNetwork: WeatherInfoNetwork) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.saveWeatherInfo(
                weatherInfoEntity = weatherInfoNetwork.asEntity()
            )
        }
    }

    /**
     * Factory for [WeatherViewModel] that takes WeatherRepository as a dependency
     */
    companion object {
        fun provideFactory(
            weatherRepository: WeatherRepository,
            weatherId: Long,
            cityName: String,
            latitude: Double,
            longitude: Double,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WeatherViewModel(
                    weatherRepository = weatherRepository,
                    weatherId = weatherId,
                    cityName = cityName,
                    latitude = latitude,
                    longitude = longitude
                ) as T
            }
        }
    }
}

sealed interface WeatherInfoModelState {
    object Loading : WeatherInfoModelState

    data class Success(
        val weatherInfoModel: WeatherInfoModel
    ) : WeatherInfoModelState
}