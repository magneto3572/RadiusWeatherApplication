package com.radius.weather.domain.repository

import com.radius.weather.data.Resource
import com.radius.weather.data.localdb.Weather
import com.radius.weather.domain.model.SearchLocationResponse
import kotlinx.coroutines.flow.Flow

abstract class WeatherHomeRepository {

    abstract suspend fun getSearchResult(query: String): Resource<SearchLocationResponse>

    abstract fun getAllDataFromLocalDb() : Flow<List<Weather>>

    abstract suspend fun removeFromLocalDb(weather: Weather)
}