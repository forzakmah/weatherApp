package com.bkcoding.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bkcoding.core.network.weatherApi.WeatherApiImpl
import com.bkcoding.weather.data.repository.WeatherRepository
import com.bkcoding.weather.ui.cities.CityScreen
import com.bkcoding.weather.ui.cities.CityViewModel
import com.bkcoding.weather.ui.theme.WeatherAppTheme
import com.bkcoding.weather.ui.weather.WeatherScreen
import com.bkcoding.weather.ui.weather.WeatherViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cityDao = (applicationContext as WeatherApplication).database.cityDao()

        val api = WeatherApiImpl()

        val repository = WeatherRepository(
            cityDao = cityDao,
            api = api
        )

        val viewModel by viewModels<CityViewModel>(
            factoryProducer = {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return CityViewModel(weatherRepository = repository) as T
                    }
                }
            }
        )

        val anotherOne by viewModels<WeatherViewModel>(
            factoryProducer = {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return WeatherViewModel(weatherRepository = repository) as T
                    }
                }
            }
        )

        setContent {
            WeatherAppTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val state by anotherOne.weatherState.collectAsState()
                    //CityScreen(
                    //    viewModel = viewModel
                    //)
                    WeatherScreen(
                        weatherState = state,
                        retry = anotherOne::fetchWeather
                    )
                }
            }
        }
    }
}