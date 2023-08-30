package com.bkcoding.weather.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkcoding.core.network.httpclient.NetworkResult
import com.bkcoding.core.network.model.WeatherInfoNetwork
import com.bkcoding.weather.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    private val cityName = "Sousse"

    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState = _weatherState.asStateFlow()

    init {
        fetchWeather()
    }

    fun fetchWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherState.value = WeatherState.Loading
            when (val response = weatherRepository.weatherByCity(query = cityName)) {
                is NetworkResult.Success -> _weatherState.value = WeatherState.Success(response.data)
                is NetworkResult.Error -> _weatherState.value = WeatherState.Error(response.message, response.code)
                is NetworkResult.Exception -> _weatherState.value = WeatherState.Error("message", 10)
            }
        }
    }
}

sealed interface WeatherState {
    object Loading : WeatherState
    data class Error(
        val message: String? = null,
        val code: Int
    ) : WeatherState

    data class Success(val weatherInfo: WeatherInfoNetwork) : WeatherState
}