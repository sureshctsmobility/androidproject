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

        // Source of Truth: Pull the absolute latest entry updated by the app
        val currentCache = weatherDao.getAbsoluteLatestCache()

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
                            // Robust Icon Selection
                            val conditionIcon = when (cache?.weatherCode) {
                                0, 1 -> R.drawable.ic_widget_sunny
                                2, 3 -> R.drawable.ic_widget_partly_cloudy
                                45, 48 -> R.drawable.ic_widget_cloudy
                                in 51..82 -> R.drawable.ic_widget_partly_cloudy // Use Rain/Partly Cloudy icon
                                in 95..99 -> R.drawable.ic_widget_cloudy // Use Storm icon if available, fallback to Cloudy
                                else -> R.drawable.ic_widget_cloudy
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
            in 51..55 -> "Drizzle"
            in 61..67 -> "Rainy"
            in 71..77 -> "Snowy"
            in 80..82 -> "Rain Showers"
            in 85..86 -> "Snow Showers"
            in 95..99 -> "Thunderstorm"
            else -> "Sunny"
        }
    }
}
