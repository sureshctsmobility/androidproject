package com.example.skycastproject.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Today", Icons.Default.Home)
    object Forecast : Screen("forecast/{cityName}", "Forecast", Icons.Default.CalendarMonth) {
        fun createRoute(cityName: String) = "forecast/$cityName"
    }
    object SavedCities : Screen("saved_cities", "Cities", Icons.Default.LocationCity)
    object Search : Screen("search", "Search", Icons.Default.Home)
}