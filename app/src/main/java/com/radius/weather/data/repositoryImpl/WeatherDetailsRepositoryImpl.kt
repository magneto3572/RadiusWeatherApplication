package com.radius.weather.data.repositoryImpl

import android.util.Log
import com.radius.weather.data.apiCall.ApiInterface
import com.radius.weather.data.apiCall.SafeApiCall
import com.radius.weather.data.localdb.Weather
import com.radius.weather.data.localdb.WeatherRecordDao
import com.radius.weather.domain.repository.WeatherDetailsRepository
import com.radius.weather.domain.utils.Constants
import javax.inject.Inject


class WeatherDetailsRepositoryImpl @Inject constructor(
    private val api: ApiInterface,
    private val weatherRecordDao: WeatherRecordDao
) : WeatherDetailsRepository(), SafeApiCall {

    override suspend fun getForecastFromApi(q: String, days: Int, aqi: String, alerts: String) = safeApiCall{
        api.getForecastResult(Constants.key, q, days, aqi, alerts)
    }

    override suspend fun insertWeatherDetailsIntoLocalDb(weather: Weather) {
        Log.d("LogTag", "cameHERE")
        weatherRecordDao.insert(weather)
    }
}