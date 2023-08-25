package com.radius.weather.domain.repository

import com.radius.weather.data.Resource
import com.radius.weather.data.localdb.Weather
import com.radius.weather.domain.model.ForecastResponse


abstract class WeatherDetailsRepository {

    abstract suspend fun getForecastFromApi(q : String, days : Int, aqi: String, alerts: String): Resource<ForecastResponse>

    abstract suspend fun insertWeatherDetailsIntoLocalDb(weather: Weather)
}