package com.bkcoding.weather.ui.cities

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bkcoding.weather.R
import com.bkcoding.weather.data.model.City

@Composable
fun CityScreen(
    viewModel: CityViewModel,
    didClick: (City) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        viewModel.suggestions.collectAsState()

        val suggestedCityState by viewModel.suggestedCity.collectAsState()

        val cities by viewModel.savedCities.collectAsState()

        /**
         * Header
         */
        AnimatedVisibility(visible = !viewModel.active) {
            HeaderCityScreen()
        }

        /**
         * Search view
         */
        CitySearchBar(
            suggestedCityState = suggestedCityState,
            active = viewModel.active,
            text = viewModel.query,
            onActiveChange = {
                viewModel.active = it
            },
            onSearch = {
                viewModel.active = false
            },
            onQueryChange = {
                viewModel.query = it
            },
            didConfirm = {
                viewModel.query = ""
                /**
                 * save city inside the mutable list
                 */
                viewModel.addCity(it)
                viewModel.active = false
            },
            didCancel = {
                viewModel.active = false
                viewModel.query = ""
            }
        )

        /**
         * List of cities
         */
        LazyColumn(
            modifier = Modifier.fillMaxSize(1f)
        ) {
            if (cities.isEmpty()) {
                item {
                    EmptyCityScreen(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .padding(16.dp)
                    )
                }
            } else {
                cities.forEach { city ->
                    item(key = city.name) {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable {
                                    didClick.invoke(city)
                                },
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 20.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = "${city.name}, ${city.state}, ${city.country}",
                                    fontWeight = FontWeight.W700,
                                    fontSize = 16.sp
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
fun HeaderCityScreen(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.title_city_screen),
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.2.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySearchBar(
    suggestedCityState: SuggestedCity,
    modifier: Modifier = Modifier,
    active: Boolean,
    text: String,
    onActiveChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    didCancel: () -> Unit,
    didConfirm: (City) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(if (active) 0.dp else 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            query = text,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            active = active,
            onActiveChange = onActiveChange,
            placeholder = { Text(stringResource(id = R.string.placeholder_search_field)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (active) {
                    Text(
                        modifier = Modifier.clickable {
                            didCancel.invoke()
                        },
                        text = stringResource(id = R.string.cancel_text)
                    )
                }
            }
        ) {
            if (suggestedCityState is SuggestedCity.Success) {
                if (suggestedCityState.data.isEmpty()) {
                    EmptySuggestedCities(
                        modifier = Modifier.fillMaxSize(),
                        query = text
                    )
                } else
                    suggestedCityState.data.forEachIndexed { _, networkCity ->
                        ListItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 4.dp
                                )
                                .clickable {
                                    didConfirm.invoke(networkCity)
                                },
                            headlineContent = {
                                Text(text = networkCity.name)
                            },
                            supportingContent = {
                                Text(text = networkCity.state)
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null
                                )
                            },
                        )
                    }
            }
        }
    }
}

@Composable
fun EmptyCityScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(200.dp),
            painter = painterResource(id = R.drawable.no_cities),
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = stringResource(id = R.string.text_indicator_when_no_cities),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.W500
        )
    }
}

@Composable
fun EmptySuggestedCities(
    modifier: Modifier = Modifier,
    query: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(48.dp),
            imageVector = Icons.Default.Search,
            contentDescription = "Search"
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = stringResource(id = R.string.no_results),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.W700,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = String.format(
                stringResource(id = R.string.no_results_found),
                query
            ),
            fontSize = 15.sp
        )
    }
}

@Preview(device = Devices.PIXEL_4_XL, uiMode = UI_MODE_NIGHT_NO)
@Preview(device = Devices.PIXEL_4_XL, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewCityScreen() {
    //CityScreen(viewModel = CityViewModel())
}