package com.bkcoding.weather.ui.cities

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bkcoding.core.network.httpclient.NetworkResult
import com.bkcoding.weather.data.model.City
import com.bkcoding.weather.data.model.asEntity
import com.bkcoding.weather.data.model.asExternalModel
import com.bkcoding.weather.data.repository.WeatherRepository
import com.bkcoding.weather.db.entity.WeatherInfoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CityViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    var query by mutableStateOf("")

    var active by mutableStateOf(false)

    val savedCities = weatherRepository.fetchWeathersInfo()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    private val _suggestedCity = MutableStateFlow<SuggestedCity>(SuggestedCity.Loading)
    val suggestedCity = _suggestedCity.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val suggestions = snapshotFlow { query }
        .filter { query.length >= 3 }
        .mapLatest {
            viewModelScope.launch(Dispatchers.IO) {
                when (val response = weatherRepository.searchCity(query = query)) {
                    is NetworkResult.Error -> _suggestedCity.value = SuggestedCity.Error(Exception())

                    is NetworkResult.Exception -> _suggestedCity.value = SuggestedCity.Error(error = response.e)

                    is NetworkResult.Success -> {
                        _suggestedCity.value = SuggestedCity.Success(
                            data = response.data.map {
                                it.asExternalModel()
                            }
                        )
                    }
                }
            }
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun confirmSavingWeatherInfo(city: City) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = weatherRepository.weatherByCity(
                query = city.name,
                lat = city.lat,
                lon = city.lon
            )
            when (response) {
                is NetworkResult.Success -> {
                    /**
                     * clear state
                     */
                    resetSuggestedCities()
                    /**
                     * Save weather info in the DB
                     */
                    saveWeatherInfo(
                        weatherInfoEntity = response.data.asEntity()
                    )
                }

                else -> Unit
            }
        }
    }

    fun resetSuggestedCities() {
        _suggestedCity.value = SuggestedCity.Loading
    }

    private fun saveWeatherInfo(
        weatherInfoEntity: WeatherInfoEntity
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.saveWeatherInfo(weatherInfoEntity)
        }
    }

    /**
     * Factory for [CityViewModel] that takes WeatherRepository as a dependency
     */
    companion object {
        fun provideFactory(
            weatherRepository: WeatherRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CityViewModel(weatherRepository = weatherRepository) as T
            }
        }
    }
}


sealed interface SuggestedCity {
    object Loading : SuggestedCity
    data class Error(val error: Throwable) : SuggestedCity
    data class Success(val data: List<City>) : SuggestedCity
}