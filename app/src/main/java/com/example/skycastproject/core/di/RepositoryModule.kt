package com.example.skycastproject.core.di

import com.example.skycastproject.data.repository.WeatherRepositoryImpl
import com.example.skycastproject.domain.repository.WeatherRepository
import dagger.Module
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepositoryContract(impl: WeatherRepositoryImpl): WeatherRepository
}