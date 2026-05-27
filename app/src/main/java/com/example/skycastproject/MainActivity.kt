package com.example.skycastproject

// Explicitly import your architectural feature layers
import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.skycastproject.ui.features.forecast.ForecastScreen
import com.example.skycastproject.ui.features.forecast.ForecastViewModel
import com.example.skycastproject.ui.features.home.HomeScreen
import com.example.skycastproject.ui.features.home.HomeViewModel
import com.example.skycastproject.ui.features.saved_cities.SavedCitiesScreen
import com.example.skycastproject.ui.features.saved_cities.SavedCitiesViewModel
import com.example.skycastproject.ui.features.search.SearchScreen
import com.example.skycastproject.ui.features.search.SearchViewModel
import com.example.skycastproject.ui.models.WeatherPreview
import com.example.skycastproject.ui.navigation.Screen
import com.example.skycastproject.ui.theme.SkycastProjectTheme
import com.example.skycastproject.worker.NotificationScheduler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationScheduler.scheduleNextRun(applicationContext)
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
    val screens = listOf(Screen.Home, Screen.Forecast, Screen.SavedCities)
    // FIX: Removed the unused 'isNight' property definition to clear compiler warnings cleanly

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            NavigationBar(containerColor = MaterialTheme.colorScheme.background, tonalElevation = 0.dp) {
                screens.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any {
                        it.route?.startsWith(screen.route.split("/")[0]) == true
                    } == true
                    NavigationBarItem(
                        icon = { Icon(screen.icon, null) },
                        label = { Text(screen.title) },
                        selected = isSelected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            unselectedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            indicatorColor = Color.Transparent
                        ),
                        onClick = {
                            val target = if (screen is Screen.Forecast) screen.createRoute("Current") else screen.route
                            navController.navigate(target) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = Screen.Home.route, modifier = Modifier.padding(innerPadding)) {
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                val uiState by homeViewModel.state.collectAsState()
                val isRefreshing by homeViewModel.isRefreshing.collectAsState()

                val context = LocalContext.current

                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
                    if (perms[Manifest.permission.ACCESS_FINE_LOCATION] == true) homeViewModel.loadDeviceLocationWeather(context)
                }

                LaunchedEffect(Unit) {
                    launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                }

                HomeScreen(
                    uiState = uiState,
                    isRefreshing = isRefreshing,
                    onRequestPermission = { launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)) },
                    onRefresh = { homeViewModel.loadDeviceLocationWeather(context) }
                )
            }
            composable(Screen.Forecast.route) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("cityName") ?: "Current"
                val vm: ForecastViewModel = hiltViewModel()
                LaunchedEffect(name) { vm.loadForecastData(name) }
                val days by vm.forecastDays.collectAsState()
                val chart by vm.chartData.collectAsState()
                ForecastScreen(cityName = name, forecastDays = days, chartData = chart, onBack = { navController.popBackStack() })
            }
            composable(Screen.SavedCities.route) {
                val vm: SavedCitiesViewModel = hiltViewModel()
                val cities by vm.citiesState.collectAsState()
                SavedCitiesScreen(cities = cities, onCityClick = { n -> navController.navigate(Screen.Forecast.createRoute(n)) }, onAddCityClick = { navController.navigate(Screen.Search.route) })
            }
            composable(Screen.Search.route) {
                val vm: SearchViewModel = hiltViewModel()
                val query by vm.query.collectAsState()
                val loading by vm.isLoading.collectAsState()
                val suggestions by vm.suggestions.collectAsState()
                val top = suggestions.firstOrNull()

                SearchScreen(
                    query = query, onQueryChange = { vm.onQueryChange(it) }, onBack = { navController.popBackStack() }, isLoading = loading,
                    searchResult = top?.let { WeatherPreview(it.name, it.country, "${it.latitude}°N", "${it.longitude}°E", 0, 0, "Location Match Available") },
                    onAddCity = { top?.let { vm.saveLocation(it) }; navController.popBackStack() }
                )
            }
        }
    }
}