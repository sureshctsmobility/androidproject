package com.example.skycastproject.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.skycastproject.MainActivity
import com.example.skycastproject.core.location.LocationTracker
import com.example.skycastproject.data.local.SkyCastDatabase
import com.example.skycastproject.data.local.entity.WeatherCacheEntity
import com.example.skycastproject.domain.repository.WeatherRepository
import com.example.skycastproject.ui.widget.SkyCastWidget
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@HiltWorker
class DailyWeatherWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker,
    private val database: SkyCastDatabase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d("DailyWeatherWorker", "Background synchronization task started.")

            val location = locationTracker.getCurrentLocationCoordinates()
                ?: return@withContext Result.retry()

            val res = repository.fetchDirectStaticForecast(location.latitude, location.longitude)

            val cityName = try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val address = addresses?.firstOrNull()
                // Standardized priority used across App and Worker
                address?.subLocality ?: address?.locality ?: address?.subAdminArea ?: "My Location"
            } catch (e: Exception) { 
                "My Location"
            }

            val entity = WeatherCacheEntity(
                cityName = cityName,
                latitude = location.latitude,
                longitude = location.longitude,
                temperature = res.currentTemp.toDouble(),
                apparentTemperature = res.apparentTemp.toDouble(),
                humidity = res.humidity.filter { it.isDigit() }.toIntOrNull() ?: 0,
                windSpeed = res.windSpeed.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0,
                weatherCode = res.weatherCode,
                uvIndex = res.uvIndex,
                hourlyTimes = res.hourlyItems.joinToString(",") { it.time },
                hourlyTemps = res.hourlyItems.joinToString(",") { it.temp.toString() },
                hourlyCodes = res.hourlyItems.joinToString(",") { it.code.toString() },
                dailyTimes = res.dailyItems.joinToString(",") { it.dayName },
                dailyHighs = res.dailyItems.joinToString(",") { it.maxTemp.toString() },
                dailyLows = res.dailyItems.joinToString(",") { it.minTemp.toString() },
                dailyCodes = res.dailyItems.joinToString(",") { it.code.toString() },
                lastUpdatedEpochMillis = System.currentTimeMillis()
            )

            // Sync database cache
            database.weatherDao().insertWeatherCache(entity)
            database.weatherDao().insertWeatherCache(entity.copy(cityName = "My Location"))

            // Trigger Widget Update
            SkyCastWidget().updateAll(context)

            val advice = "Temp is ${res.currentTemp}°C. ${if (res.uvIndex >= 5.0) "High UV—apply sunscreen." else "Weather conditions are normal."}"
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.createNotificationChannel(
                    NotificationChannel("skycast_weather_ch", "Weather Updates", NotificationManager.IMPORTANCE_HIGH)
                )
            }

            val intent = Intent(context, MainActivity::class.java)
            val pending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

            val builder = NotificationCompat.Builder(context, "skycast_weather_ch")
                .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
                .setContentTitle("Morning Weather Update")
                .setContentText(advice)
                .setAutoCancel(true)
                .setContentIntent(pending)
            manager.notify(1001, builder.build())

            NotificationScheduler.scheduleNextRun(context)
            Result.success()
        } catch(e: Exception) {
            Log.e("DailyWeatherWorker", "Automatic background execution failed", e)
            Result.retry()
        }
    }
}
