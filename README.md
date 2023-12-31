# <b>WeatherApp</b>

A simple android weather app demonstrating Jetpack Compose, Coroutines and Kotlin flow.
The simple Offers offline mode user experience using Room api for managing local persistence
and online mode data is provided by [OpenWeatherMap API](https://openweathermap.org/api) public API.

![alt text](https://github.com/forzakmah/weatherApp/blob/main/previews/weather_app.png "Preview")

## Modules:

<b>app</b>: the main entry point of the application.
<b>core-network</b>: A basic http client module implemented with [OkHttp3](https://square.github.io/okhttp/).

The core-network module provides 2 main Endpoints.

* Search city by name
* Fetch weather metrics.

Below an example of how to use the core-network module.

```kotlin
//Create a new instance of WeatherAppApi
val weatherApi = WeatherAppApi()

//The response of the endpoints is a NetworkResult of generic type T
sealed class NetworkResult<T : Any> {
    class Success<T : Any>(
        val data: T,
        val code: Int = HttpURLConnection.HTTP_OK
    ) : NetworkResult<T>()

    class Error<T : Any>(
        val code: Int,
        val message: String?
    ) : NetworkResult<T>()

    class Exception<T : Any>(
        val e: Throwable
    ) : NetworkResult<T>()
}

// Example of fetching cities
val citiesResponse: NetworkResult<List<CityNetwork>> = api.searchCity(query = query, limit = limit)

// Example get weather info using city name, latitude, and longitude
val weatherResponse: NetworkResult<WeatherInfoNetwork> = api.fetchWeather(query = query, lat = lat, lon = lon)
```

Dependency injection is handled manually with this
class [WeatherContainer.kt](https://github.com/forzakmah/weatherApp/blob/main/app/src/main/java/com/bkcoding/weather/WeatherContainer.kt).

### Libraries

* Android Architecture Components (ViewModel, Compose navigation)
* [Room](https://developer.android.com/training/data-storage/room) is a persistence library provides an abstraction
  layer over SQLite.
* [coroutines](https://developer.android.com/kotlin/coroutines?hl=fr) is a concurrency design pattern for asynchronously
  tasks.
* [Jetpack Compose](https://developer.android.com/jetpack/compose) is a modern toolkit for building native UI.
* [OkHttp3](https://square.github.io/okhttp/) is a third-party library developed by Square for sending and receive
  HTTP-based network requests.
* [material3](https://developer.android.com/jetpack/androidx/releases/compose-material3) Material Design 3 Components.
* [kotlinx-serialization](https://github.com/Kotlin/kotlinx.serialization) Kotlin serialization consists of a compiler
  plugin, that generates visitor code for serializable classes.

#### Sonar analyse result

![alt text](https://github.com/forzakmah/weatherApp/blob/main/previews/sonar_weather_app.png "Preview")