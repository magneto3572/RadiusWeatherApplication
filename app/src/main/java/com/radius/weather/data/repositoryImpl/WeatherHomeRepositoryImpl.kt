package com.radius.weather.data.repositoryImpl

import com.radius.weather.data.Resource
import com.radius.weather.data.apiCall.SafeApiCall
import com.radius.weather.data.apiCall.ApiInterface
import com.radius.weather.data.localdb.Weather
import com.radius.weather.data.localdb.WeatherRecordDao
import com.radius.weather.domain.repository.WeatherHomeRepository
import com.radius.weather.domain.utils.Constants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class WeatherHomeRepositoryImpl @Inject constructor(
    private val api: ApiInterface,
    private val dao: WeatherRecordDao
): WeatherHomeRepository(), SafeApiCall {

    override suspend fun getSearchResult(query: String) = safeApiCall{
        api.getSearchResult(Constants.key, query)
    }

    override fun getAllDataFromLocalDb(): Flow<List<Weather>> {
       return dao.getAll()
    }

    override suspend fun removeFromLocalDb(weather: Weather) {
        dao.delete(weather)
    }
}