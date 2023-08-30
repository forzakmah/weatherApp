package com.bkcoding.weather

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bkcoding.weather.data.WeatherContainer
import com.bkcoding.weather.ui.cities.CityScreen
import com.bkcoding.weather.ui.weather.WeatherScreen
import com.bkcoding.weather.ui.weather.WeatherViewModel

object Routing {
    /**
     * route name for
     */
    const val cityScreen = "cityScreen"

    /**
     * list of arguments passed to weather screen
     */
    const val latitudeArg = "latitudeArg"
    const val longitudeArg = "longitudeArg"
    const val queryArg = "queryArg"
    const val weatherScreen = "weatherScreen/{$queryArg}/{$latitudeArg}/{$longitudeArg}"
}

/**
 * Basic graph navigation
 * @param modifier [Modifier]
 * @param navController [NavHostController]
 * @param startDestination [String]
 */
@Composable
fun WeatherNavigationApp(
    weatherContainer: WeatherContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routing.cityScreen
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {

        /**
         * List of the saved cities
         */
        composable(
            route = Routing.cityScreen
        ) {
            CityScreen(
                /**
                 * Inject viewModel from the [WeatherContainer] manuel dependency injection
                 */
                viewModel = weatherContainer.cityViewModel,
                didClick = { city ->
                    /**
                     * Navigate to weather details and pass the selected city
                     */
                    navController.navigate(
                        Routing.weatherScreen
                            .replace("{${Routing.queryArg}}", city.name)
                            .replace("{${Routing.latitudeArg}}", city.lat.toString())
                            .replace("{${Routing.longitudeArg}}", city.lon.toString())
                    )
                }
            )
        }

        /**
         * Weather info by city name
         */
        composable(
            route = Routing.weatherScreen,
            arguments = listOf(
                navArgument(
                    name = Routing.latitudeArg
                ) {
                    type = NavType.FloatType
                },
                navArgument(
                    name = Routing.longitudeArg
                ) {
                    type = NavType.FloatType
                }
            )
        ) { backStackEntry ->

            val query = backStackEntry.arguments?.getString(Routing.queryArg) ?: ""
            val latitude = backStackEntry.arguments?.getFloat(Routing.latitudeArg) ?: 0.0
            val longitude = backStackEntry.arguments?.getFloat(Routing.longitudeArg) ?: 0.0

            /**
             * Create viewModel using provideFactory static function
             */
            val weatherViewModel: WeatherViewModel = viewModel(
                factory = WeatherViewModel.provideFactory(
                    weatherRepository = weatherContainer.weatherRepository,
                    query = query,
                    lat = latitude.toDouble(),
                    lon = longitude.toDouble()
                )
            )

            WeatherScreen(
                weatherViewModel
            )
        }
    }
}
