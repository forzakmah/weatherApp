package com.bkcoding.weather.ui.cities

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bkcoding.core.network.httpclient.NetworkResult
import com.bkcoding.core.network.weatherApi.WeatherApiImpl
import com.bkcoding.weather.data.model.City
import com.bkcoding.weather.data.model.asEntity
import com.bkcoding.weather.data.model.asExternalModel
import com.bkcoding.weather.data.repository.WeatherRepository
import com.bkcoding.weather.db.entity.CityEntity
import com.bkcoding.weather.ui.weather.WeatherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CityViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val api = WeatherApiImpl()

    var query by mutableStateOf("")

    var active by mutableStateOf(false)

    val savedCities = weatherRepository.fetchCities()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    val suggestedCity = MutableStateFlow<SuggestedCity>(SuggestedCity.Loading)

    @OptIn(ExperimentalCoroutinesApi::class)
    val suggestions = snapshotFlow { query }
        .filter { query.length >= 3 }
        .mapLatest {
            viewModelScope.launch(Dispatchers.IO) {
                when (val response = api.searchCity(query = query)) {
                    is NetworkResult.Error -> suggestedCity.value = SuggestedCity.Error(Exception())

                    is NetworkResult.Exception -> {
                        suggestedCity.value = SuggestedCity.Error(
                            error = response.e
                        )
                    }

                    is NetworkResult.Success -> {
                        suggestedCity.value = SuggestedCity.Success(
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
            initialValue = ""
        )

    /**
     * Save city
     * @param city [City] City that will be mapped to [CityEntity] an saved in local DB
     */
    fun addCity(city: City) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.addCity(
                entity = city.asEntity()
            )
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