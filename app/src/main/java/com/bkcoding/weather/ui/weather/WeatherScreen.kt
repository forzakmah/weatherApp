package com.bkcoding.weather.ui.weather

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeatherScreen() {
    val weatherMetrics = listOf(
        Pair("Feels like", "22"),
        Pair("Humidity", "50%"),
        Pair("Visibility", "25 km"),
        Pair("pressure", "1.012"),
        Pair("Wind", "15 km"),
        Pair("clouds", "All")
    )
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
        content = {
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sousse",
                        fontSize = 50.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "26°",
                        fontSize = 60.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Mostly clear",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "H:32° L:22°",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Image(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .size(150.dp),
                        painter = painterResource(id = com.bkcoding.weather.R.drawable.rain_img),
                        contentDescription = "test"
                    )
                }
            }

            weatherMetrics.forEach {
                item {
                    WeatherCardInfo(
                        icon = Icons.Default.CheckCircle,
                        key = it.first,
                        value = it.second
                    )
                }
            }
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Card(
                    modifier = Modifier
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
                            text = "☀\uFE0F sunrise",
                            fontWeight = FontWeight.W400,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "05:47",
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
                            text = "\uD83C\uDF05 sunset",
                            fontWeight = FontWeight.W400,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "18:48",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 25.sp
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun WeatherCardInfo(
    icon: ImageVector,
    key: String,
    value: String
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
                    modifier = Modifier.padding(end = 5.dp),
                    imageVector = icon,
                    contentDescription = key
                )
                Text(
                    text = key.uppercase()
                )
            }
            Text(
                text = "$value°",
                fontSize = 35.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
fun PreviewWeatherScreen() {
    WeatherScreen()
}