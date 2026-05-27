package com.example.skycastproject.core.di

import android.content.Context
import androidx.room.Room
import com.example.skycastproject.data.local.SkyCastDatabase
import com.example.skycastproject.data.local.dao.TrackedCityDao
import com.example.skycastproject.data.local.dao.WeatherDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabaseInstance(@ApplicationContext context: Context): SkyCastDatabase {
        return Room.databaseBuilder(
            context,
            SkyCastDatabase::class.java,
            "skycast_production_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideWeatherDao(db: SkyCastDatabase): WeatherDao = db.weatherDao()

    @Provides
    @Singleton
    fun provideTrackedCityDao(db: SkyCastDatabase): TrackedCityDao = db.trackedCityDao()
}

