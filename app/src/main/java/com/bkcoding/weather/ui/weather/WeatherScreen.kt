package com.bkcoding.weather.ui.weather

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WindPower
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bkcoding.core.network.model.WeatherInfoNetwork
import com.bkcoding.weather.R
import com.bkcoding.weather.utils.WeatherCircularProgressBar
import com.bkcoding.weather.utils.toTime
import kotlin.math.roundToInt

data class Metric(
    val measure: Measure,
    val icon: ImageVector
)

object MetricIcons {
    val temperature = Icons.Filled.WbSunny
    val humidity = Icons.Filled.CloudQueue
    val visibility = Icons.Filled.Visibility
    val pressure = Icons.Default.Compress
    val winds = Icons.Default.WindPower
    val clouds = Icons.Default.Cloud
}


enum class Measure(
    val symbol: String
) {
    PERCENT(symbol = "%"),
    CELSIUS(symbol = "°"),
    HECTOPASCAL(symbol = " hPa"),
    KILOMETER(symbol = " km"),

    /**
     * if some cases we can found some fields without unit
     * like the UV index
     */
    NONE(symbol = "")
}


enum class WeatherIcon(
    @DrawableRes val drawable: Int,
    val value: String,
) {
    CLEAR_SKY_DAY(
        drawable = R.drawable.clear_day,
        value = "01d"
    ),
    CLEAR_SKY_NIGHT(
        drawable = R.drawable.clear_night,
        value = "01n"
    ),
    FEW_CLOUDS_DAY(
        drawable = R.drawable.few_clouds_day,
        value = "02d"
    ),
    FEW_CLOUDS_NIGHT(
        drawable = R.drawable.few_clouds_night,
        value = "02n"
    ),
    SCATTERED_CLOUDS_DAY(
        drawable = R.drawable.scattered_clouds,
        value = "03d"
    ),
    SCATTERED_CLOUDS_NIGHT(
        drawable = R.drawable.scattered_clouds,
        value = "03n"
    ),
    BROKEN_CLOUDS_DAY(
        drawable = R.drawable.broken_clouds,
        value = "04d"
    ),
    BROKEN_CLOUDS_NIGHT(
        drawable = R.drawable.broken_clouds,
        value = "04n"
    ),
    SHOWER_RAIN_DAY(
        drawable = R.drawable.shower_rain,
        value = "09d"
    ),
    SHOWER_RAIN_NIGHT(
        drawable = R.drawable.shower_rain,
        value = "09n"
    ),
    RAIN_DAY(
        drawable = R.drawable.rain_day,
        value = "10d"
    ),
    RAIN_NIGHT(
        drawable = R.drawable.rain_night,
        value = "10n"
    ),
    THUNDERSTORM_DAY(
        drawable = R.drawable.thunderstorm,
        value = "11d"
    ),
    THUNDERSTORM_NIGHT(
        drawable = R.drawable.thunderstorm,
        value = "11n"
    ),
    SNOW_DAY(
        drawable = R.drawable.snow,
        value = "13d"
    ),
    SNOW_NIGHT(
        drawable = R.drawable.snow,
        value = "13n"
    ),
    MIST_DAY(
        drawable = R.drawable.mist,
        value = "50d"
    ),
    MIST_NIGHT(
        drawable = R.drawable.mist,
        value = "50n"
    ),
    UNKNOWN(
        drawable = R.drawable.scattered_clouds,
        value = "00"
    );

    companion object {
        fun find(key: String): WeatherIcon {
            return WeatherIcon.values().find { it.value == key } ?: UNKNOWN
        }
    }
}

private fun extractWeatherMetrics(weatherInfo: WeatherInfoNetwork): List<Triple<Int, String, Metric>> {
    return listOf(
        Triple(
            first = R.string.label_feels_like,
            second = "${weatherInfo.main.feelsLike.roundToInt()}",
            third = Metric(measure = Measure.CELSIUS, icon = MetricIcons.temperature)
        ),
        Triple(
            first = R.string.label_humidity,
            second = "${weatherInfo.main.humidity}",
            third = Metric(measure = Measure.PERCENT, icon = MetricIcons.humidity)
        ),
        Triple(
            first = R.string.label_visibility,
            second = "${(weatherInfo.visibility / 1000)}",
            third = Metric(measure = Measure.KILOMETER, icon = MetricIcons.visibility)
        ),
        Triple(
            first = R.string.label_pressure,
            second = "${weatherInfo.main.pressure / 1000}",
            third = Metric(measure = Measure.HECTOPASCAL, icon = MetricIcons.pressure)
        ),
        Triple(
            first = R.string.label_wind,
            second = "${weatherInfo.wind.speed.roundToInt()}",
            third = Metric(measure = Measure.KILOMETER, icon = MetricIcons.winds)
        ),
        Triple(
            first = R.string.label_clouds,
            second = "${weatherInfo.clouds.all}",
            third = Metric(measure = Measure.PERCENT, icon = MetricIcons.clouds)
        )
    )
}

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel
) {
    val weatherState by viewModel.weatherState.collectAsState()

    when (val state = weatherState) {
        is WeatherState.Loading -> WeatherCircularProgressBar(visible = true)

        is WeatherState.Success -> WeatherScreenBody(weatherInfo = state.weatherInfo)

        is WeatherState.Error -> ErrorWeatherScreen(retry = viewModel::fetchWeather)
    }
}

@Composable
fun WeatherScreenBody(weatherInfo: WeatherInfoNetwork) {
    /**
     * Extract all data that will be displayed
     */
    val weatherMetrics = extractWeatherMetrics(weatherInfo = weatherInfo)

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
        content = {

            /**
             * Header with basic information
             */
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                HeaderWeatherScreen(
                    weatherInfo = weatherInfo
                )
            }


            /**
             * Cards with metrics [Pressure, Visibility and others]
             */
            weatherMetrics.forEach {
                item {
                    WeatherCardInfo(
                        key = it.first,
                        value = it.second,
                        metric = it.third
                    )
                }
            }

            /**
             * Footer to display sunrise and sunset
             */
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                FooterWeatherScreen(
                    weatherInfo = weatherInfo
                )
            }
        }
    )
}

@Composable
fun WeatherCardInfo(
    @StringRes key: Int,
    value: String,
    metric: Metric
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .size(28.dp)
                        .padding(end = 5.dp),
                    imageVector = metric.icon,
                    contentDescription = stringResource(id = key)
                )
                Text(
                    text = stringResource(id = key).uppercase(),
                    fontSize = 14.sp,
                )
            }
            Text(
                text = "$value${metric.measure.symbol}",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun HeaderWeatherScreen(
    weatherInfo: WeatherInfoNetwork,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = weatherInfo.name,
            fontSize = 50.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "${weatherInfo.main.temp.roundToInt()}${Measure.CELSIUS.symbol}",
            fontSize = 60.sp,
            fontWeight = FontWeight.ExtraBold
        )
        if (weatherInfo.weather.isNotEmpty()) {
            Text(
                text = weatherInfo.weather.first().description,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Text(
            text = String.format(
                stringResource(id = R.string.label_highest_lowest_temp),
                weatherInfo.main.tempMax.roundToInt(),
                Measure.CELSIUS.symbol,
                weatherInfo.main.tempMin.roundToInt(),
                Measure.CELSIUS.symbol,
            ),
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold
        )

        val weatherIcon = WeatherIcon.find(
            key = weatherInfo.weather.first().icon
        )
        Image(
            modifier = Modifier
                .padding(vertical = 0.dp)
                .size(150.dp),
            painter = painterResource(
                id = weatherIcon.drawable
            ),
            contentDescription = null
        )
    }
}

@Composable
fun FooterWeatherScreen(
    weatherInfo: WeatherInfoNetwork,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.label_sunrise),
                fontWeight = FontWeight.W400,
                fontSize = 20.sp
            )
            Text(
                text = weatherInfo.sys.sunrise.toTime(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 25.sp
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.label_sunset),
                fontWeight = FontWeight.W400,
                fontSize = 20.sp
            )
            Text(
                text = weatherInfo.sys.sunset.toTime(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 25.sp
            )
        }
    }
}

@Composable
fun ErrorWeatherScreen(
    retry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(180.dp),
            painter = painterResource(id = R.drawable.no_cities),
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            fontSize = 14.sp,
            fontWeight = FontWeight.W400,
            text = stringResource(id = R.string.something_goes_wrong),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(15.dp))
        TextButton(
            onClick = retry,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff9FA8B9))
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                text = stringResource(id = R.string.retry_label_btn),
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
            )
        }
    }
}