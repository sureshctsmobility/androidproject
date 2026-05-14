package com.example.skycastproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.skycastproject.ui.features.forecast.ForecastScreen
import com.example.skycastproject.ui.features.home.HomeScreen
import com.example.skycastproject.ui.features.saved_cities.SavedCitiesScreen
import com.example.skycastproject.ui.features.search.SearchScreen
import com.example.skycastproject.ui.models.CityUiModel
import com.example.skycastproject.ui.models.ForecastDay
import com.example.skycastproject.ui.models.HomeUiState
import com.example.skycastproject.ui.models.WeatherPreview
import com.example.skycastproject.ui.navigation.Screen
import com.example.skycastproject.ui.theme.SkycastProjectTheme

class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkycastProjectTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val screens = listOf(Screen.Home, Screen.Forecast, Screen.SavedCities, Screen.Settings)
    
    // Theme state
    val isNight = isSystemInDarkTheme()
    val backgroundColor = if (isNight) Color.Black else Color.White
    val contentColor = if (isNight) Color.White else Color.Black

    





    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            NavigationBar(
                containerColor = backgroundColor,
                tonalElevation = 0.dp
            ) {
                screens.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { 
                        it.route?.startsWith(screen.route.split("/")[0]) == true 
                    } == true
                    
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = isSelected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = contentColor,
                            selectedTextColor = contentColor,
                            unselectedIconColor = contentColor.copy(alpha = 0.4f),
                            unselectedTextColor = contentColor.copy(alpha = 0.4f),
                            indicatorColor = Color.Transparent
                        ),
                        onClick = {
                            val route = if (screen is Screen.Forecast) screen.createRoute("Chennai") else screen.route
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    uiState = HomeUiState(),
                    isNight = isNight
                )
            }
            composable(Screen.Forecast.route) { backStackEntry ->
                val cityName = backStackEntry.arguments?.getString("cityName") ?: "Chennai"
                ForecastScreen(
                    cityName = cityName,
                    forecastDays = listOf(
                        ForecastDay("Tue", 34, 27, "Sunny", "2%"),
                        ForecastDay("Wed", 33, 26, "Sunny", "10%"),
                        ForecastDay("Thu", 34, 27, "Cloudy", "5%"),
                        ForecastDay("Fri", 32, 25, "Rainy", "35%"),
                        ForecastDay("Sat", 30, 24, "Rainy", "70%")
                    ),
                    chartData = listOf(31, 33, 34, 32, 30),
                    onBack = { navController.popBackStack() },
                    isNight = isNight
                )
            }


            composable(Screen.SavedCities.route) {
                SavedCitiesScreen(
                    cities = listOf(
                        CityUiModel("1", "Chennai", "9:41 AM", "India", 32, "Sunny", 34, 27, listOf()),
                        CityUiModel("2", "Mumbai", "9:41 AM", "India", 28, "Light Rain", 30, 25, listOf()),
                        CityUiModel("3", "London", "5:11 AM", "UK", 14, "Cloudy", 17, 11, listOf()),
                        CityUiModel("4", "Reykjavik", "4:41 AM", "Iceland", -2, "Snow", 1, -5, listOf())
                    ),
                    onCityClick = { cityName -> navController.navigate(Screen.Forecast.createRoute(cityName)) },
                    onAddCityClick = { navController.navigate(Screen.Search.route) },
                    isNight = isNight
                )
            }
            composable(Screen.Settings.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Settings Screen", color = contentColor)
                }
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    query = "tokyo",
                    onQueryChange = {},
                    onBack = { navController.popBackStack() },
                    isLoading = true,
                    searchResult = WeatherPreview(
                        name = "Tokyo",
                        country = "Japan",
                        lat = "35.68°N",
                        lon = "139.65°E",
                        temp = 19,
                        feelsLike = 18,
                        condition = "Partly Cloudy"
                    ),
                    onAddCity = {},
                    isNight = isNight
                )
            }
        }
    }
}
