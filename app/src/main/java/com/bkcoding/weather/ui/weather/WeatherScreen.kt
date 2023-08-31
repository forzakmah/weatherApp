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
import com.bkcoding.weather.data.model.WeatherInfoModel
import com.bkcoding.weather.utils.WeatherCircularProgressBar
import com.bkcoding.weather.utils.formatTo
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


private fun extractWeatherMetrics(weatherInfo: WeatherInfoModel): List<Triple<Int, String, Metric>> {
    return listOf(
        Triple(
            first = R.string.label_feels_like,
            second = "${weatherInfo.feelsLike.roundToInt()}",
            third = Metric(measure = Measure.CELSIUS, icon = MetricIcons.temperature)
        ),
        Triple(
            first = R.string.label_humidity,
            second = "${weatherInfo.humidity}",
            third = Metric(measure = Measure.PERCENT, icon = MetricIcons.humidity)
        ),
        Triple(
            first = R.string.label_visibility,
            second = "${(weatherInfo.visibility / 1000)}",
            third = Metric(measure = Measure.KILOMETER, icon = MetricIcons.visibility)
        ),
        Triple(
            first = R.string.label_pressure,
            second = "${weatherInfo.pressure / 1000}",
            third = Metric(measure = Measure.HECTOPASCAL, icon = MetricIcons.pressure)
        ),
        Triple(
            first = R.string.label_wind,
            second = "${weatherInfo.wind.roundToInt()}",
            third = Metric(measure = Measure.KILOMETER, icon = MetricIcons.winds)
        ),
        Triple(
            first = R.string.label_clouds,
            second = "${weatherInfo.clouds}",
            third = Metric(measure = Measure.PERCENT, icon = MetricIcons.clouds)
        )
    )
}

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel
) {
    val weatherInfo by viewModel.weatherInfo.collectAsState()


    when (val state = weatherInfo) {
        is WeatherInfoModelState.Loading -> WeatherCircularProgressBar(visible = true)

        is WeatherInfoModelState.Success -> WeatherScreenBody(weatherInfo = state.weatherInfoModel)
    }
}

@Composable
fun WeatherScreenBody(weatherInfo: WeatherInfoModel) {
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

            /**
             * Latest update indicator
             */
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = String.format(
                            stringResource(id = R.string.label_latest_update),
                            weatherInfo.createdAt.time.formatTo()
                        ),
                        fontSize = 12.sp
                    )
                }
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
    weatherInfo: WeatherInfoModel,
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
            text = weatherInfo.cityName,
            fontSize = 50.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "${weatherInfo.temp.roundToInt()}${Measure.CELSIUS.symbol}",
            fontSize = 60.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = weatherInfo.description,
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = String.format(
                stringResource(id = R.string.label_highest_lowest_temp),
                weatherInfo.tempMax.roundToInt(),
                Measure.CELSIUS.symbol,
                weatherInfo.tempMin.roundToInt(),
                Measure.CELSIUS.symbol,
            ),
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold
        )

        val weatherIcon = WeatherIcon.find(
            key = weatherInfo.icon
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
    weatherInfo: WeatherInfoModel,
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
                text = weatherInfo.sunrise.toTime(),
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
                text = weatherInfo.sunset.toTime(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 25.sp
            )
        }
    }
}

/**
 * Can be used to display error when something
 * wrong happen and give the user the possibility to retry the request
 */
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