package com.bkcoding.weather.ui.cities

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkcoding.core.network.httpclient.NetworkResult
import com.bkcoding.core.network.model.NetworkCity
import com.bkcoding.core.network.weatherApi.WeatherApiImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CityViewModel : ViewModel() {

    private val api = WeatherApiImpl()

    var query by mutableStateOf("")

    var active by mutableStateOf(false)

    val savedCities = MutableStateFlow<MutableList<NetworkCity>>(mutableListOf())

    val suggestedCity = MutableStateFlow<SuggestedCity>(SuggestedCity.Empty)

    @OptIn(ExperimentalCoroutinesApi::class)
    val suggestions = snapshotFlow { query }
        .filter { query.length >= 3 }
        .mapLatest {
            viewModelScope.launch(Dispatchers.IO) {
                suggestedCity.value = SuggestedCity.Loading
                when (val response = api.searchCity(query = query)) {
                    is NetworkResult.Error -> suggestedCity.value = SuggestedCity.Empty
                    is NetworkResult.Exception -> suggestedCity.value = SuggestedCity.Error(error = response.e)
                    is NetworkResult.Success -> suggestedCity.value = SuggestedCity.Success(data = response.data)
                }
            }
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ""
        )

    fun addCity(city: NetworkCity) {
        savedCities.value = mutableListOf(city)
    }
}


sealed interface SuggestedCity {
    object Empty : SuggestedCity
    object Loading : SuggestedCity
    data class Error(val error: Throwable) : SuggestedCity
    data class Success(val data: List<NetworkCity>) : SuggestedCity
}