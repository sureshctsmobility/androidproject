package com.example.skycastproject.ui.widget

import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.skycastproject.MainActivity
import com.example.skycastproject.R
import com.example.skycastproject.core.location.LocationTracker
import com.example.skycastproject.data.local.SkyCastDatabase
import com.example.skycastproject.data.local.entity.WeatherCacheEntity
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SkyCastWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetEntryPoint {
        fun databaseInstance(): SkyCastDatabase
        fun locationTracker(): LocationTracker
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val weatherDao = entryPoint.databaseInstance().weatherDao()
        val locationTracker = entryPoint.locationTracker()

        // 1. Resolve current geocoded name for fallback
        val geocodedCityName = withContext(Dispatchers.IO) {
            val location = locationTracker.getCurrentLocationCoordinates()
            if (location != null) {
                try {
                    val geocoder = Geocoder(context.applicationContext, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val address = addresses?.firstOrNull()
                    address?.subLocality ?: address?.locality ?: address?.subAdminArea ?: "My Location"
                } catch (e: Exception) {
                    "My Location"
                }
            } else {
                "My Location"
            }
        }

        // 2. Fetch data: Prefer named city cache over generic "My Location" to ensure sync with app's last selected city
        val currentCache = weatherDao.getLatestNamedCityCache() 
            ?: weatherDao.getAbsoluteLatestCache()

        provideContent {
            WidgetLayout(currentCache, geocodedCityName)
        }
    }

    @Composable
    private fun WidgetLayout(cache: WeatherCacheEntity?, fallbackName: String) {
        val baseBlue = ColorProvider(androidx.compose.ui.graphics.Color(0xFF4F86F7))
        val textColor = ColorProvider(androidx.compose.ui.graphics.Color(0xFFFFFFFF))
        val subTextColor = ColorProvider(androidx.compose.ui.graphics.Color(0xCCFFFFFF))

        val calendar = Calendar.getInstance()
        val timeString = SimpleDateFormat("h a EEEE", Locale.getDefault()).format(calendar.time).uppercase()

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(baseBlue)
                .padding(vertical = 2.dp)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                // LEFT COLUMN
                Column(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .padding(start = 20.dp),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Text(
                        text = timeString,
                        style = TextStyle(color = subTextColor, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    )

                    Row(verticalAlignment = Alignment.Vertical.Top) {
                        Text(
                            text = if (cache != null) "${cache.temperature.toInt()}" else "--",
                            style = TextStyle(color = textColor, fontSize = 38.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "°C",
                            style = TextStyle(color = textColor, fontSize = 14.sp),
                            modifier = GlanceModifier.padding(top = 4.dp)
                        )
                    }

                    Text(
                        text = if (cache != null) mapWmoCodeToCondition(cache.weatherCode) else "Updating...",
                        style = TextStyle(color = subTextColor, fontSize = 12.sp)
                    )
                }

                // RIGHT COLUMN
                Box(
                    modifier = GlanceModifier
                        .fillMaxHeight()
                        .width(150.dp)
                        .background(ImageProvider(R.drawable.widget_layer_bottom)),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Box(
                        modifier = GlanceModifier
                            .fillMaxHeight()
                            .width(135.dp)
                            .background(ImageProvider(R.drawable.widget_layer_top)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                            verticalAlignment = Alignment.Vertical.CenterVertically,
                            modifier = GlanceModifier.padding(end = 8.dp)
                        ) {
                            val conditionIcon = when (cache?.weatherCode) {
                                0, 1 -> R.drawable.ic_widget_sunny
                                2, 3, 45, 48 -> R.drawable.ic_widget_partly_cloudy
                                else -> R.drawable.ic_widget_cloudy // Fallback for Rainy/Stormy/Snowy
                            }

                            Image(
                                provider = ImageProvider(conditionIcon),
                                contentDescription = null,
                                modifier = GlanceModifier.size(46.dp)
                            )

                            Spacer(modifier = GlanceModifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.Vertical.CenterVertically,
                                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                            ) {
                                Image(
                                    provider = ImageProvider(R.drawable.ic_widget_location_pin),
                                    contentDescription = null,
                                    modifier = GlanceModifier.size(10.dp)
                                )
                                Spacer(modifier = GlanceModifier.width(4.dp))
                                
                                // Logic: Use cache name unless it is generic, then use current geocoded fallback
                                val cityNameToShow = when (cache?.cityName) {
                                    null, "My Location", "Current Location", "Current", "Cache Mapping Container" -> fallbackName
                                    else -> cache.cityName
                                }
                                
                                Text(
                                    text = cityNameToShow,
                                    style = TextStyle(color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun mapWmoCodeToCondition(code: Int): String {
        return when (code) {
            0 -> "Sunny"
            1 -> "Mainly Clear"
            2 -> "Partly Cloudy"
            3 -> "Overcast"
            45, 48 -> "Foggy"
            51, 53, 55 -> "Drizzle"
            61, 63, 65 -> "Rainy"
            66, 67 -> "Freezing Rain"
            71, 73, 75 -> "Snowy"
            77 -> "Snow Grains"
            80, 81, 82 -> "Rain Showers"
            85, 86 -> "Snow Showers"
            95, 96, 99 -> "Thunderstorm"
            else -> "Sunny"
        }
    }
}
