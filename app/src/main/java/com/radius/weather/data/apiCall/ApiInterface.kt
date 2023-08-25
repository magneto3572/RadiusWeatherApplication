package com.radius.weather.data.apiCall

import com.radius.weather.domain.model.ForecastResponse
import com.radius.weather.domain.model.SearchLocationResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface ApiInterface {

    @GET("v1/search.json")
    suspend fun getSearchResult(@Header("key") key : String, @Query("q") query: String) : SearchLocationResponse

    @GET("/v1/forecast.json")
    suspend fun getForecastResult(@Header("key") key: String,
                                  @Query("q") q: String,
                                  @Query("days") days: Int,
                                  @Query("aqi") aqi: String,
                                  @Query("alerts") alerts: String) : ForecastResponse

}