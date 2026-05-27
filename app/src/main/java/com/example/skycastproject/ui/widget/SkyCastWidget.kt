package com.example.skycastproject.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.skycastproject.R
import com.example.skycastproject.core.location.LocationTracker
import com.example.skycastproject.data.local.SkyCastDatabase
import com.example.skycastproject.data.local.entity.WeatherCacheEntity
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
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

        // IMPROVEMENT: Instead of re-geocoding (which fails in background), 
        // we take the LATEST entry saved by the app. This ensures 36 deg vs 37 deg match 
        // and "Siruseri" name consistency.
        val currentCache = weatherDao.getLatestSpecificCityCache() 
            ?: weatherDao.getWeatherCacheByCity("My Location")

        provideContent {
            WidgetLayout(currentCache)
        }
    }

    @Composable
    private fun WidgetLayout(cache: WeatherCacheEntity?) {
        val baseBlue = ColorProvider(androidx.compose.ui.graphics.Color(0xFF4F86F7))
        val textColor = ColorProvider(androidx.compose.ui.graphics.Color(0xFFFFFFFF))
        val subTextColor = ColorProvider(androidx.compose.ui.graphics.Color(0xCCFFFFFF))

        val calendar = Calendar.getInstance()
        val timeString = SimpleDateFormat("h a EEEE", Locale.getDefault()).format(calendar.time).uppercase()

        // REDUCED HEIGHT: Using smaller paddings and tighter layout
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(baseBlue)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                // LEFT COLUMN: Metrics
                Column(
                    modifier = GlanceModifier.defaultWeight(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Text(
                        text = timeString,
                        style = TextStyle(color = subTextColor, fontSize = 10.sp)
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
                        text = if (cache != null) getCondition(cache.weatherCode) else "Updating...",
                        style = TextStyle(color = subTextColor, fontSize = 12.sp)
                    )
                }

                // RIGHT COLUMN: Icon and Resolved Name
                Column(
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    val iconRes = if (cache != null) getIcon(cache.weatherCode) else R.drawable.ic_widget_cloudy
                    Image(
                        provider = ImageProvider(iconRes),
                        contentDescription = null,
                        modifier = GlanceModifier.size(42.dp)
                    )
                    
                    Spacer(modifier = GlanceModifier.height(2.dp))

                    Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
                        Image(
                            provider = ImageProvider(R.drawable.ic_widget_location_pin),
                            contentDescription = null,
                            modifier = GlanceModifier.size(10.dp)
                        )
                        Spacer(modifier = GlanceModifier.width(4.dp))
                        Text(
                            text = cache?.cityName ?: "Location",
                            style = TextStyle(color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }

    private fun getCondition(code: Int): String = when (code) {
        0 -> "Clear"
        1, 2 -> "Partly Cloudy"
        3 -> "Overcast"
        else -> "Cloudy"
    }

    private fun getIcon(code: Int): Int = when (code) {
        0 -> R.drawable.ic_widget_sunny
        1, 2, 3 -> R.drawable.ic_widget_partly_cloudy
        else -> R.drawable.ic_widget_cloudy
    }
}
