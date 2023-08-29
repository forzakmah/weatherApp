package com.bkcoding.weather

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bkcoding.core.network.httpclient.NetworkResult
import com.bkcoding.core.network.model.NetworkCity
import com.bkcoding.core.network.weatherApi.WeatherApiImpl
import com.bkcoding.weather.ui.theme.WeatherAppTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cities = MutableStateFlow<List<NetworkCity>>(listOf())
        setContent {
            val data by cities.collectAsState()
            LaunchedEffect(key1 = null) {
                val weatherApi = WeatherApiImpl()
                delay(5000)
                GlobalScope.launch(Dispatchers.IO) {
                    when (val response = weatherApi.searchCity(query = "London")) {
                        is NetworkResult.Success -> cities.value = response.data
                        is NetworkResult.Error -> Log.e("error(${response.code}", response.message.toString())
                        is NetworkResult.Exception -> Log.e("exception", response.e.message.toString())
                    }

                    when (val response = weatherApi.fetchWeather(query = "London")) {
                        is NetworkResult.Success -> Log.e("success", response.data.toString())
                        is NetworkResult.Error -> Log.e("error(${response.code}", response.message.toString())
                        is NetworkResult.Exception -> Log.e("exception", response.e.message.toString())
                    }
                }
            }
            WeatherAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    /**
                     * test the call of search city
                     */
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        data.forEach {
                            item {
                                Text(
                                    text = "${it.name}, ${it.state}, ${it.country}",
                                    style = TextStyle(fontSize = 22.sp)
                                )
                            }
                            item {
                                Divider(
                                    color = Color.DarkGray,
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WeatherAppTheme {
        Greeting("Android")
    }
}