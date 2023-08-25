package com.radius.weather.domain

import com.radius.weather.data.repositoryImpl.WeatherDetailsRepositoryImpl
import com.radius.weather.data.repositoryImpl.WeatherHomeRepositoryImpl
import com.radius.weather.domain.repository.WeatherDetailsRepository
import com.radius.weather.domain.repository.WeatherHomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ViewModelComponent {

    @Binds
    abstract fun provideWeatherDetailRepository(weatherDetailsRepositoryImpl: WeatherDetailsRepositoryImpl) : WeatherDetailsRepository

    @Binds
    abstract fun provideWeatherHomeRepository(weatherHomeRepositoryImpl: WeatherHomeRepositoryImpl) : WeatherHomeRepository

}